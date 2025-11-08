import { useCallback, useEffect, useMemo, useState } from 'react'

import { getDepartments, type Department } from '@/api/locations'

interface UseDepartmentsState {
  data: Department[]
  loading: boolean
  error: string | null
  lastUpdated?: number | null
}

export function useDepartments(countryId: string) {
  const normalizedCountryId = useMemo(() => countryId.trim(), [countryId])
  const [state, setState] = useState<UseDepartmentsState>({
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

    if (!normalizedCountryId) {
      setState({ data: [], loading: false, error: null })
      return () => {
        cancelled = true
      }
    }

    const load = async (initial = false) => {
      if (initial) setState((prev) => ({ ...prev, loading: true, error: null }))

      try {
        const result = await getDepartments(normalizedCountryId)
        if (cancelled) return

        setState((prev) => {
          const prevData = prev.data || []
          const same = JSON.stringify(prevData) === JSON.stringify(result)
          if (same) return { ...prev, loading: false, error: null }
          return { data: result, loading: false, error: null, lastUpdated: Date.now() }
        })
      } catch (error) {
        console.error('Error polling departments for', normalizedCountryId, error)
        if (cancelled) return

        setState((prev) => ({ ...prev, loading: false, error: 'No se pudieron cargar los departamentos. Inténtalo de nuevo.' }))
      }
    }

    void load(true)
    intervalId = setInterval(() => void load(false), 5000)

    return () => {
      cancelled = true
      if (intervalId != null) clearInterval(intervalId)
    }
  }, [normalizedCountryId, refreshIndex])

  return { ...state, refresh }
}
