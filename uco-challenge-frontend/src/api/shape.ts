// src/api/shape.ts
export function normalizeToArray<T = any>(data: any): T[] {
  if (Array.isArray(data)) return data
  if (Array.isArray(data?.content)) return data.content
  if (Array.isArray(data?.items)) return data.items
  if (Array.isArray(data?.data)) return data.data
  if (Array.isArray(data?.results)) return data.results
  // Si es objeto con {content: {items:[]}} u otro anidado raro
  if (data && typeof data === 'object') {
    for (const k of Object.keys(data)) {
      const v = (data as any)[k]
      if (Array.isArray(v)) return v
    }
  }
  return []
}
