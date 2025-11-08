import { useCallback, useEffect, useMemo, useState } from 'react'

import { getCities, type City } from '@/api/locations'

interface UseCitiesState {
  data: City[]
  loading: boolean
  error: string | null
  lastUpdated?: number | null
}

export function useCities(departmentId: string) {
  const normalizedDepartmentId = useMemo(() => departmentId.trim(), [departmentId])
  const [state, setState] = useState<UseCitiesState>({
    data: [],
    loading: false,
    error: null,
    lastUpdated: null,
  })
  const [refreshIndex, setRefreshIndex] = useState(0)

  const refresh = useCallback(() => {
    setRefreshIndex((value) => value + 1)
  }, [])

  useEffect(() => {
    let cancelled = false
    let intervalId: ReturnType<typeof setInterval> | null = null

    if (!normalizedDepartmentId) {
      setState({ data: [], loading: false, error: null })
      return () => {
        cancelled = true
      }
    }

    const load = async (initial = false) => {
      if (initial) setState((prev) => ({ ...prev, loading: true, error: null }))

      try {
        const result = await getCities(normalizedDepartmentId)
        if (cancelled) return

        setState((prev) => {
          const prevData = prev.data || []
          const same = JSON.stringify(prevData) === JSON.stringify(result)
          if (same) return { ...prev, loading: false, error: null }
          return { data: result, loading: false, error: null, lastUpdated: Date.now() }
        })
      } catch (error) {
        console.error('Error polling cities for', normalizedDepartmentId, error)
        if (cancelled) return

        setState((prev) => ({ ...prev, loading: false, error: 'No se pudieron cargar las ciudades. Inténtalo de nuevo.' }))
      }
    }

    void load(true)
    intervalId = setInterval(() => void load(false), 5000)

    return () => {
      cancelled = true
      if (intervalId != null) clearInterval(intervalId)
    }
  }, [normalizedDepartmentId, refreshIndex])

  return { ...state, refresh }
}
