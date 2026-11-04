import { Link, Outlet } from "react-router";
import styles from "./Layout.module.css";

export default function Layout() {
  return (
    <div className={styles.layout}>
      <header className={styles.header}>
        <Link to="/" className={styles.brand}>
          Pictogame
        </Link>
      </header>
      <main className={styles.main}>
        {/* The current page is rendered here */}
        <Outlet />
      </main>
    </div>
  );
}
