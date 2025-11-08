import { useCallback, useEffect, useState } from 'react'

import { getUsers } from '@/api/users'
import type { UsersPage } from '@/pages/users/types'

interface UseUsersState {
  data: UsersPage | null
  loading: boolean
  error: string | null
  lastUpdated?: number | null
}

export function useUsers(page: number, size: number) {
  const [state, setState] = useState<UseUsersState>({
    data: null,
    loading: true,
    error: null,
    lastUpdated: null,
  })
  const [refreshIndex, setRefreshIndex] = useState(0)

  const reload = useCallback(() => {
    setRefreshIndex((value) => value + 1)
  }, [])

  useEffect(() => {
    let cancelled = false
    let intervalId: ReturnType<typeof setInterval> | null = null

    const load = async (initial = false) => {
      if (initial) setState((prev) => ({ ...prev, loading: true, error: null }))

      try {
        const response = (await getUsers(page, size)) as UsersPage
        if (cancelled) return

        setState((prev) => {
          const prevData = prev.data
          // compare basic serialized form to avoid unnecessary updates
          const same = JSON.stringify(prevData) === JSON.stringify(response)
          if (same) return { ...prev, loading: false, error: null }
          return { data: response, loading: false, error: null, lastUpdated: Date.now() }
        })
      } catch (error) {
        console.error('Error polling users', error)
        if (cancelled) return

        // keep previous data on polling errors; show gentle error message
        setState((prev) => ({ ...prev, loading: false, error: 'No se pudo cargar la lista de usuarios. Intenta nuevamente en unos segundos.' }))
      }
    }

    void load(true)
    intervalId = setInterval(() => void load(false), 5000)

    return () => {
      cancelled = true
      if (intervalId != null) clearInterval(intervalId)
    }
  }, [page, size, refreshIndex])

  return { ...state, reload }
}
