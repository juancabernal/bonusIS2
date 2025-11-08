import { Link } from 'react-router-dom'
import { useAuth0 } from '@auth0/auth0-react'
import LogoutButton from '../components/LogoutButton'

const Dashboard = () => {
  const { user } = useAuth0()
  const displayName = user?.name ?? user?.email ?? 'Usuario'

  return (
    <main className="page dashboard-page">
      <header className="page-header">
        <div>
          <h1>Panel de control</h1>
          <p>
            Visualiza la salud operativa de Uco Challenge y accede rápidamente a los flujos críticos del
            ecosistema.
          </p>
        </div>
        <div className="page-actions">
          <Link to="/users/new" className="btn btn-accent">
            Registrar nuevo usuario
          </Link>
          <LogoutButton className="btn btn-secondary" />
        </div>
      </header>

      <section className="card-grid" style={{ marginBottom: '2.5rem' }}>
        <article className="card card--accent">
          <span className="badge" aria-label="Cuenta activa">
            ✅ Sesión activa
          </span>
          <h2 style={{ marginBottom: '0.75rem' }}>Hola, {displayName}</h2>
          <p>
            Tu sesión Auth0 está sincronizada y lista para administrar usuarios, validaciones y catálogos
            oficiales desde un solo lugar.
          </p>
        </article>

        <article className="card" style={{ background: 'rgba(59, 130, 246, 0.18)' }}>
          <span className="metric-title">Usuarios totales</span>
          <p className="metric-value">—</p>
          <p style={{ margin: 0 }}>
            Consulta el detalle completo en la vista de usuarios para acceder a verificaciones y acciones
            rápidas.
          </p>
        </article>

        <article className="card" style={{ background: 'rgba(56, 189, 248, 0.18)' }}>
          <span className="metric-title">Contactos verificados</span>
          <p className="metric-value">—</p>
          <p style={{ margin: 0 }}>
            Mantén correos y móviles confiables con el flujo de código OTP integrado en el sistema.
          </p>
        </article>
      </section>

      <section className="card-grid">
        <article className="card">
          <h3>Acciones rápidas</h3>
          <ul>
            <li>Explora el listado de usuarios y filtra por verificación.</li>
            <li>Registra nuevos perfiles con validaciones en tiempo real.</li>
            <li>Refresca catálogos de identificación y ubicación cuando sea necesario.</li>
          </ul>
          <div className="card-actions card-actions--start">
            <Link to="/users" className="btn btn-primary">
              Ver usuarios registrados
            </Link>
          </div>
        </article>

        <article className="card">
          <h3>Soporte y monitoreo</h3>
          <p>
            ¿Dudas con la verificación o con los microservicios? Documentamos cada flujo. Comunícate con
            soporte@uco.edu.co para escalamientos inmediatos.
          </p>
          <div className="card-actions card-actions--start">
            <Link to="/" className="btn btn-outline">
              Volver al inicio
            </Link>
          </div>
        </article>

        <article className="card" style={{ background: 'rgba(148, 163, 184, 0.15)' }}>
          <h3>Próximos lanzamientos</h3>
          <p>
            Roadmap preparado para dashboards analíticos, filtros avanzados y auditoría en tiempo real.
            Mantente atento a las próximas iteraciones.
          </p>
        </article>
      </section>
    </main>
  )
}

export default Dashboard
