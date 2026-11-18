import { useId, useState } from "react";
import { addWord } from "../services/api";
import Button from "./../components/Button";
import styles from "./AddWordPage.module.css";
import { getErrorMessage } from "../services/errorMessages";

export default function AddWordPage() {
  const [word, setWord] = useState("");
  const [lastWordAdded, setLastWordAdded] = useState("");
  const [error, setError] = useState("");
  const wordId = useId();

  async function newWord() {
    setError("");
    try {
      const wordAdded = await addWord(word);
      setLastWordAdded(wordAdded.label);
    } catch (e) {
      setError(getErrorMessage(e));
    }
  }

  return (
    <section className={styles.page}>
      <div className={styles.leftPart}>
        <h1>Ajouter un mot</h1>
        <p className={styles.intro}>
          Ecrivez un mot puis ajoutez le à la liste de mots disponibles pour les dictées. Visualisez
          la liste des mots disponibles sur la droite.
        </p>
        <div className={styles.addWordBox}>
          <div>
            <label className={styles.wordLabel} htmlFor={wordId}>
              Mot à ajouter
            </label>
            <input
              className={styles.wordInput}
              id={wordId}
              value={word}
              placeholder="écrivez le mot à ajouter"
              onChange={(e) => setWord(e.target.value)}
            />
          </div>
          {error && (
            <p className={styles.error} role="alert">
              {error}
            </p>
          )}
          <Button onClick={() => newWord()}>Ajouter</Button>
        </div>
        {lastWordAdded && <p>Dernier mot ajouté : {lastWordAdded}.</p>}
        {/* // ajouter les trois derniers mots ? */}
      </div>
      <div className={styles.rightPart}>
        <p>
          // nombre total de mots // Bouton afficher la liste des mots/ cacher la liste des mots //
          liste des mots
        </p>
      </div>
    </section>
  );
}
