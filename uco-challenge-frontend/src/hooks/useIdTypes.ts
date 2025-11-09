import { useCallback, useEffect, useState } from 'react'

import { getIdTypes, type IdType } from '@/api/idTypes'

interface UseIdTypesState {
  data: IdType[]
  loading: boolean
  error: string | null
  lastUpdated?: number | null
}

export function useIdTypes() {
  const [state, setState] = useState<UseIdTypesState>({
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
        const result = await getIdTypes()
        if (cancelled) return

        setState((prev) => {
          // avoid unnecessary state updates to prevent visual flicker
          const prevData = prev.data || []
          const same = JSON.stringify(prevData) === JSON.stringify(result)
          if (same) return { ...prev, loading: false, error: null }
          return { data: result, loading: false, error: null, lastUpdated: Date.now() }
        })
      } catch (error) {
        console.error('Error polling id types', error)
        if (cancelled) return

        // keep previous data on polling errors; surface a gentle error
        setState((prev) => ({ ...prev, loading: false, error: 'No se pudieron cargar los tipos de documento.' }))
      }
    }

    // initial load (shows loading)
    void load(true)

    // polling every 5s; keep running while component is mounted
    intervalId = setInterval(() => void load(false), 5000)

    return () => {
      cancelled = true
      if (intervalId != null) clearInterval(intervalId)
    }
  }, [refreshIndex])

  return { ...state, refresh }
}
