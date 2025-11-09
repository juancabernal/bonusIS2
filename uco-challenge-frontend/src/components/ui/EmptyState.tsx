import type { ReactNode } from 'react'
import styles from './EmptyState.module.css'

interface EmptyStateProps {
  title?: string
  description?: string
  actions?: ReactNode
}

const EmptyState = ({ title = 'No hay usuarios', description, actions }: EmptyStateProps) => {
  return (
    <section className={styles.emptyState} aria-live="polite">
      <span className={styles.icon} aria-hidden>
        📄
      </span>
      <h2 className={styles.title}>{title}</h2>
      {description ? <p className={styles.description}>{description}</p> : null}
      {actions ? <div className={styles.actions}>{actions}</div> : null}
    </section>
  )
}

export default EmptyState
