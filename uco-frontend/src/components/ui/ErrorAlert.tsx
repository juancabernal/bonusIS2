import type { ReactNode } from 'react'
import styles from './ErrorAlert.module.css'

interface ErrorAlertProps {
  title?: string
  message?: string
  children?: ReactNode
  actions?: ReactNode
}

const ErrorAlert = ({ title = 'Ha ocurrido un error', message, children, actions }: ErrorAlertProps) => {
  return (
    <section className={styles.alert} role="alert" aria-live="assertive">
      <div className={styles.header}>
        <span className={styles.icon} aria-hidden>
          !
        </span>
        <div className={styles.content}>
          {title ? <h2 className={styles.title}>{title}</h2> : null}
          {message ? <p className={styles.message}>{message}</p> : null}
          {children}
        </div>
      </div>
      {actions ? <div className={styles.actions}>{actions}</div> : null}
    </section>
  )
}

export default ErrorAlert
