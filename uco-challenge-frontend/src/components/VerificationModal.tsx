import { useEffect, useState } from 'react'
import { createPortal } from 'react-dom'
import { isAxiosError } from 'axios'
import { toast } from 'react-toastify'
import { verifyContactCode } from '@/api/verification'
import { parseApiError } from '@/utils/parseApiError'
import styles from './VerificationModal.module.css'

const CODE_REGEX = /^\d{6}$/u

interface VerificationModalProps {
  open: boolean
  onClose: () => void
  userId: string
  channel: 'email' | 'mobile'
  targetLabel?: string
  onVerified: () => void
}

export default function VerificationModal({
  open,
  onClose,
  userId,
  channel,
  targetLabel,
  onVerified,
}: VerificationModalProps) {
  const [code, setCode] = useState('')
  const [submitting, setSubmitting] = useState(false)
  const isValidCode = CODE_REGEX.test(code)

  useEffect(() => {
    if (!open) setCode('')
  }, [open])

  const handleConfirm = async () => {
    if (!isValidCode) return

    try {
      setSubmitting(true)
      await verifyContactCode(userId, channel, code)
      toast.success('Contacto verificado correctamente')
      onClose()
      onVerified()
    } catch (error) {
      let message = 'No se pudo verificar el código. Intenta nuevamente.'
      if (isAxiosError(error)) {
        const status = error.response?.status
        const parsed = parseApiError(error)
        if (status === 400 || status === 500) {
          message = parsed
        } else if (parsed) {
          message = parsed
        }
      } else if (error instanceof Error) {
        message = error.message
      }
      toast.error(message)
      console.error(error)
    } finally {
      setSubmitting(false)
    }
  }

  if (!open) return null

  const content = (
    <div className={styles.overlay} role="dialog" aria-modal="true">
      <div className={styles.container} role="document">
        <h3 className={styles.title}>Verificar {channel === 'email' ? 'correo' : 'móvil'}</h3>
        <p className={styles.text}>
          Ingresa el código de 6 dígitos enviado a <strong>{targetLabel}</strong>
        </p>

        <input
          className={styles.input}
          type="text"
          inputMode="numeric"
          maxLength={6}
          value={code}
          onChange={(event) => {
            const value = event.target.value.replace(/\D/g, '').slice(0, 6)
            setCode(value)
          }}
          placeholder="000000"
          aria-label="Código de verificación"
          pattern="\d{6}"
          aria-invalid={!isValidCode && code.length > 0}
        />

        <div className={styles.actions}>
          <button type="button" onClick={onClose} className="button button--ghost" disabled={submitting}>
            Cancelar
          </button>
          <button
            type="button"
            onClick={handleConfirm}
            disabled={submitting || !isValidCode}
            className="button button--primary"
            aria-busy={submitting}
          >
            {submitting ? 'Confirmando...' : 'Confirmar'}
          </button>
        </div>
      </div>
    </div>
  )

  return createPortal(content, document.body)
}
