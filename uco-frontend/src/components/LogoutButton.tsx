import { useCallback } from 'react'
import { useAuth0 } from '@auth0/auth0-react'

interface LogoutButtonProps {
  className?: string
}

const LogoutButton = ({ className }: LogoutButtonProps) => {
  const { logout } = useAuth0()

  const handleLogout = useCallback(() => {
    logout({
      logoutParams: {
        returnTo: window.location.origin,
      },
    })
  }, [logout])

  return (
    <button type="button" className={className} onClick={handleLogout}>
      Cerrar sesión
    </button>
  )
}

export default LogoutButton
