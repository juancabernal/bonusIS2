import type { UserSummary } from '../../api/users'
import styles from './UsersTable.module.css'

interface UsersTableProps {
  data: UserSummary[]
  onConfirmEmail: (user: UserSummary) => void
  onConfirmMobile: (user: UserSummary) => void
}

const UsersTable = ({ data, onConfirmEmail, onConfirmMobile }: UsersTableProps) => {
  return (
    <section className={styles.tableCard} aria-live="polite">
      <div className={styles.tableWrapper}>
        <table className={styles.table} aria-label="Listado de usuarios">
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

              return (
                <tr key={user.id}>
                  <td className={styles.fullName}>{fullName}</td>
                  <td className={styles.email}>{user.email}</td>
                  <td className={styles.mobile}>{formattedMobile}</td>
                  <td className={styles.statusCell}>
                    <div className={styles.statusBadges}>
                      <span
                        className={`${styles.statusBadge} ${
                          emailConfirmed ? styles.statusBadgeSuccess : styles.statusBadgePending
                        }`.trim()}
                        role="status"
                        aria-live="polite"
                        aria-label={emailStatusLabel}
                      >
                        <span className={styles.statusBadgeIcon} aria-hidden>
                          {emailConfirmed ? '✅' : '⌛'}
                        </span>
                        <span>{emailStatusLabel}</span>
                      </span>
                      <span
                        className={`${styles.statusBadge} ${
                          mobileConfirmed ? styles.statusBadgeSuccess : styles.statusBadgePending
                        }`.trim()}
                        role="status"
                        aria-live="polite"
                        aria-label={mobileStatusLabel}
                      >
                        <span className={styles.statusBadgeIcon} aria-hidden>
                          {mobileConfirmed ? '✅' : '⌛'}
                        </span>
                        <span>{mobileStatusLabel}</span>
                      </span>
                    </div>
                  </td>
                  <td className={styles.actionsCell}>
                    <div className={styles.actions}>
                      <button
                        type="button"
                        className="btn btn-primary"
                        onClick={() => onConfirmEmail(user)}
                        disabled={!canConfirmEmail}
                        aria-label={`Confirmar correo de ${displayName}`}
                        title={emailConfirmed ? 'Correo ya confirmado' : 'Confirmar correo'}
                      >
                        Confirmar correo
                      </button>
                      <button
                        type="button"
                        className="btn btn-outline"
                        onClick={() => onConfirmMobile(user)}
                        disabled={!canConfirmMobile}
                        aria-label={`Confirmar número de ${displayName}`}
                        title={
                          mobileConfirmed
                            ? 'Número ya confirmado'
                            : mobileNumber
                              ? 'Confirmar número'
                              : 'Número de móvil no disponible'
                        }
                      >
                        Confirmar número
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
