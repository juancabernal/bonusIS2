import { api } from './client'

export interface Country {
  id: string
  name: string
}

export interface Department {
  id: string
  name: string
}

export interface City {
  id: string
  name: string
}

export async function getCountries(): Promise<Country[]> {
  try {
    const { data } = await api.get<Country[]>('/locations/countries')
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

export async function getDepartments(countryId: string): Promise<Department[]> {
  try {
    const { data } = await api.get<Department[]>(
      `/locations/countries/${countryId}/departments`
    )
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

export async function getCities(departmentId: string): Promise<City[]> {
  try {
    const { data } = await api.get<City[]>(
      `/locations/departments/${departmentId}/cities`
    )
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
