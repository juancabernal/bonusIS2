import { Link } from 'react-router-dom'
import { useAuth0 } from '@auth0/auth0-react'
import LogoutButton from '../components/LogoutButton'
import styles from './Dashboard.module.css'

const Dashboard = () => {
  const { user } = useAuth0()
  const displayName = user?.name ?? user?.email ?? 'Usuario'

  return (
    <main className={`page ${styles.dashboard}`}>
      <header className={styles.header}>
        <div className={styles.headerText}>
          <h1>Panel operativo</h1>
          <p>
            Supervisa la actividad del ecosistema UCO y accede a las acciones clave para mantener datos y
            verificaciones alineadas.
          </p>
        </div>
        <div className={styles.headerActions}>
          <Link to="/users/new" className="button button--primary">
            Registrar usuario
          </Link>
          <LogoutButton className="button button--ghost" />
        </div>
      </header>

      <section className={styles.metrics}>
        <article className={styles.metricCard}>
          <span className={styles.metricBadge}>Sesión activa</span>
          <strong>{displayName}</strong>
          <p>Tu sesión Auth0 está vigente y lista para interactuar con el API Gateway.</p>
        </article>
        <article className={styles.metricCard}>
          <span className={styles.metricBadge} style={{ borderColor: 'rgba(108, 99, 255, 0.35)', background: 'rgba(108, 99, 255, 0.12)', color: '#c7d2fe' }}>
            Usuarios
          </span>
          <strong>—</strong>
          <p>Consulta el detalle completo en la vista de usuarios y aplica filtros inmediatos.</p>
        </article>
        <article className={styles.metricCard}>
          <span className={styles.metricBadge} style={{ borderColor: 'rgba(250, 204, 21, 0.3)', background: 'rgba(250, 204, 21, 0.12)', color: '#fde68a' }}>
            Contactos
          </span>
          <strong>OTP</strong>
          <p>Confirma correos y móviles mediante códigos de verificación enviados desde la consola.</p>
        </article>
      </section>

      <section className={styles.sections}>
        <article className={styles.sectionCard}>
          <h3>Acciones rápidas</h3>
          <ul className={styles.sectionList}>
            <li>Explora el directorio de usuarios y verifica sus estados de contacto.</li>
            <li>Registra nuevos perfiles con catálogos de documento y ubicación actualizados.</li>
            <li>Vuelve a enviar códigos de confirmación cuando sea necesario.</li>
          </ul>
          <div className={styles.sectionFooter}>
            <Link to="/users" className="button button--secondary">
              Ir al listado de usuarios
            </Link>
          </div>
        </article>

        <article className={styles.sectionCard}>
          <h3>Monitoreo continuo</h3>
          <p>
            Mantén la trazabilidad de los microservicios gracias al gateway. Si detectas incidentes,
            contáctanos para escalamientos.
          </p>
          <div className={styles.sectionFooter}>
            <Link to="/" className="button button--ghost">
              Volver a inicio
            </Link>
          </div>
        </article>

        <article className={styles.sectionCard}>
          <h3>Próximas mejoras</h3>
          <p>
            Estamos preparando vistas analíticas, auditoría de eventos y reportes descargables para ampliar
            la gobernanza del sistema.
          </p>
        </article>
      </section>
    </main>
  )
}

export default Dashboard
