export const NAME_REGEX = /^[A-Za-zÁÉÍÓÚÑáéíóúñ]+(?: [A-Za-zÁÉÍÓÚÑáéíóúñ]+)*$/
export const ID_REGEX = /^\d{6,15}$/
// Colombia: exactamente 10 dígitos
export const MOBILE_CO_REGEX = /^\d{10}$/
// Email debe terminar en .com o .co
export const EMAIL_REGEX = /^[^\s@]+@[^\s@]+\.(?:com|co)$/i

export function cleanName(s: string) {
  return (s ?? '').trim().replace(/\s+/g, ' ')
}

export type UserForm = {
  firstName: string
  secondName?: string
  firstSurname: string
  secondSurname?: string
  idNumber: string
  email: string
  mobile: string // 10 dígitos Colombia
  countryId: string
  departmentId: string
  cityId: string
}

export function validateUserForm(values: UserForm) {
  const errors: Partial<Record<keyof UserForm, string>> = {}

  const firstName = cleanName(values.firstName)
  const secondName = cleanName(values.secondName ?? '')
  const firstSurname = cleanName(values.firstSurname)
  const secondSurname = cleanName(values.secondSurname ?? '')

  if (!firstName || firstName.length < 2 || firstName.length > 40 || !NAME_REGEX.test(firstName)) {
    errors.firstName = 'Nombre inválido: solo letras/espacios (2–40).'
  }
  if (secondName && (secondName.length > 40 || !NAME_REGEX.test(secondName))) {
    errors.secondName = 'Segundo nombre inválido.'
  }
  if (!firstSurname || firstSurname.length < 2 || firstSurname.length > 40 || !NAME_REGEX.test(firstSurname)) {
    errors.firstSurname = 'Primer apellido inválido: solo letras/espacios (2–40).'
  }
  if (secondSurname && (secondSurname.length > 40 || !NAME_REGEX.test(secondSurname))) {
    errors.secondSurname = 'Segundo apellido inválido.'
  }

  if (!ID_REGEX.test(values.idNumber)) {
    errors.idNumber = 'Identificación inválida: solo dígitos (6–15).'
  }
  if (!EMAIL_REGEX.test(values.email)) {
    errors.email = 'Correo inválido: debe terminar en .com o .co.'
  }
  // Colombia: exactamente 10 dígitos
  if (!MOBILE_CO_REGEX.test(values.mobile)) {
    errors.mobile = 'Móvil inválido (Colombia): exactamente 10 dígitos.'
  }

  if (!values.countryId) errors.countryId = 'País requerido.'
  if (!values.departmentId) errors.departmentId = 'Departamento requerido.'
  if (!values.cityId) errors.cityId = 'Ciudad requerida.'

  return {
    errors,
    cleaned: {
      ...values,
      firstName,
      secondName,
      firstSurname,
      secondSurname,
      // asegurar números puros en id/móvil
      idNumber: (values.idNumber ?? '').replace(/\D/g, ''),
      mobile: (values.mobile ?? '').replace(/\D/g, ''),
    },
  }
}
