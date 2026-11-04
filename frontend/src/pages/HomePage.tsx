import { Link } from "react-router";
import styles from "./HomePage.module.css";

export default function HomePage() {
  return (
    <section className={styles.hero}>
      <h1 className={styles.title}>Dictée de mots</h1>
      <p className={styles.description}>
        Récupérez une liste de mots tirés au hasard, lancez la dictée et laissez l'ordinateur les
        prononcer à voix haute, un par un, à intervalle régulier.
      </p>
      <Link to="/dictation" className={styles.cta}>
        Commencer une dictée
      </Link>
    </section>
  );
}
