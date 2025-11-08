import { Auth0Provider, type AppState } from '@auth0/auth0-react'
import { useCallback } from 'react'
import { useNavigate } from 'react-router-dom'
import type { ReactNode } from 'react'

interface Auth0ProviderWithNavigateProps {
  children: ReactNode
}

const getEnv = (key: string) => {
  const value = import.meta.env[key]
  if (!value) {
    console.warn(`Auth0 environment variable "${key}" is not defined.`)
  }
  console.debug(`Auth0 env ${key}=${value ? '[SET]' : '[NOT SET]'}`)
  return value
}

const Auth0ProviderWithNavigate = ({ children }: Auth0ProviderWithNavigateProps) => {
  const navigate = useNavigate()

  const domain = getEnv('VITE_AUTH0_DOMAIN')
  const clientId = getEnv('VITE_AUTH0_CLIENT_ID')
  const audience = getEnv('VITE_AUTH0_AUDIENCE')
  const scope = getEnv('VITE_AUTH0_SCOPE') ?? 'openid profile email'

  const onRedirectCallback = useCallback(
    (appState?: AppState) => {
      console.debug('Auth0 onRedirectCallback called', { appState })
      const targetUrl = appState?.returnTo ?? appState?.target ?? window.location.pathname
      console.debug('Auth0 onRedirectCallback -> navigating to', targetUrl)
      navigate(targetUrl, { replace: true })
    },
    [navigate],
  )

  if (!domain || !clientId) {
    console.warn('Auth0ProviderWithNavigate: domain or clientId missing, skipping Auth0Provider', { domain, clientId })
    return children
  }

  console.debug('Auth0ProviderWithNavigate: Auth0Provider initialized', { domain: domain ? '[SET]' : domain, clientId: clientId ? '[SET]' : clientId, audience: audience ? '[SET]' : audience })

  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      authorizationParams={{
        redirect_uri: window.location.origin,
        audience: audience ?? undefined,
        scope,
        prompt: 'select_account',
      }}
      onRedirectCallback={onRedirectCallback}
      cacheLocation="localstorage"
      useRefreshTokens
    >
      {children}
    </Auth0Provider>
  )
}

export default Auth0ProviderWithNavigate
