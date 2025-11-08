import { useCallback, useEffect, useState } from 'react'

import { getCountries, type Country } from '@/api/locations'

interface UseCountriesState {
  data: Country[]
  loading: boolean
  error: string | null
}

export function useCountries() {
  const [state, setState] = useState<UseCountriesState>({
    data: [],
    loading: true,
    error: null,
  })
  const [refreshIndex, setRefreshIndex] = useState(0)

  const refresh = useCallback(() => {
    setRefreshIndex((value) => value + 1)
  }, [])

  useEffect(() => {
    let cancelled = false

    const load = async () => {
      setState((prev) => ({ ...prev, loading: true, error: null }))

      try {
        const result = await getCountries()
        if (cancelled) return

        setState({ data: result, loading: false, error: null })
      } catch (error) {
        console.error(error)
        if (cancelled) return

        setState({
          data: [],
          loading: false,
          error: 'No se pudieron cargar los países. Inténtalo de nuevo.',
        })
      }
    }

    void load()

    return () => {
      cancelled = true
    }
  }, [refreshIndex])

  return { ...state, refresh }
}
