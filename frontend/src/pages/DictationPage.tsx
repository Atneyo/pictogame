import { useMemo, useState } from "react";
import Button from "../components/Button.tsx";
import { useRandomWords } from "../hooks/useRandomWords";
import { useWordReader } from "../hooks/useWordReader.ts";
import styles from "./DictationPage.module.css";
import NumberParameter from "../components/NumberParameter.tsx";

// see backend/src/main/java/chloe/pictogame/common/WordConstants.java
export const DEFAULT_WORD_COUNT = "20";
export const WORD_MIN = 1;
export const WORD_MAX = 50;

export const DEFAULT_DELAY_S = "3";

export default function DictationPage() {
  const [countString, setCountString] = useState(DEFAULT_WORD_COUNT);
  const count = Number(countString);

  const [delaySString, setDelaySString] = useState(DEFAULT_DELAY_S);
  const delayS = Number(delaySString);

  const { words, isLoading, error, loadWords } = useRandomWords();
  const labels = useMemo(() => words.map((word) => word.label), [words]);
  const { status, currentWord, isSupported, start, pause, resume, stop } = useWordReader(labels, {
    delayMs: delayS * 1000,
  });

  // Dictation screen: only the word being spoken, centered
  if (status !== "idle") {
    return (
      <section className={styles.stage}>
        <div className={styles.wordArea} aria-live="polite">
          {currentWord && <p className={styles.currentWord}>{currentWord}</p>}
          {status === "paused" && <p className={styles.pauseLabel}>En pause</p>}
        </div>
        <div className={styles.actions}>
          {status === "running" ? (
            <Button onClick={pause}>Pause</Button>
          ) : (
            <Button onClick={resume}>Reprendre</Button>
          )}
          <Button variant="secondary" onClick={stop}>
            Arrêter
          </Button>
        </div>
      </section>
    );
  }

  // Base screen
  const hasWords = words.length > 0;
  const loadLabel = isLoading
    ? "Chargement…"
    : hasWords
      ? "Changer la liste"
      : "Récupérer une liste de mots";

  return (
    <section className={styles.page}>
      <h1>Dictée</h1>
      <p className={styles.intro}>
        Récupérez une liste de mots, puis lancez la dictée : chaque mot s'affiche au centre de
        l'écran pendant qu'il est prononcé.
      </p>

      <div className={styles.parameters}>
        <h2>Paramètres</h2>
        <NumberParameter
          label="Nombre de mots"
          value={countString}
          updateValue={setCountString}
          step={10}
          min={WORD_MIN}
          max={WORD_MAX}
        />
        <NumberParameter
          label="Délai lors de la dictée"
          value={delaySString}
          updateValue={setDelaySString}
          step={1}
          min={0}
        />
      </div>

      {!isSupported && (
        <p className={styles.error} role="alert">
          Votre navigateur ne gère pas la synthèse vocale.
        </p>
      )}
      {error && (
        <p className={styles.error} role="alert">
          {error}
        </p>
      )}

      <div className={styles.actions}>
        <Button
          variant={hasWords ? "secondary" : "primary"}
          onClick={() => loadWords(count)}
          disabled={isLoading}
        >
          {loadLabel}
        </Button>
        {hasWords && (
          <Button onClick={start} disabled={isLoading || !isSupported}>
            Lancer la dictée
          </Button>
        )}
      </div>

      {hasWords && (
        <div>
          <h2 className={styles.listTitle}>Votre liste ({words.length} mots)</h2>
          <ul className={styles.words}>
            {words.map((word) => (
              <li key={word.id} className={styles.word}>
                {word.label}
              </li>
            ))}
          </ul>
        </div>
      )}
    </section>
  );
}
