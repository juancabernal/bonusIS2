import type { ReactNode } from 'react'
import styles from './LoadingSpinner.module.css'

interface LoadingSpinnerProps {
  label?: ReactNode
}

const LoadingSpinner = ({ label }: LoadingSpinnerProps) => {
  return (
    <div className={styles.container} role="status" aria-live="polite">
      <span className={styles.spinner} aria-hidden />
      {label ? <span className={styles.label}>{label}</span> : null}
    </div>
  )
}

export default LoadingSpinner
