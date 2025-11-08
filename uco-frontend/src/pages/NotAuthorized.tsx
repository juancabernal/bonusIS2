import { Link } from 'react-router-dom'

const NotAuthorized = () => (
  <main className="page">
    <section className="card empty-state" role="alert">
      <span className="status-icon" aria-hidden>
        🔐
      </span>
      <div>
        <h1>No autorizado</h1>
        <p>No tienes permisos para acceder a esta sección.</p>
        <p>Si crees que es un error, contacta al administrador del sistema.</p>
      </div>
      <Link to="/" className="btn btn-primary">
        Ir al inicio
      </Link>
    </section>
  </main>
)

export default NotAuthorized
