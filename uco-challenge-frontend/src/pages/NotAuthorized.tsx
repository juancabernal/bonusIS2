import { Link } from 'react-router-dom'
import styles from './NotAuthorized.module.css'

const NotAuthorized = () => (
  <main className={`page ${styles.page}`}>
    <section className={styles.card} role="alert" aria-live="assertive">
      <span className={styles.icon} aria-hidden>
        🔐
      </span>
      <div>
        <h1>No autorizado</h1>
        <p>No tienes permisos para acceder a esta sección.</p>
        <p>Si crees que es un error, contacta al administrador del sistema.</p>
      </div>
      <div className={styles.actions}>
        <Link to="/" className="button button--primary">
          Ir al inicio
        </Link>
      </div>
    </section>
  </main>
)

export default NotAuthorized
