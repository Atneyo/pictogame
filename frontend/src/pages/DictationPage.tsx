import { useMemo, useState } from "react";
import Button from "../components/Button.tsx";
import { useRandomWords } from "../hooks/useRandomWords";
import { useWordReader } from "../hooks/useWordReader.ts";
import styles from "./DictationPage.module.css";

export const DEFAULT_WORD_COUNT = 20;

export default function DictationPage() {
  const { words, isLoading, error, loadWords } = useRandomWords();
  const labels = useMemo(() => words.map((word) => word.label), [words]);
  const { status, currentWord, isSupported, start, pause, resume, stop } = useWordReader(labels);

  const [count, setCount] = useState(DEFAULT_WORD_COUNT);

  function setCountHandleNaN(c: number) {
    if (Number.isNaN(c)) {
      setCount(0);
    } else {
      setCount(c);
    }
  }

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
        <div className={styles.parameter}>
          <label htmlFor="countId" className={styles.parameterLabel}>
            Nombre de mots
          </label>
          <Button
            size="small"
            aria-label="Retirer 5 mots"
            onClick={() => setCount(Math.max(0, count - 5))}
          >
            −5
          </Button>
          <input
            id="countId"
            className={styles.parameterInput}
            type="number"
            value={count}
            min={0}
            onChange={(e) => setCountHandleNaN(Number.parseInt(e.target.value))}
          />
          <Button size="small" aria-label="Ajouter 5 mots" onClick={() => setCount(count + 5)}>
            +5
          </Button>
        </div>
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
