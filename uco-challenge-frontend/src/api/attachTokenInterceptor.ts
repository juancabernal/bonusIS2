// src/api/attachTokenInterceptor.ts
import { AxiosError } from 'axios'
import type { GetTokenSilentlyOptions } from '@auth0/auth0-react'
import { api } from './client'

let getTokenSilentlyFn:
  | ((opts?: GetTokenSilentlyOptions) => Promise<string>)
  | null = null

let installed = false
let reqId: number | null = null
let resId: number | null = null

export const attachTokenInterceptor = (
  getTokenSilently: (opts?: GetTokenSilentlyOptions) => Promise<string>
) => {
  getTokenSilentlyFn = getTokenSilently
  console.debug('attachTokenInterceptor: registered getTokenSilently')

  // Evita duplicar interceptores si el hook se llama más de una vez
  if (installed) {
    if (reqId !== null) api.interceptors.request.eject(reqId)
    if (resId !== null) api.interceptors.response.eject(resId)
    installed = false
  }

  reqId = api.interceptors.request.use(async (config) => {
    try {
      if (getTokenSilentlyFn) {
        console.debug('attachTokenInterceptor: requesting token for request', {
          url: config.url,
          method: config.method,
        })

        const token = await getTokenSilentlyFn({
          authorizationParams: {
            audience: import.meta.env.VITE_AUTH0_AUDIENCE,
            scope: import.meta.env.VITE_AUTH0_SCOPE || 'openid profile email',
          },
        })

        if (token) {
          // Guarda para depuración local (opcional)
          try {
            localStorage.setItem('DEBUG_ACCESS_TOKEN', token)
          } catch {}

          // Asegura objeto headers y setea Authorization
          config.headers = config.headers ?? {}
          ;(config.headers as any).Authorization = `Bearer ${token}`
        } else {
          console.debug('attachTokenInterceptor: no token obtained')
        }
      }
    } catch (err) {
      console.error('attachTokenInterceptor: error while getting token', err)
    }
    return config
  })

  resId = api.interceptors.response.use(
    (res) => res,
    (err: AxiosError) => {
      const status = err.response?.status
      const url = err.config?.url
      console.error('API response error', { status, url, message: err.message })
      if (status === 401) {
        console.warn('API interceptor: 401 -> redirect to /login', { url })
        window.location.href = '/login'
      }
      if (status === 403) {
        console.warn('API interceptor: 403 -> redirect to /not-authorized', { url })
        window.location.href = '/not-authorized'
      }
      return Promise.reject(err)
    }
  )

  installed = true
}
