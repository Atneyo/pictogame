import type { ButtonHTMLAttributes } from "react";
import styles from "./Button.module.css";

type ButtonVariant = "primary" | "secondary";

interface ButtonProps extends ButtonHTMLAttributes<HTMLButtonElement> {
  variant?: ButtonVariant;
}

export default function Button({ variant = "primary", ...props }: ButtonProps) {
  // type="button" avoids accidental form submissions; callers can still override it
  return <button type="button" className={`${styles.button} ${styles[variant]}`} {...props} />;
}
