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
        bottom: 24,
        right: 28,
        background: 'rgba(90, 84, 255, 0.18)',
        color: 'var(--color-text-primary)',
        padding: '0.7rem 1.2rem',
        border: '1px solid rgba(90, 84, 255, 0.32)',
        borderRadius: '14px',
        boxShadow: '0 24px 40px rgba(20, 33, 61, 0.18)',
        backdropFilter: 'blur(10px)',
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
        <aside className="shell-sidebar">
          <NavLink to="/" className="shell-sidebar__brand" aria-label="Ir al inicio de UCO Challenge">
            <span className="shell-sidebar__brand-mark" aria-hidden />
            <div className="shell-sidebar__brand-text">
              <span>UCO</span>
              <strong>Control</strong>
            </div>
          </NavLink>

          <nav className="shell-sidebar__nav" aria-label="Navegación principal">
            <NavLink to="/" className={({ isActive }) => `shell-sidebar__link${isActive ? ' is-active' : ''}`}>
              <span aria-hidden>🏠</span>
              <span>Inicio</span>
            </NavLink>
            <NavLink to="/dashboard" className={({ isActive }) => `shell-sidebar__link${isActive ? ' is-active' : ''}`}>
              <span aria-hidden>📊</span>
              <span>Panel</span>
            </NavLink>
            <NavLink to="/users" className={({ isActive }) => `shell-sidebar__link${isActive ? ' is-active' : ''}`}>
              <span aria-hidden>👥</span>
              <span>Usuarios</span>
            </NavLink>
          </nav>

          <div className="shell-sidebar__info">
            <span className="shell-sidebar__info-badge">Operación en vivo</span>
            <p>
              Gateway, Auth0 y microservicios coordinados para la Universidad Cooperativa de Colombia.
            </p>
          </div>
        </aside>

        <div className="shell-content">
          <header className="shell-topbar">
            <div className="shell-topbar__intro">
              <h1>Centro de control UCO</h1>
              <p>Gestiona usuarios y verificaciones desde una vista única.</p>
            </div>

            {isAuthenticated ? (
              <div className="shell-user" aria-live="polite">
                <span className="shell-user__avatar" aria-hidden>
                  {avatarLetter}
                </span>
                <div className="shell-user__details">
                  <span className="shell-user__label">Sesión activa</span>
                  <span className="shell-user__name">{user?.name ?? user?.email}</span>
                </div>
                <button
                  onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
                  className="button button--ghost"
                  type="button"
                >
                  Cerrar sesión
                </button>
              </div>
            ) : (
              <div className="shell-guest" aria-live="polite">
                <span>Sesión no iniciada</span>
                <span>Accede para operar el panel seguro.</span>
              </div>
            )}
          </header>

          <main className="shell-main">
            <div className="shell-main__inner">
              <AppRouter />
            </div>
          </main>

          <footer className="shell-footer">
            <p>
              © {new Date().getFullYear()} Operación UCO. Plataforma segura gestionada con Auth0 y microservicios.
            </p>
          </footer>
        </div>
      </div>

      <ToastContainer
        position="bottom-left"
        autoClose={3200}
        newestOnTop
        closeOnClick
        pauseOnHover
        theme="light"
      />

      {/* Botón de debug para obtener token manualmente */}
      <DebugTokenButton />
    </ErrorBoundary>
  )
}

export default App
