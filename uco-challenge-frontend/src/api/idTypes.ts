import { api } from './client'

export interface IdType {
  id?: string
  code?: string
  name?: string
  description?: string
}

export async function getIdTypes(): Promise<IdType[]> {
  try {
    const { data } = await api.get<IdType[]>('/idtypes')
    return data
  } catch (err: any) {
    console.error(
      'CATALOGS_ERROR',
      err?.config?.url,
      err?.response?.status,
      err?.response?.data
    )
    throw err
  }
}
