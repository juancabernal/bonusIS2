import { api } from './client'

const USERS_BASE = '/users'

export async function confirmUserEmail(id: string) {
  await api.post(`${USERS_BASE}/${id}/confirm-email`)
}

export async function confirmUserMobile(id: string) {
  await api.post(`${USERS_BASE}/${id}/confirm-mobile`)
}
