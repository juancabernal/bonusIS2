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
          <p>Administra usuarios y mantén tu organización sincronizada.</p>
        </div>
        <div className="page-actions">
          <Link to="/users/new" className="btn btn-accent">
            Registrar nuevo usuario
          </Link>
          <LogoutButton className="btn btn-secondary" />
        </div>
      </header>

      <section className="card-grid">
        <article className="card card--accent">
          <span className="badge" aria-label="Cuenta activa">
            ✅ Sesión activa
          </span>
          <h2 style={{ marginBottom: '0.5rem' }}>Hola, {displayName}</h2>
          <p>
            Tu cuenta está conectada correctamente. Desde este panel puedes revisar la lista completa de
            usuarios, registrar nuevos perfiles y actualizar la información existente.
          </p>
        </article>

        <article className="card">
          <h3>Próximos pasos</h3>
          <ul>
            <li>Consulta el listado de usuarios activos.</li>
            <li>Revisa los datos antes de realizar cambios.</li>
            <li>Apóyate en el registro guiado para nuevos usuarios.</li>
          </ul>
          <div className="card-actions card-actions--start">
            <Link to="/users" className="btn btn-primary">
              Ver usuarios registrados
            </Link>
          </div>
        </article>

        <article className="card">
          <h3>¿Necesitas ayuda?</h3>
          <p>
            El equipo de soporte está disponible para resolver tus dudas. Consulta la documentación interna
            o escribe a soporte@uco.edu.co.
          </p>
          <div className="card-actions card-actions--start">
            <Link to="/" className="btn btn-outline">
              Volver al inicio
            </Link>
          </div>
        </article>
      </section>
    </main>
  )
}

export default Dashboard
