export type VerificationChannel = 'email' | 'mobile'

export interface UserSummary {
  id: string
  firstName: string
  lastName?: string | null
  email: string
  documentNumber?: string | null
  mobileNumber?: string | null
  emailConfirmed?: boolean | null
  mobileNumberConfirmed?: boolean | null
}

export interface UsersPage {
  users: UserSummary[]
  page: number
  size: number
  totalElements: number
}

export interface VerificationAction {
  userId: string
  channel: VerificationChannel
}
