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
        right: 24,
        background: 'linear-gradient(135deg, rgba(118, 186, 255, 0.18), rgba(104, 150, 255, 0.35))',
        color: 'var(--color-text-primary)',
        padding: '0.7rem 1.2rem',
        border: '1px solid rgba(132, 168, 255, 0.4)',
        borderRadius: '999px',
        boxShadow: '0 24px 60px rgba(5, 8, 26, 0.55)',
        backdropFilter: 'blur(14px)',
        fontWeight: 600,
        letterSpacing: '0.08em',
        textTransform: 'uppercase',
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
        <div className="shell-ambient" aria-hidden>
          <span className="shell-ambient__orb shell-ambient__orb--primary" />
          <span className="shell-ambient__orb shell-ambient__orb--secondary" />
          <span className="shell-ambient__grid" />
        </div>

        <div className="shell-layout">
          <aside className="shell-sidebar" aria-label="Navegación principal">
            <NavLink to="/" className="shell-logo" aria-label="Ir al inicio de UCO Challenge">
              <span className="shell-logo__mark" aria-hidden />
              <div className="shell-logo__text">
                <strong>UCO</strong>
                <span>Operations</span>
              </div>
            </NavLink>

            <nav className="shell-menu">
              <NavLink to="/" className={({ isActive }) => `shell-menu__item${isActive ? ' is-active' : ''}`}> 
                <span className="shell-menu__indicator" aria-hidden />
                <span>Inicio</span>
              </NavLink>
              <NavLink
                to="/dashboard"
                className={({ isActive }) => `shell-menu__item${isActive ? ' is-active' : ''}`}
              >
                <span className="shell-menu__indicator" aria-hidden />
                <span>Panel</span>
              </NavLink>
              <NavLink
                to="/users"
                className={({ isActive }) => `shell-menu__item${isActive ? ' is-active' : ''}`}
              >
                <span className="shell-menu__indicator" aria-hidden />
                <span>Usuarios</span>
              </NavLink>
            </nav>
            <div className="shell-sidebar__footer">
              <p>Centro de control de contactos y verificaciones.</p>
            </div>
          </aside>

          <div className="shell-mainzone">
            <header className="shell-topbar">
              <div className="shell-topbar__status">
                <span className="shell-topbar__pulse" aria-hidden />
                <div>
                  <p>Entorno seguro activo</p>
                  <small>API Gateway · Auth0 · Microservicios</small>
                </div>
              </div>

              {isAuthenticated ? (
                <div className="shell-user" aria-live="polite">
                  <span className="shell-user__avatar" aria-hidden>
                    {avatarLetter}
                  </span>
                  <div className="shell-user__meta">
                    <span className="shell-user__name">{user?.name ?? user?.email}</span>
                    <span className="shell-user__role">Operador</span>
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
                <span className="shell-guest">Invitado</span>
              )}
            </header>

            <main className="shell-main">
              <div className="shell-main__inner">
                <AppRouter />
              </div>
            </main>

            <footer className="shell-footer">
              <p>
                © {new Date().getFullYear()} Operación UCO. Plataforma segura gestionada con Auth0 y
                microservicios.
              </p>
            </footer>
          </div>
        </div>

      </div>

      {/*
        Mueve el contenedor de toasts fuera de la shell principal para evitar que
        quede por detrás de modales o elementos con stacking context aislado
        (como .app-shell que usa isolation).  Al estar a nivel de raíz y con
        z-index elevado, los mensajes de error o éxito se mostrarán siempre
        por encima de los diálogos y el usuario no tendrá que cerrar el modal
        para verlos.
      */}
      <ToastContainer
        position="top-center"
        autoClose={3200}
        newestOnTop
        closeOnClick
        pauseOnHover
        theme="dark"
        toastClassName={() => 'toast-shell'}
        bodyClassName={() => 'toast-shell__body'}
        progressClassName="toast-shell__progress"
        /*
          Establecemos un z-index alto para que el contenedor de toasts
          supere el z-index del overlay de verificación (1000) y de cualquier
          otro modal.  De este modo los toasts se renderizan por encima de
          los cuadros flotantes.  No afecta la lógica ni el comportamiento
          funcional, solo la superposición visual.
        */
        style={{ zIndex: 2000 }}
      />

      {/* Botón de debug para obtener token manualmente */}
      <DebugTokenButton />
    </ErrorBoundary>
  )
}

export default App
