import { useCallback, useEffect, useMemo, useState } from 'react'

import { getCities, type City } from '@/api/locations'

interface UseCitiesState {
  data: City[]
  loading: boolean
  error: string | null
}

export function useCities(departmentId: string) {
  const normalizedDepartmentId = useMemo(() => departmentId.trim(), [departmentId])
  const [state, setState] = useState<UseCitiesState>({
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

    if (!normalizedDepartmentId) {
      setState({ data: [], loading: false, error: null })
      return () => {
        cancelled = true
      }
    }

    const load = async () => {
      setState({ data: [], loading: true, error: null })

      try {
        const result = await getCities(normalizedDepartmentId)
        if (cancelled) return

        setState({ data: result, loading: false, error: null })
      } catch (error) {
        console.error(error)
        if (cancelled) return

        setState({
          data: [],
          loading: false,
          error: 'No se pudieron cargar las ciudades. Inténtalo de nuevo.',
        })
      }
    }

    void load()

    return () => {
      cancelled = true
    }
  }, [normalizedDepartmentId, refreshIndex])

  return { ...state, refresh }
}
