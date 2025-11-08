import { useCallback, useEffect, useState } from 'react'

import { getIdTypes, type IdType } from '@/api/idTypes'

interface UseIdTypesState {
  data: IdType[]
  loading: boolean
  error: string | null
}

export function useIdTypes() {
  const [state, setState] = useState<UseIdTypesState>({
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
        const result = await getIdTypes()
        if (cancelled) return

        setState({ data: result, loading: false, error: null })
      } catch (error) {
        console.error(error)
        if (cancelled) return

        setState({
          data: [],
          loading: false,
          error: 'No se pudieron cargar los tipos de documento.',
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
