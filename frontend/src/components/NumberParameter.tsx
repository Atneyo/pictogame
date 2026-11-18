import { useId } from "react";
import Button from "./Button.tsx";
import styles from "./NumberParameter.module.css";

interface NumberParameterProps {
  label: string;
  value: string;
  updateValue: (count: string) => void;
  step: number;
  min?: number;
  max?: number;
}

export default function NumberParameter({
  label,
  value,
  updateValue,
  step,
  min,
  max,
}: NumberParameterProps) {

  function check(s: string) {
    let n = Number(s);
    if (min !== undefined) n = Math.max(min, n);
    if (max !== undefined) n = Math.min(max, n);
    updateValue(n.toString());
  }

  const countId = useId();

  return (
    <div className={styles.parameter}>
      <label htmlFor={countId} className={styles.parameterLabel}>
        {label}
      </label>
      <Button
        size="small"
        aria-label={`Retirer ${step} mots`}
        className={styles.parameterButton}
        onClick={() =>
          updateValue(
            (min !== undefined
              ? Math.max(min, Number(value) - step)
              : Number(value) - step
            ).toString(),
          )
        }
      >
        −{step}
      </Button>
      <input
        id={countId}
        className={styles.parameterInput}
        type="number"
        value={value}
        min={min}
        max={max}
        onChange={(e) => updateValue(e.target.value)}
        onBlur={() => check(value)}
      />
      <Button
        size="small"
        aria-label={`Ajouter ${step} mots`}
        className={styles.parameterButton}
        onClick={() =>
          updateValue(
            (max !== undefined
              ? Math.min(max, Number(value) + step)
              : Number(value) + step
            ).toString(),
          )
        }
      >
        +{step}
      </Button>
    </div>
  );
}
