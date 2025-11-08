import React, { useEffect, useState } from 'react'
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
        background: '#007bff',
        color: '#fff',
        padding: '10px 16px',
        border: 'none',
        borderRadius: '8px',
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

  // Estado para Toast dinámico
  const [ToastComp, setToastComp] = useState<any>(null)

  useEffect(() => {
    let mounted = true
    import('react-toastify')
      .then(mod => {
        const Comp =
          (mod as any).ToastContainer ?? (mod as any).default?.ToastContainer ?? null
        if (mounted) setToastComp(() => Comp)
        console.log('DEBUG: react-toastify module ->', mod, 'resolved ToastContainer ->', Comp)
      })
      .catch(err => {
        console.warn('DEBUG: no se pudo cargar react-toastify dinámicamente', err)
      })
    return () => {
      mounted = false
    }
  }, [])

  const avatarLetter = (user?.name ?? user?.email ?? '?').charAt(0).toUpperCase()

  return (
    <ErrorBoundary>
      <div className="app-shell">
        <header className="app-header">
          <NavLink to="/" className="brand">
            <span className="brand__dot" aria-hidden />
            <span>UCO Admin</span>
          </NavLink>

          <nav className="app-nav">
            <NavLink to="/" className={({ isActive }) => `app-nav__link${isActive ? ' is-active' : ''}`}>
              Inicio
            </NavLink>
            <NavLink
              to="/dashboard"
              className={({ isActive }) => `app-nav__link${isActive ? ' is-active' : ''}`}
            >
              Panel
            </NavLink>
            <NavLink to="/users" className={({ isActive }) => `app-nav__link${isActive ? ' is-active' : ''}`}>
              Usuarios
            </NavLink>
          </nav>

          {isAuthenticated ? (
            <div className="app-user" aria-live="polite">
              <span className="app-user__avatar" aria-hidden>
                {avatarLetter}
              </span>
              <span className="app-user__name">{user?.name ?? user?.email}</span>
              <button
                onClick={() => logout({ logoutParams: { returnTo: window.location.origin } })}
                className="logout-btn"
              >
                Cerrar sesión
              </button>
            </div>
          ) : (
            <span className="app-guest">Invitado</span>
          )}
        </header>

        <div className="app-content">
          <AppRouter />
        </div>

        <ToastContainer
          position="top-right"
          autoClose={3000}
          newestOnTop
          closeOnClick
          pauseOnHover
          theme="colored"
        />
      </div>

      {/* Botón de debug para obtener token manualmente */}
      <DebugTokenButton />
    </ErrorBoundary>
  )
}

export default App
