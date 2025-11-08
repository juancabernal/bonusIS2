import { AxiosError } from 'axios'
import type { GetTokenSilentlyOptions } from '@auth0/auth0-react'
import { api } from './client'

let getTokenSilentlyFn: ((opts?: GetTokenSilentlyOptions) => Promise<string>) | null = null;

export const attachTokenInterceptor = (
  getTokenSilently: (opts?: GetTokenSilentlyOptions) => Promise<string>,
) => {
  getTokenSilentlyFn = getTokenSilently;
  console.debug('attachTokenInterceptor: registered getTokenSilently')

  api.interceptors.request.use(async (config) => {
    try {
      if (getTokenSilentlyFn) {
        console.debug('attachTokenInterceptor: requesting token for request', { url: config.url, method: config.method })
        const token = await getTokenSilentlyFn({
          authorizationParams: {
            audience: import.meta.env.VITE_AUTH0_AUDIENCE,
            scope: import.meta.env.VITE_AUTH0_SCOPE || 'openid profile email',
          },
        });

        const SHOW_FULL_TOKEN = true;

        if (token) {
          // 💾 Guarda el token completo en localStorage (solo para depuración local)
          localStorage.setItem('DEBUG_ACCESS_TOKEN', token);
          console.debug('🔑 Token guardado en localStorage bajo "DEBUG_ACCESS_TOKEN"');

          // 🔐 Agrega el token al header Authorization
          config.headers.Authorization = `Bearer ${token}`;
        } else {
          console.debug('attachTokenInterceptor: no token obtained');
        }
      }
    } catch (err) {
      console.error('attachTokenInterceptor: error while getting token', err)
    }
    return config
  });

  api.interceptors.response.use(
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
  );
}
