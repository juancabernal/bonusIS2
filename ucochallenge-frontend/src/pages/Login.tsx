import { useCallback, useEffect, useMemo } from 'react'
import { Link, useLocation, useNavigate, type Location } from 'react-router-dom'
import { useAuth0 } from '@auth0/auth0-react'
import Loading from '../components/Loading'

const Login = () => {
  const { isAuthenticated, isLoading, loginWithRedirect } = useAuth0()
  const location = useLocation()
  const navigate = useNavigate()

  const loginState = useMemo(
    () => location.state as { from?: Location; unauthorized?: boolean } | undefined,
    [location.state],
  )

  const unauthorized = Boolean(loginState?.unauthorized)

  const fromPathname = loginState?.from?.pathname ?? '/'

  const handleSwitchAccount = useCallback(() => {
    console.debug('Login: handleSwitchAccount called')
    void loginWithRedirect({
      authorizationParams: {
        prompt: 'select_account',
      },
    })
  }, [loginWithRedirect])

  useEffect(() => {
    console.debug('Login useEffect (redirect) fired', { isLoading, isAuthenticated, fromPathname })
    if (!isLoading && !isAuthenticated) {
      const returnTo = fromPathname
      console.debug('Login: triggering loginWithRedirect', { returnTo })
      void loginWithRedirect({
        appState: {
          returnTo,
          target: returnTo,
        },
        authorizationParams: {
          prompt: 'select_account',
        },
      })
    }
  }, [fromPathname, isAuthenticated, isLoading, loginWithRedirect])

  useEffect(() => {
    console.debug('Login useEffect (navigate after auth) fired', { isLoading, isAuthenticated, unauthorized, fromPathname })
    if (!isLoading && isAuthenticated && !unauthorized) {
      console.debug('Login: navigating to', fromPathname)
      navigate(fromPathname, { replace: true })
    }
  }, [fromPathname, isAuthenticated, isLoading, navigate, unauthorized])

  if (unauthorized) {
    return (
      <main className="page">
        <section className="card status-card status-card--warning" role="alert">
          <span className="status-icon" aria-hidden>
            ⚠️
          </span>
          <div>
            <h1>Sin permisos</h1>
            <p>No tienes permisos para acceder al panel de control.</p>
            <p>
              Regresa al <Link to="/">inicio</Link> o inicia sesión con una cuenta con privilegios de
              administrador.
            </p>
          </div>
          <div className="card-actions card-actions--start">
            <button type="button" className="btn btn-primary" onClick={handleSwitchAccount}>
              Cambiar de cuenta
            </button>
            <Link to="/" className="btn btn-outline">
              Volver al inicio
            </Link>
          </div>
        </section>
      </main>
    )
  }

  return <Loading message="Redirigiendo a Auth0..." />
}

export default Login
