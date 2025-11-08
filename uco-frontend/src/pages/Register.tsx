import { useEffect } from 'react'
import { useAuth0 } from '@auth0/auth0-react'
import Loading from '../components/Loading'

const Register = () => {
  const { loginWithRedirect } = useAuth0()

  useEffect(() => {
    void loginWithRedirect({
      authorizationParams: {
        screen_hint: 'signup',
        prompt: 'login',
        audience: import.meta.env.VITE_AUTH0_AUDIENCE,
        scope: import.meta.env.VITE_AUTH0_SCOPE || 'openid profile email',
      },
      appState: { returnTo: '/dashboard' },
    })
  }, [loginWithRedirect])

  return <Loading message="Redirigiendo al registro..." />
}

export default Register
