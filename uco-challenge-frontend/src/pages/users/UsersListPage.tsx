import { ChangeEvent, useDeferredValue, useEffect, useMemo, useState } from 'react'
import { Link, useLocation, useNavigate, useSearchParams } from 'react-router-dom'
import { isAxiosError } from 'axios'
import { toast } from 'react-toastify'
import { sendVerificationCode } from '../../api/verification'
import { parseApiError } from '../../utils/parseApiError'
import EmptyState from '../../components/ui/EmptyState'
import ErrorAlert from '../../components/ui/ErrorAlert'
import LoadingSpinner from '../../components/ui/LoadingSpinner'
import PageSizeSelect from '../../components/ui/PageSizeSelect'
import Pagination from '../../components/ui/Pagination'
import VerificationModal from '../../components/VerificationModal'
import UsersTable from './UsersTable'
import styles from './UsersListPage.module.css'
import type { VerificationAction, VerificationChannel } from './types'
import { useUsers } from '@/hooks/useUsers'

type ModalState = {
  open: boolean
  userId: string
  channel: VerificationChannel
  targetLabel?: string
}

const PAGE_SIZES = [10, 20, 30, 50]
const DEFAULT_PAGE = 0
const DEFAULT_SIZE = 10

const sanitizePage = (value: string | null) => {
  if (!value) return DEFAULT_PAGE
  const parsed = Number.parseInt(value, 10)
  return Number.isNaN(parsed) || parsed < 0 ? DEFAULT_PAGE : parsed
}

const sanitizeSize = (value: string | null) => {
  if (!value) return DEFAULT_SIZE
  const parsed = Number.parseInt(value, 10)
  return PAGE_SIZES.includes(parsed) ? parsed : DEFAULT_SIZE
}

const UsersListPage = () => {
  const [searchParams, setSearchParams] = useSearchParams()
  const navigate = useNavigate()
  const [feedback, setFeedback] = useState<{ type: 'success' | 'error'; message: string } | null>(null)
  const [query, setQuery] = useState('')
  const [modal, setModal] = useState<ModalState>({
    open: false,
    userId: '',
    channel: 'email',
    targetLabel: '',
  })
  const [pendingAction, setPendingAction] = useState<VerificationAction | null>(null)
  const location = useLocation()

  const page = useMemo(() => sanitizePage(searchParams.get('page')), [searchParams])
  const size = useMemo(() => sanitizeSize(searchParams.get('size')), [searchParams])

  const {
    data,
    loading,
    error: loadError,
    reload,
    lastUpdated: usersLastUpdated,
  } = useUsers(page, size)

  const [showUsersBadge, setShowUsersBadge] = useState(false)
  useEffect(() => {
    if (!usersLastUpdated) return
    setShowUsersBadge(true)
    const t = setTimeout(() => setShowUsersBadge(false), 1500)
    return () => clearTimeout(t)
  }, [usersLastUpdated])

  useEffect(() => {
    const next = new URLSearchParams(searchParams)
    let changed = false

    if (searchParams.get('page') !== String(page)) {
      next.set('page', String(page))
      changed = true
    }

    if (searchParams.get('size') !== String(size)) {
      next.set('size', String(size))
      changed = true
    }

    if (changed) {
      setSearchParams(next, { replace: true })
    }
  }, [page, size, searchParams, setSearchParams])

  const handlePageChange = (nextPage: number) => {
    const next = new URLSearchParams(searchParams)
    next.set('page', String(nextPage))
    next.set('size', String(size))
    setSearchParams(next)
  }

  const handleSizeChange = (nextSize: number) => {
    const next = new URLSearchParams(searchParams)
    next.set('page', String(DEFAULT_PAGE))
    next.set('size', String(nextSize))
    setSearchParams(next)
  }

  const handleSearchChange = (event: ChangeEvent<HTMLInputElement>) => {
    setQuery(event.target.value)
  }

  const handleRetry = () => {
    setFeedback(null)
    reload()
  }

  const totalUsers = data?.totalElements ?? 0
  const users = data?.users ?? []
  const normalizedQuery = query.trim().toLowerCase()
  const deferredQuery = useDeferredValue(normalizedQuery)
  const filteredUsers = useMemo(() => {
    if (!deferredQuery) {
      return users
    }

    return users.filter((user) => {
      const fullName = [user.firstName, user.lastName ?? ''].filter(Boolean).join(' ').toLowerCase()
      const email = user.email.toLowerCase()
      const document = user.documentNumber?.toLowerCase() ?? ''
      const mobile = user.mobileNumber?.toString().toLowerCase() ?? ''
      const identifier = user.id.toLowerCase()

      return (
        fullName.includes(deferredQuery) ||
        email.includes(deferredQuery) ||
        document.includes(deferredQuery) ||
        mobile.includes(deferredQuery) ||
        identifier.includes(deferredQuery)
      )
    })
  }, [users, deferredQuery])
  const hasQuery = normalizedQuery.length > 0
  const pageCount = users.length
  const filteredCount = filteredUsers.length
  const isFiltering = normalizedQuery !== deferredQuery
  const summaryHelperMessage = isFiltering
    ? 'Calculando coincidencias...'
    : hasQuery
      ? `Filtrando ${filteredCount} de ${pageCount} registros en esta página.`
      : `Mostrando ${pageCount} registros en esta página.`
  const searchMetaMessage = isFiltering
    ? 'Buscando coincidencias...'
    : hasQuery
      ? `${filteredCount} coincidencias en esta página.`
      : `${pageCount} registros visibles.`

  const locationState = location.state as
    | { refresh?: number; feedback?: { type: 'success' | 'error'; message: string } }
    | null

  useEffect(() => {
    if (!locationState) {
      return
    }

    if (locationState.refresh) {
      reload()
    }

    if (locationState.feedback) {
      setFeedback(locationState.feedback)
    }

    if (locationState.refresh || locationState.feedback) {
      navigate(`${location.pathname}${location.search}`, { replace: true })
    }
  }, [locationState, location.pathname, location.search, navigate, reload])

  useEffect(() => {
    if (!feedback) return

    const timeout = setTimeout(() => {
      setFeedback(null)
    }, 5000)

    return () => {
      clearTimeout(timeout)
    }
  }, [feedback])

  const handleSendCode = async (userId: string, channel: VerificationChannel) => {
    const sendingMessage =
      channel === 'mobile'
        ? 'Enviando código al número registrado...'
        : 'Enviando código al correo electrónico...'
    const successMessage =
      channel === 'mobile'
        ? 'Código enviado por SMS correctamente.'
        : 'Correo de verificación enviado.'

    try {
      toast.info(sendingMessage)
      await sendVerificationCode(userId, channel)
      toast.success(successMessage)
      return true
    } catch (error) {
      const detailed = isAxiosError(error)
        ? ((error as typeof error & { __niceMessage?: string }).__niceMessage ?? parseApiError(error))
        : error instanceof Error
          ? error.message
          : 'Error al enviar el código. Intente nuevamente.'
      toast.error(detailed)
      console.error(error)
      return false
    }
  }

  const openVerificationFor = async (
    userId: string,
    channel: VerificationChannel,
    targetLabel?: string,
  ) => {
    if (!targetLabel) {
      setFeedback({ type: 'error', message: 'El contacto seleccionado no está disponible.' })
      return
    }

    if (pendingAction) {
      return
    }

    setFeedback(null)
    setPendingAction({ userId, channel })
    try {
      const sent = await handleSendCode(userId, channel)
      if (!sent) {
        return
      }
      setModal({ open: true, userId, channel, targetLabel })
    } finally {
      setPendingAction(null)
    }
  }

  const handleVerified = () => {
    reload()
    setFeedback({ type: 'success', message: 'Contacto verificado correctamente.' })
  }

  return (
    <main className="page">
      <header className={styles.header}>
        <div className={styles.headerTop}>
          <div className={styles.headerText}>
            <h1>Usuarios</h1>
            <p>Gestiona el directorio oficial de usuarios y controla su estado de verificación.</p>
          </div>
          <div className={styles.headerActions}>
            <div className={styles.filters}>
              <div className={styles.search}>
                <svg viewBox="0 0 24 24" fill="none" aria-hidden>
                  <path
                    d="M15.5 15.5L20 20"
                    stroke="currentColor"
                    strokeWidth="1.5"
                    strokeLinecap="round"
                    strokeLinejoin="round"
                  />
                  <circle cx="11" cy="11" r="6" stroke="currentColor" strokeWidth="1.5" />
                </svg>
                <label className="visually-hidden" htmlFor="user-search">
                  Buscar usuarios
                </label>
                <input
                  id="user-search"
                  type="search"
                  value={query}
                  onChange={handleSearchChange}
                  placeholder="Buscar por nombre, documento o correo"
                  autoComplete="off"
                />
                <span
                  className={styles.searchMeta}
                  data-busy={isFiltering ? 'true' : undefined}
                  role="status"
                  aria-live="polite"
                >
                  {searchMetaMessage}
                </span>
              </div>
              <PageSizeSelect value={size} onChange={handleSizeChange} />
            </div>
            <Link className="button button--primary" to="/users/new" aria-label="Registrar nuevo usuario">
              Registrar usuario
            </Link>
          </div>
        </div>
      </header>

      <section className={styles.summaryCard} aria-live="polite">
        <span className={styles.summaryLabel}>
          Usuarios totales
          {showUsersBadge ? (
            <span className={`${styles.updateBadge} ${styles.updateBadgeBlink}`} aria-hidden>
              Actualizado
            </span>
          ) : null}
        </span>
        <p className={styles.summaryValue}>{totalUsers}</p>
        <p
          className={styles.summaryHelper}
          data-busy={isFiltering ? 'true' : undefined}
        >
          {summaryHelperMessage}
        </p>
      </section>

      {feedback && (
        <div
          className={`${styles.feedback} ${
            feedback.type === 'success' ? styles.feedbackSuccess : styles.feedbackError
          }`.trim()}
          role="status"
          aria-live="assertive"
        >
          <span aria-hidden>{feedback.type === 'success' ? '✔' : '⚠'}</span>
          <span>{feedback.message}</span>
        </div>
      )}

      <div className={styles.contentStack}>
        {loading && (
          <section className={styles.summaryCard} aria-busy="true">
            <LoadingSpinner label="Cargando usuarios..." />
          </section>
        )}

        {!loading && loadError && (
          <ErrorAlert
            message={loadError}
            actions={
              <>
                <button type="button" className="button button--primary" onClick={handleRetry} aria-label="Reintentar carga">
                  Reintentar
                </button>
                <Link to="/" className="button button--ghost" aria-label="Ir al inicio">
                  Ir al inicio
                </Link>
              </>
            }
          >
            <p className={styles.summaryHelper}>
              Verifica tu conexión o tus permisos e intenta nuevamente.
            </p>
          </ErrorAlert>
        )}

        {!loading && !loadError && pageCount === 0 && (
          <EmptyState description="No hay usuarios registrados todavía." />
        )}

        {!loading && !loadError && pageCount > 0 && hasQuery && filteredCount === 0 && (
          <EmptyState title="Sin coincidencias" description="No se encontraron usuarios para esta búsqueda." />
        )}

        {!loading && !loadError && filteredCount > 0 && (
          <UsersTable
            data={filteredUsers}
            pendingAction={pendingAction}
            isFiltering={isFiltering}
            onConfirmEmail={(user) =>
              void openVerificationFor(user.id, 'email', user.email.trim())
            }
            onConfirmMobile={(user) =>
              void openVerificationFor(
                user.id,
                'mobile',
                user.mobileNumber?.toString().trim() || '',
              )
            }
          />
        )}
      </div>

      {!loading && !loadError && data ? (
        <footer className={styles.footer}>
          <Pagination page={page} size={size} totalElements={data.totalElements} onPageChange={handlePageChange} />
        </footer>
      ) : null}

      <VerificationModal
        open={modal.open}
        onClose={() => setModal((state) => ({ ...state, open: false }))}
        userId={modal.userId}
        channel={modal.channel}
        targetLabel={modal.targetLabel}
        onVerified={handleVerified}
      />
    </main>
  )
}

export default UsersListPage
