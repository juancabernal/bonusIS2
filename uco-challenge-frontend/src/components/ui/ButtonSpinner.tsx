import styles from './ButtonSpinner.module.css'

interface ButtonSpinnerProps {
  label?: string
}

const ButtonSpinner = ({ label = 'Procesando...' }: ButtonSpinnerProps) => {
  return (
    <span className={styles.root} role="status" aria-live="polite">
      <span className={styles.spinner} aria-hidden="true" />
      <span className="visually-hidden">{label}</span>
    </span>
  )
}

export default ButtonSpinner
