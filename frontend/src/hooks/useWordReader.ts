import { useCallback, useEffect, useState } from "react";

export type ReaderStatus = "idle" | "running" | "paused";

// "speaking": the current word is being pronounced.
// "waiting": silence between the end of a word and the start of the next one.
type Phase = "speaking" | "waiting";

interface UseWordReaderOptions {
  /** Silence between the end of a word and the start of the next one, in ms. */
  delayMs?: number;
  /** BCP 47 language tag used by the browser to pick the voice. */
  lang?: string;
}

const isSupported = typeof window !== "undefined" && "speechSynthesis" in window;

export function useWordReader(
  words: string[],
  { delayMs = 3000, lang = "fr-FR" }: UseWordReaderOptions = {},
) {
  const [status, setStatus] = useState<ReaderStatus>("idle");
  const [phase, setPhase] = useState<Phase>("speaking");
  const [index, setIndex] = useState(0);
  // Snapshot of the words taken at start(): changing the list mid-dictation has no effect
  const [queue, setQueue] = useState<string[]>([]);

  // Needs a stable identity: it is a dependency of the effect below.
  // Without useCallback, the effect would restart (cutting the speech) on every render.
  const reset = useCallback(() => {
    setStatus("idle");
    setPhase("speaking");
    setIndex(0);
  }, []);

  function start() {
    if (!isSupported || words.length === 0) return;
    setQueue(words);
    setIndex(0);
    setPhase("speaking");
    setStatus("running");
  }

  function pause() {
    setStatus((current) => (current === "running" ? "paused" : current));
  }

  function resume() {
    setStatus((current) => (current === "paused" ? "running" : current));
  }

  // The whole dictation is driven by this effect. Pausing changes `status`, which runs the
  // cleanup (cancels speech and timer); resuming runs the effect again for the same word.
  useEffect(() => {
    if (status !== "running") return undefined;

    if (phase === "waiting") {
      const timeoutId = window.setTimeout(() => {
        setIndex((current) => current + 1);
        setPhase("speaking");
      }, delayMs);
      return () => window.clearTimeout(timeoutId);
    }

    const synth = window.speechSynthesis;
    const utterance = new SpeechSynthesisUtterance(queue[index]);
    utterance.lang = lang;

    const handleDone = () => {
      if (index >= queue.length - 1) {
        reset(); // last word spoken: back to the base screen
      } else {
        setPhase("waiting");
      }
    };
    utterance.onend = handleDone;
    utterance.onerror = handleDone; // a failed word must not freeze the dictation

    synth.speak(utterance);

    return () => {
      // Detach handlers first: cancelling would otherwise trigger them
      utterance.onend = null;
      utterance.onerror = null;
      if (synth.speaking || synth.pending) synth.cancel();
    };
  }, [status, phase, index, queue, lang, delayMs, reset]);

  // The word is only exposed while it is being spoken
  const currentWord = status !== "idle" && phase === "speaking" ? (queue[index] ?? null) : null;

  return { status, currentWord, isSupported, start, pause, resume, stop: reset };
}
