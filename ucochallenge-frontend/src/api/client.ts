import axios from 'axios'
import { parseApiError } from '../utils/parseApiError'

function trimTrailingSlash(url: string) {
  return url.endsWith('/') ? url.slice(0, -1) : url
}

const DEFAULT_BASE_URL = '/api/admin/uco-challenge/api/v1'
const resolvedBaseUrl = trimTrailingSlash(import.meta.env.VITE_API_BASE_URL ?? DEFAULT_BASE_URL)

console.debug('api client: resolvedBaseUrl', { resolvedBaseUrl })

export const api = axios.create({
  baseURL: resolvedBaseUrl,
  headers: { 'Content-Type': 'application/json' },
})

export const apiClient = api

api.interceptors.response.use(
  (response) => response,
  (error) => {
    try {
      const nice = parseApiError(error)
      ;(error as any).__niceMessage = nice
      console.error('api client interceptor: parsed error message', { nice })
    } catch (parseErr) {
      console.error('api client interceptor: failed to parse error', parseErr)
    }
    return Promise.reject(error)
  }
)
