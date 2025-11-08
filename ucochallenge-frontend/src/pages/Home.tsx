import { Link } from 'react-router-dom'
import { useAuth0 } from '@auth0/auth0-react'
import LogoutButton from '../components/LogoutButton'

const featureCards = [
  {
    title: 'Gestión centralizada',
    description: 'Administra los usuarios de manera ágil con un panel intuitivo y accesible.',
  },
  {
    title: 'Roles y permisos seguros',
    description: 'Mantén el control con accesos protegidos por Auth0 y políticas claras.',
  },
  {
    title: 'Integración lista para usar',
    description: 'Conecta con los servicios de la UCO y obtén información en tiempo real.',
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
          <h1 className="home-hero__title">Plataforma administrativa UCO</h1>
          <p className="home-hero__subtitle">
            Gestiona usuarios, mantén los permisos bajo control y supervisa toda la operación desde un
            panel moderno pensado para la productividad.
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
            <h3>{feature.title}</h3>
            <p>{feature.description}</p>
          </article>
        ))}
      </section>
    </main>
  )
}

export default Home
