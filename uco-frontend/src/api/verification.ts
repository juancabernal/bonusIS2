import { apiClient } from './client'

export type Channel = 'email' | 'mobile'

export async function sendVerificationCode(userId: string, channel: Channel) {
  return apiClient.post(`/users/${userId}/send-code`, null, { params: { channel } })
}

export async function verifyContactCode(userId: string, channel: Channel, code: string) {
  return apiClient.post(`/users/${userId}/confirm-code`, { channel, code })
}
