import { FormEvent, useEffect, useMemo, useState } from 'react'
import { isAxiosError } from 'axios'
import { useNavigate, useSearchParams } from 'react-router-dom'
import { verifyContactCode } from '../api/verification'
import styles from './VerifyContactPage.module.css'

const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.[^\s@]+$/
const PHONE_REGEX = /^\+[0-9]{7,15}$/
const CODE_REGEX = /^\d{6}$/

type ToastLike = {
  success?: (message: string) => void
}

type ToastWindow = Window & { toast?: ToastLike }

const resolveToast = (): ToastLike | undefined => {
  if (typeof window === 'undefined') return undefined
  const toast = (window as ToastWindow).toast
  if (!toast) return undefined
  return toast
}

const getInitialContact = (searchParams: URLSearchParams) => {
  const value = searchParams.get('contact')
  return value ?? ''
}

const normalizeCode = (value: string) => value.replace(/\D/g, '').slice(0, 6)

const VerifyContactPage = () => {
  const navigate = useNavigate()
  const [params] = useSearchParams()

  const [contact, setContact] = useState('')
  const [code, setCode] = useState('')
  const [error, setError] = useState<string | null>(null)
  const [errorField, setErrorField] = useState<'contact' | 'code' | null>(null)
  const [loading, setLoading] = useState(false)

  const initialContact = useMemo(() => getInitialContact(params), [params])

  useEffect(() => {
    if (initialContact) {
      setContact(initialContact)
    }
  }, [initialContact])

  const validateContact = (value: string) => EMAIL_REGEX.test(value) || PHONE_REGEX.test(value)
  const isCodeReady = CODE_REGEX.test(code)
  const isContactReady = validateContact(contact.trim())
  const canSubmit = isContactReady && isCodeReady && !loading

  const handleSubmit = async (event: FormEvent<HTMLFormElement>) => {
    event.preventDefault()
    setError(null)
    setErrorField(null)

    const trimmedContact = contact.trim()
    const normalizedCode = code.trim()

    if (!trimmedContact || !validateContact(trimmedContact)) {
      setError('Debes ingresar un correo válido o un número en formato +57...')
      setErrorField('contact')
      return
    }

    if (!CODE_REGEX.test(normalizedCode)) {
      setError('El código debe tener 6 dígitos.')
      setErrorField('code')
      return
    }

    try {
      setLoading(true)
      await verifyContactCode(trimmedContact, normalizedCode)

      const toast = resolveToast()
      if (toast?.success) {
        toast.success('Verificación exitosa 🎉')
      } else if (typeof window !== 'undefined') {
        window.alert('Verificación exitosa')
      }

      navigate('/users', { replace: true, state: { refresh: Date.now() } })
    } catch (err) {
      if (isAxiosError(err)) {
        const status = err.response?.status
        if (status && status >= 400 && status < 500) {
          setError('Código inválido o vencido. Intenta de nuevo.')
          setErrorField('code')
          return
        }
      }
      setError('No se pudo verificar. Revisa tu conexión o intenta más tarde.')
      setErrorField(null)
    } finally {
      setLoading(false)
    }
  }

  return (
    <main className={`page ${styles.page}`} aria-labelledby="verify-title">
      <section className={styles.card} role="presentation">
        <header className={styles.header}>
          <h1 id="verify-title" className={styles.title}>
            Verificar contacto
          </h1>
          <p className={styles.description}>
            Ingresa el correo o número (formato +57...) y el código de 6 dígitos que recibiste para confirmar el contacto.
          </p>
        </header>

        <form className={styles.form} onSubmit={handleSubmit} noValidate>
          <div className={`${styles.field} ${errorField === 'contact' ? styles.fieldError : ''}`}>
            <span className={styles.label}>Correo o número</span>
            <input
              id="contact"
              type="text"
              inputMode="text"
              autoComplete="username"
              placeholder="usuario@uco.edu.co o +57300..."
              value={contact}
              onChange={(event) => {
                setContact(event.target.value)
                if (errorField === 'contact') {
                  setError(null)
                  setErrorField(null)
                }
              }}
              aria-describedby={error ? 'verify-error' : undefined}
              aria-invalid={errorField === 'contact' ? true : undefined}
              required
              className={styles.control}
            />
          </div>

          <div className={`${styles.field} ${errorField === 'code' ? styles.fieldError : ''}`}>
            <span className={styles.label}>Código de verificación</span>
            <input
              id="code"
              type="text"
              inputMode="numeric"
              autoComplete="one-time-code"
              maxLength={6}
              placeholder="000000"
              value={code}
              onChange={(event) => {
                setCode(normalizeCode(event.target.value))
                if (errorField === 'code') {
                  setError(null)
                  setErrorField(null)
                }
              }}
              aria-describedby={error ? 'verify-error' : undefined}
              aria-invalid={errorField === 'code' ? true : undefined}
              className={`${styles.control} ${styles.codeInput}`.trim()}
              required
            />
          </div>

          {error && (
            <div id="verify-error" className={styles.errorMessage} role="alert">
              <span aria-hidden>⚠</span>
              <span>{error}</span>
            </div>
          )}

          <div className={styles.actions}>
            <button type="submit" className="button button--primary" disabled={!canSubmit}>
              {loading ? 'Verificando...' : 'Verificar'}
            </button>
          </div>
        </form>
      </section>
    </main>
  )
}

export default VerifyContactPage
