import { Link } from 'react-router-dom'
import { useAuth0 } from '@auth0/auth0-react'
import LogoutButton from '../components/LogoutButton'
import styles from './Home.module.css'

const featureCards = [
  {
    title: 'Supervisión inmediata',
    description:
      'Consulta usuarios, catálogos y verificaciones en una sola vista sin sacrificar velocidad.',
    icon: '🧭',
  },
  {
    title: 'Seguridad Auth0',
    description:
      'Sesiones protegidas, scopes controlados y tokens gestionados automáticamente por el gateway.',
    icon: '🔒',
  },
  {
    title: 'Flujos de validación',
    description:
      'Envía códigos OTP y confirma contactos con retroalimentación inmediata para cada usuario.',
    icon: '📡',
  },
  {
    title: 'Catálogos oficiales',
    description:
      'Documentos y ubicaciones actualizadas directamente desde los microservicios institucionales.',
    icon: '🗂️',
  },
]

const Home = () => {
  const { isAuthenticated, user } = useAuth0()
  const displayName = user?.name ?? user?.email ?? 'invitado'

  return (
    <main className={`page ${styles.home}`}>
      <section className={styles.hero}>
        <div className={styles.heroContent}>
          <span className={styles.heroBadge}>
            👋 {isAuthenticated ? `Hola, ${displayName}` : 'Bienvenido a la consola'}
          </span>
          <h1 className={styles.heroTitle}>UCO Challenge Admin Console</h1>
          <p className={styles.heroSubtitle}>
            Observa el pulso operativo de la plataforma y ejecuta acciones críticas sobre usuarios y
            verificaciones con una interfaz sobria y enfocada.
          </p>
          <div className={styles.heroActions}>
            {isAuthenticated ? (
              <>
                <Link to="/dashboard" className="button button--primary">
                  Abrir panel
                </Link>
                <Link to="/users" className="button button--secondary">
                  Usuarios registrados
                </Link>
                <LogoutButton className="button button--ghost" />
              </>
            ) : (
              <>
                <Link to="/login" className="button button--primary">
                  Iniciar sesión
                </Link>
                <Link to="/users" className="button button--secondary">
                  Explorar usuarios
                </Link>
              </>
            )}
          </div>
        </div>

        <aside className={styles.heroPanel}>
          <div className={styles.heroPanelCard}>
            <h3>Universidad Cooperativa de Colombia</h3>
            <p>
              Administración centralizada del ecosistema UCO: usuarios, catálogos, ubicaciones y
              validaciones conectadas al API Gateway.
            </p>
          </div>
          <div className={styles.heroProgress}>
            <h4>Cadena operativa</h4>
            <ul>
              <li>Autenticación segura con Auth0 y scopes dedicados.</li>
              <li>Gateway gestionando microservicios y catálogos institucionales.</li>
              <li>Verificaciones con códigos OTP y seguimiento inmediato.</li>
            </ul>
          </div>
        </aside>
      </section>

      <section className={styles.featureSection}>
        <header>
          <h2>Capacidades destacadas</h2>
          <p>
            Una consola oscura y minimalista diseñada para que cada acción crítica esté a dos clics de
            distancia.
          </p>
        </header>
        <div className={styles.featureGrid}>
          {featureCards.map((feature) => (
            <article key={feature.title} className={styles.featureCard}>
              <span aria-hidden>{feature.icon}</span>
              <h3>{feature.title}</h3>
              <p>{feature.description}</p>
            </article>
          ))}
        </div>
      </section>
    </main>
  )
}

export default Home
