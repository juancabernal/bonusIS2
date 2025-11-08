import { Link } from 'react-router-dom'
import { useAuth0 } from '@auth0/auth0-react'
import LogoutButton from '../components/LogoutButton'

const featureCards = [
  {
    title: 'Operaciones bajo control',
    description:
      'Monitorea usuarios, verificaciones y catálogos clave desde una consola unificada con datos al instante.',
    icon: '🛰️',
  },
  {
    title: 'Seguridad empresarial',
    description:
      'Protección Auth0 end-to-end, sesiones seguras y flujos de verificación que refuerzan la confianza.',
    icon: '🛡️',
  },
  {
    title: 'Ecosistema conectado',
    description:
      'Integración directa con microservicios UCO. Consulta identificaciones, ubicaciones y más sin salir del panel.',
    icon: '🔗',
  },
  {
    title: 'Listo para escalar',
    description:
      'Arquitectura cloud-native, métricas claras y experiencias responsivas para equipos ágiles.',
    icon: '🚀',
  },
]

const Home = () => {
  const { isAuthenticated, user } = useAuth0()
  const displayName = user?.name ?? user?.email ?? 'invitado'

  return (
    <main className="page home-page">
      <section className="home-hero">
        <div>
          <span className="home-hero__badge">👋 Hola {isAuthenticated ? displayName : 'bienvenido'}</span>
          <h1 className="home-hero__title">Gestiona Uco Challenge con precisión milimétrica</h1>
          <p className="home-hero__subtitle">
            Una experiencia administrativa pensada para startups de alto crecimiento: oscura, elegante y
            con la potencia que tu operación necesita para mantenerse sincronizada.
          </p>
          <div className="home-hero__actions">
            {isAuthenticated ? (
              <>
                <Link to="/dashboard" className="btn btn-primary">
                  Ir al panel de control
                </Link>
                <LogoutButton className="btn btn-secondary" />
              </>
            ) : (
              <Link to="/login" className="btn btn-primary">
                Iniciar sesión
              </Link>
            )}
            <Link to="/users" className="btn btn-outline">
              Ver usuarios
            </Link>
          </div>
        </div>
        <div className="home-hero__illustration" aria-hidden="true" />
      </section>

      <section className="feature-grid">
        {featureCards.map((feature) => (
          <article key={feature.title} className="card feature-card">
            <span aria-hidden style={{ fontSize: '1.75rem' }}>
              {feature.icon}
            </span>
            <h3>{feature.title}</h3>
            <p>{feature.description}</p>
          </article>
        ))}
      </section>

      <section className="card" style={{ marginTop: '3rem' }}>
        <header className="page-header" style={{ marginBottom: '1.5rem' }}>
          <div>
            <h2 style={{ margin: 0 }}>Conectado con la Universidad Cooperativa de Colombia</h2>
            <p>
              Integraciones vivas con catálogos de identificación, ubicaciones y validaciones de contacto
              para garantizar datos confiables en cada registro.
            </p>
          </div>
        </header>

        <div className="card-grid">
          <article className="card" style={{ background: 'rgba(15, 118, 110, 0.2)' }}>
            <span className="metric-title">Integraciones clave</span>
            <p className="metric-value">+6</p>
            <p style={{ margin: 0 }}>Microservicios sincronizados en tiempo real mediante el API Gateway.</p>
          </article>
          <article className="card" style={{ background: 'rgba(59, 130, 246, 0.18)' }}>
            <span className="metric-title">Verificaciones seguras</span>
            <p className="metric-value">OTP</p>
            <p style={{ margin: 0 }}>Flujos de email y SMS con feedback inmediato para usuarios confiables.</p>
          </article>
          <article className="card" style={{ background: 'rgba(124, 58, 237, 0.2)' }}>
            <span className="metric-title">Disponibilidad</span>
            <p className="metric-value">24/7</p>
            <p style={{ margin: 0 }}>Arquitectura preparada para escalar sin interrumpir la operación.</p>
          </article>
        </div>
      </section>
    </main>
  )
}

export default Home
