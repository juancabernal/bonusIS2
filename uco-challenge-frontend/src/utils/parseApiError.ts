export function parseApiError(err: any): string {
  // Axios error?
  const res = err?.response
  const data = res?.data

  // Formato plano: { code, message }
  if (data?.message && typeof data.message === 'string') {
    return data.message
  }

  // Formato anidado: { data: { code, message }, dataReturned: true }
  if (data?.data?.message && typeof data.data.message === 'string') {
    return data.data.message
  }

  // Otros posibles campos comunes
  const alt =
    data?.error_description ||
    data?.error ||
    res?.statusText ||
    err?.message

  return typeof alt === 'string' && alt.trim().length > 0
    ? alt
    : 'Ocurrió un error. Intenta nuevamente.'
}
