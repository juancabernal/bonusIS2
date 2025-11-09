import ButtonSpinner from '../../components/ui/ButtonSpinner'
import type { UserSummary, VerificationAction } from './types'
import styles from './UsersTable.module.css'

interface UsersTableProps {
  data: UserSummary[]
  onConfirmEmail: (user: UserSummary) => void
  onConfirmMobile: (user: UserSummary) => void
  pendingAction?: VerificationAction | null
  isFiltering?: boolean
}

const UsersTable = ({
  data,
  onConfirmEmail,
  onConfirmMobile,
  pendingAction = null,
  isFiltering = false,
}: UsersTableProps) => {
  const busy = Boolean(isFiltering)

  return (
    <section
      className={styles.wrapper}
      aria-live="polite"
      data-filtering={busy ? 'true' : undefined}
    >
      <div className={styles.tableScroll}>
        <table className={styles.table} aria-label="Listado de usuarios" aria-busy={busy}>
          <thead>
            <tr>
              <th scope="col">Nombre completo</th>
              <th scope="col">Email</th>
              <th scope="col">Móvil</th>
              <th scope="col">Estado</th>
              <th scope="col">Acciones</th>
            </tr>
          </thead>
          <tbody>
            {data.map((user) => {
              const fullName = [user.firstName, user.lastName ?? ''].filter(Boolean).join(' ')
              const displayName = fullName || user.email
              const mobileNumber = user.mobileNumber?.toString().trim()
              const formattedMobile = mobileNumber ? mobileNumber : '—'
              const emailConfirmed = Boolean(user.emailConfirmed)
              const mobileConfirmed = Boolean(user.mobileNumberConfirmed)
              const canConfirmEmail = !emailConfirmed
              const canConfirmMobile = Boolean(mobileNumber) && !mobileConfirmed
              const emailStatusLabel = emailConfirmed ? 'Correo verificado' : 'Correo pendiente'
              const mobileStatusLabel = mobileConfirmed ? 'Móvil verificado' : 'Móvil pendiente'
              const isPendingRow = pendingAction?.userId === user.id
              const isEmailPending = isPendingRow && pendingAction?.channel === 'email'
              const isMobilePending = isPendingRow && pendingAction?.channel === 'mobile'

              return (
                <tr
                  key={user.id}
                  data-pending={isPendingRow ? 'true' : undefined}
                  aria-busy={isPendingRow ? true : undefined}
                >
                  <td className={styles.fullName}>{fullName}</td>
                  <td className={styles.email}>{user.email}</td>
                  <td className={styles.mobile}>{formattedMobile}</td>
                  <td className={styles.statusCell}>
                    <div className={styles.statusGroup}>
                      <span
                        className={`${styles.status} ${emailConfirmed ? styles.statusSuccess : styles.statusPending}`.trim()}
                        role="status"
                        aria-live="polite"
                        aria-label={emailStatusLabel}
                      >
                        {emailStatusLabel}
                      </span>
                      <span
                        className={`${styles.status} ${mobileConfirmed ? styles.statusSuccess : styles.statusPending}`.trim()}
                        role="status"
                        aria-live="polite"
                        aria-label={mobileStatusLabel}
                      >
                        {mobileStatusLabel}
                      </span>
                    </div>
                  </td>
                  <td className={styles.actionsCell}>
                    <div className={styles.actions}>
                      <button
                        type="button"
                        className="button button--primary"
                        onClick={() => onConfirmEmail(user)}
                        disabled={!canConfirmEmail || Boolean(isEmailPending)}
                        aria-busy={isEmailPending}
                        aria-label={`Confirmar correo de ${displayName}`}
                        title={
                          isEmailPending
                            ? 'Enviando código de verificación...'
                            : emailConfirmed
                              ? 'Correo ya confirmado'
                              : 'Confirmar correo'
                        }
                      >
                        <span className={styles.buttonContent}>
                          {isEmailPending ? (
                            <>
                              <ButtonSpinner label={`Enviando código al correo de ${displayName}`} />
                              <span>Enviando…</span>
                            </>
                          ) : (
                            'Confirmar correo'
                          )}
                        </span>
                      </button>
                      <button
                        type="button"
                        className="button button--secondary"
                        onClick={() => onConfirmMobile(user)}
                        disabled={!canConfirmMobile || Boolean(isMobilePending)}
                        aria-busy={isMobilePending}
                        aria-label={`Confirmar número de ${displayName}`}
                        title={
                          isMobilePending
                            ? 'Enviando código de verificación...'
                            : mobileConfirmed
                              ? 'Número ya confirmado'
                              : mobileNumber
                                ? 'Confirmar número'
                                : 'Número de móvil no disponible'
                        }
                      >
                        <span className={styles.buttonContent}>
                          {isMobilePending ? (
                            <>
                              <ButtonSpinner label={`Enviando código al móvil de ${displayName}`} />
                              <span>Enviando…</span>
                            </>
                          ) : (
                            'Confirmar número'
                          )}
                        </span>
                      </button>
                    </div>
                  </td>
                </tr>
              )
            })}
          </tbody>
        </table>
      </div>
    </section>
  )
}

export default UsersTable
