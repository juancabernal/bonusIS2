import React, { useEffect } from 'react'
import { NavLink } from 'react-router-dom'
import { ToastContainer } from 'react-toastify'
import { useAuth0 } from '@auth0/auth0-react'
import { attachTokenInterceptor } from './api/apiClient'
import AppRouter from './routes/Router'
import 'react-toastify/dist/ReactToastify.css'
import './App.css'

// 🧱 ErrorBoundary para capturar errores de renderizado
class ErrorBoundary extends React.Component<{ children: React.ReactNode }> {
  state = { hasError: false, error: null as Error | null }

  static getDerivedStateFromError(error: Error) {
    return { hasError: true, error }
  }

  componentDidCatch(error: Error) {
    console.error('[Debug] ErrorBoundary caught:', error)
  }

  render() {
    if (this.state.hasError) {
      console.error('[Debug] Error state:', this.state.error)
      return null
    }
    return this.props.children
  }
}

// 🧩 Componente de debug opcional
function DebugTokenButton() {
  const { getAccessTokenSilently, isAuthenticated } = useAuth0()

  const handleClick = async () => {
    try {
      console.log('🧩 Solicitando token manualmente...')
      const token = await getAccessTokenSilently({
        authorizationParams: {
          audience: import.meta.env.VITE_AUTH0_AUDIENCE,
          scope: 'openid profile email',
        },
      })
      console.log('🔑 Token obtenido manualmente:', token)
      localStorage.setItem('DEBUG_ACCESS_TOKEN', token)
      alert('Token guardado en localStorage como DEBUG_ACCESS_TOKEN')
    } catch (err) {
      console.error('❌ Error al obtener token manualmente:', err)
    }
  }

  if (!isAuthenticated) return null

  return (
    <button
      onClick={handleClick}
      style={{
        position: 'fixed',
        bottom: 20,
        right: 20,
        background: 'rgba(108, 99, 255, 0.16)',
        color: 'var(--color-text-primary)',
        padding: '0.6rem 1.1rem',
        border: '1px solid rgba(108, 99, 255, 0.4)',
        borderRadius: '12px',
        boxShadow: '0 18px 40px rgba(3, 5, 12, 0.4)',
        backdropFilter: 'blur(8px)',
        fontWeight: 600,
        cursor: 'pointer',
        zIndex: 1000,
      }}
    >
      🧩 Obtener Token
    </button>
  )
}

const App = () => {
  const { getAccessTokenSilently, isAuthenticated, isLoading, user, logout } = useAuth0()

  // 🧠 Inicializar interceptor cuando Auth0 esté listo
  useEffect(() => {
    if (!isLoading && isAuthenticated) {
      console.debug('✅ Auth0 listo — registrando attachTokenInterceptor...')
      attachTokenInterceptor(getAccessTokenSilently)
    } else {
      console.debug('⏳ Esperando autenticación antes de registrar interceptor...', {
        isLoading,
        isAuthenticated,
      })
    }
  }, [isLoading, isAuthenticated, getAccessTokenSilently])

  // 🧹 Limpiar token de debug al cerrar sesión
  useEffect(() => {
    if (!isAuthenticated) {
      console.debug('🧹 Limpiando DEBUG_ACCESS_TOKEN (logout detectado)')
      localStorage.removeItem('DEBUG_ACCESS_TOKEN')
    }
  }, [isAuthenticated])

  const avatarLetter = (user?.name ?? user?.email ?? '?').charAt(0).toUpperCase()

  return (
    <ErrorBoundary>
      <div className="app-shell">
        <header className="shell-header">
          <div className="shell-header__group">
            <NavLink to="/" className="shell-header__brand" aria-label="Ir al inicio de UCO Challenge">
              <span className="shell-header__brand-mark" aria-hidden />
              <span>UCO Control</span>
            </NavLink>

            <nav className="shell-header__nav" aria-label="Navegación principal">
              <NavLink to="/" className={({ isActive }) => `shell-header__nav-link${isActive ? ' is-active' : ''}`}>
                Inicio
              </NavLink>
              <NavLink
                to="/dashboard"
                className={({ isActive }) => `shell-header__nav-link${isActive ? ' is-active' : ''}`}
              >
                Panel
              </NavLink>
              <NavLink
                to="/users"
                className={({ isActive }) => `shell-header__nav-link${isActive ? ' is-active' : ''}`}
              >
                Usuarios
              </NavLink>
            </nav>
          </div>

          {isAuthenticated ? (
            <div className="shell-user" aria-live="polite">
              <span className="shell-user__avatar" aria-hidden>
                {avatarLetter}
              </span>
              <span className="shell-user__name">{user?.name ?? user?.email}</span>
              <button
                onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
                className="button button--ghost"
                type="button"
              >
                Cerrar sesión
              </button>
            </div>
          ) : (
            <span className="shell-guest">Invitado</span>
          )}
        </header>

        <main className="shell-main">
          <AppRouter />
        </main>

        <footer className="shell-footer">
          <p>
            © {new Date().getFullYear()} Operación UCO. Plataforma segura gestionada con Auth0 y
            microservicios.
          </p>
        </footer>

        <ToastContainer
          position="top-right"
          autoClose={3200}
          newestOnTop
          closeOnClick
          pauseOnHover
          theme="dark"
        />
      </div>

      {/* Botón de debug para obtener token manualmente */}
      <DebugTokenButton />
    </ErrorBoundary>
  )
}

export default App
