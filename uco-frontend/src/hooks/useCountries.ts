import { useCallback, useEffect, useState } from 'react'

import { getCountries, type Country } from '@/api/locations'

interface UseCountriesState {
  data: Country[]
  loading: boolean
  error: string | null
  lastUpdated?: number | null
}

export function useCountries() {
  const [state, setState] = useState<UseCountriesState>({
    data: [],
    loading: true,
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

    const load = async (initial = false) => {
      if (initial) setState((prev) => ({ ...prev, loading: true, error: null }))

      try {
        const result = await getCountries()
        if (cancelled) return

        setState((prev) => {
          const prevData = prev.data || []
          const same = JSON.stringify(prevData) === JSON.stringify(result)
          if (same) return { ...prev, loading: false, error: null }
          return { data: result, loading: false, error: null, lastUpdated: Date.now() }
        })
      } catch (error) {
        console.error('Error polling countries', error)
        if (cancelled) return

        setState((prev) => ({ ...prev, loading: false, error: 'No se pudieron cargar los países. Inténtalo de nuevo.' }))
      }
    }

    void load(true)
    intervalId = setInterval(() => void load(false), 5000)

    return () => {
      cancelled = true
      if (intervalId != null) clearInterval(intervalId)
    }
  }, [refreshIndex])

  return { ...state, refresh }
}
