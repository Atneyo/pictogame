import { useEffect, useRef, useState } from "react";
import { getWords } from "../services/api.ts";
import { getErrorMessage } from "../services/errorMessages.ts";
import type { Word } from "../types/word.ts";

export function useRandomWords() {
  const [words, setWords] = useState<Word[]>([]);
  const [isLoading, setIsLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const abortRef = useRef<AbortController | null>(null);

  async function loadWords() {
    // Cancel any request still in flight: only the latest one counts
    abortRef.current?.abort();
    const controller = new AbortController();
    abortRef.current = controller;

    setIsLoading(true);
    setError(null);
    try {
      setWords(await getWords(undefined, controller.signal));
    } catch (e) {
      // A cancelled request is expected, not an error to display
      if (e instanceof DOMException && e.name === "AbortError") return;
      setError(getErrorMessage(e));
    } finally {
      if (abortRef.current === controller) setIsLoading(false);
    }
  }

  // Cancel the pending request when the page is left
  useEffect(() => {
    return () => {
      abortRef.current?.abort();
    };
  }, []);

  return { words, isLoading, error, loadWords };
}
