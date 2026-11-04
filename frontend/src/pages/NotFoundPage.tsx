import { Link } from "react-router";
import styles from "./NotFoundPage.module.css";

export default function NotFoundPage() {
  return (
    <section className={styles.container}>
      <h1>Page introuvable</h1>
      <p>L'adresse demandée n'existe pas.</p>
      <Link to="/" className={styles.link}>
        Retour à l'accueil
      </Link>
    </section>
  );
}
