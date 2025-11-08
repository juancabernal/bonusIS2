import { useCallback, useEffect, useState } from 'react'

import { getUsers } from '@/api/users'
import type { UsersPage } from '@/pages/users/types'

interface UseUsersState {
  data: UsersPage | null
  loading: boolean
  error: string | null
}

export function useUsers(page: number, size: number) {
  const [state, setState] = useState<UseUsersState>({
    data: null,
    loading: true,
    error: null,
  })
  const [refreshIndex, setRefreshIndex] = useState(0)

  const reload = useCallback(() => {
    setRefreshIndex((value) => value + 1)
  }, [])

  useEffect(() => {
    let cancelled = false

    const load = async () => {
      setState((prev) => ({ ...prev, loading: true, error: null }))

      try {
        const response = (await getUsers(page, size)) as UsersPage
        if (cancelled) return

        setState({ data: response, loading: false, error: null })
      } catch (error) {
        console.error(error)
        if (cancelled) return

        setState({
          data: null,
          loading: false,
          error: 'No se pudo cargar la lista de usuarios. Intenta nuevamente en unos segundos.',
        })
      }
    }

    void load()

    return () => {
      cancelled = true
    }
  }, [page, size, refreshIndex])

  return { ...state, reload }
}
