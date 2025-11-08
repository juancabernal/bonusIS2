import { useCallback, useEffect, useMemo, useState } from 'react'

import { getDepartments, type Department } from '@/api/locations'

interface UseDepartmentsState {
  data: Department[]
  loading: boolean
  error: string | null
}

export function useDepartments(countryId: string) {
  const normalizedCountryId = useMemo(() => countryId.trim(), [countryId])
  const [state, setState] = useState<UseDepartmentsState>({
    data: [],
    loading: false,
    error: null,
  })
  const [refreshIndex, setRefreshIndex] = useState(0)

  const refresh = useCallback(() => {
    setRefreshIndex((value) => value + 1)
  }, [])

  useEffect(() => {
    let cancelled = false

    if (!normalizedCountryId) {
      setState({ data: [], loading: false, error: null })
      return () => {
        cancelled = true
      }
    }

    const load = async () => {
      setState({ data: [], loading: true, error: null })

      try {
        const result = await getDepartments(normalizedCountryId)
        if (cancelled) return

        setState({ data: result, loading: false, error: null })
      } catch (error) {
        console.error(error)
        if (cancelled) return

        setState({
          data: [],
          loading: false,
          error: 'No se pudieron cargar los departamentos. Inténtalo de nuevo.',
        })
      }
    }

    void load()

    return () => {
      cancelled = true
    }
  }, [normalizedCountryId, refreshIndex])

  return { ...state, refresh }
}
