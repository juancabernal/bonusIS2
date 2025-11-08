import { ChangeEvent, FocusEvent, FormEvent, useEffect, useState } from 'react'
import { isAxiosError } from 'axios'
import { Link, useNavigate } from 'react-router-dom'
import { createUser } from '../../api/users'
import { toast } from 'react-toastify'
import { getCities, getCountries, getDepartments, type City, type Country, type Department } from '../../api/locations'
import { getIdTypes, type IdType } from '../../api/idTypes'
import { EMAIL_REGEX, MOBILE_CO_REGEX, validateUserForm, type UserForm } from '@/utils/validators'

interface CreateUserRequest {
  idType: string
  idNumber: string
  firstName: string
  secondName?: string
  firstSurname: string
  secondSurname?: string
  homeCity: string
  email: string
  mobileNumber: string
}

type FormErrorKey = keyof CreateUserRequest | 'country' | 'department'

interface BackendErrorDetail {
  field?: string
  code?: string
  message?: string
  value?: string
}

interface DuplicateFieldDetail {
  field?: string
  value?: string
}

type BackendErrorDetails = BackendErrorDetail[] | DuplicateFieldDetail

interface BackendErrorResponse {
  code?: string
  message?: string
  userMessage?: string
  details?: BackendErrorDetails
}

const FORM_ERROR_KEYS: FormErrorKey[] = [
  'idType',
  'idNumber',
  'firstName',
  'secondName',
  'firstSurname',
  'secondSurname',
  'homeCity',
  'email',
  'mobileNumber',
  'country',
  'department',
]

const USER_FORM_FIELD_MAP: Record<string, keyof UserForm> = {
  firstName: 'firstName',
  secondName: 'secondName',
  firstSurname: 'firstSurname',
  secondSurname: 'secondSurname',
  idNumber: 'idNumber',
  email: 'email',
  mobileNumber: 'mobile',
}

const mapValidationErrors = (
  errors: Partial<Record<keyof UserForm, string>>,
): Partial<Record<FormErrorKey, string>> => {
  const mapped: Partial<Record<FormErrorKey, string>> = {}

  if (errors.firstName) mapped.firstName = errors.firstName
  if (errors.secondName) mapped.secondName = errors.secondName
  if (errors.firstSurname) mapped.firstSurname = errors.firstSurname
  if (errors.secondSurname) mapped.secondSurname = errors.secondSurname
  if (errors.idNumber) mapped.idNumber = errors.idNumber
  if (errors.email) mapped.email = errors.email
  if (errors.mobile) mapped.mobileNumber = errors.mobile
  if (errors.countryId) mapped.country = errors.countryId
  if (errors.departmentId) mapped.department = errors.departmentId
  if (errors.cityId) mapped.homeCity = errors.cityId

  return mapped
}

const REQUIRED_MESSAGE = 'Este campo es obligatorio.'
const EMAIL_DUPLICATE_MESSAGE = 'Este correo ya está registrado'
const REVIEW_FIELDS_MESSAGE = 'Revisa los campos marcados antes de continuar.'

const isFormErrorKey = (value: string): value is FormErrorKey =>
  FORM_ERROR_KEYS.includes(value as FormErrorKey)

export default function UserCreatePage() {
  const [formState, setFormState] = useState<CreateUserRequest>({
    idType: '',
    idNumber: '',
    firstName: '',
    secondName: '',
    firstSurname: '',
    secondSurname: '',
    homeCity: '',
    email: '',
    mobileNumber: '',
  })
  const [fieldErrors, setFieldErrors] = useState<Partial<Record<FormErrorKey, string>>>({})
  const [saving, setSaving] = useState(false)
  const [err, setErr] = useState<string | null>(null)
  const [locationsError, setLocationsError] = useState<string | null>(null)
  const [failedRequest, setFailedRequest] = useState<'countries' | 'departments' | 'cities' | null>(null)

  const [idTypes, setIdTypes] = useState<IdType[]>([])
  const [loadingIdTypes, setLoadingIdTypes] = useState(false)
  const [errorIdTypes, setErrorIdTypes] = useState<string | null>(null)

  const [countries, setCountries] = useState<Country[]>([])
  const [departments, setDepartments] = useState<Department[]>([])
  const [cities, setCities] = useState<City[]>([])
  const [selectedCountry, setSelectedCountry] = useState('')
  const [selectedDepartment, setSelectedDepartment] = useState('')

  const [countryRequestId, setCountryRequestId] = useState(0)
  const [departmentRequestId, setDepartmentRequestId] = useState(0)
  const [cityRequestId, setCityRequestId] = useState(0)

  const [loadingCountries, setLoadingCountries] = useState(false)
  const [loadingDepartments, setLoadingDepartments] = useState(false)
  const [loadingCities, setLoadingCities] = useState(false)

  const navigate = useNavigate()

  const emailPattern = EMAIL_REGEX.source.replace(/^\^|\$$/g, '')
  const mobilePattern = MOBILE_CO_REGEX.source.replace(/^\^|\$$/g, '')

  const buildUserForm = (overrides: Partial<UserForm> = {}) => ({
    firstName: formState.firstName,
    secondName: formState.secondName ?? '',
    firstSurname: formState.firstSurname,
    secondSurname: formState.secondSurname ?? '',
    idNumber: formState.idNumber,
    email: formState.email,
    mobile: formState.mobileNumber,
    countryId: selectedCountry,
    departmentId: selectedDepartment,
    cityId: formState.homeCity,
    ...overrides,
  })

  useEffect(() => {
    let active = true
    setLoadingIdTypes(true)
    setLoadingCountries(true)
    setErrorIdTypes(null)
    setLocationsError(null)
    setFailedRequest(null)

    ;(async () => {
      try {
        const [fetchedIdTypes, fetchedCountries] = await Promise.all([
          getIdTypes(),
          getCountries(),
        ])
        if (!active) return

        setIdTypes(fetchedIdTypes)
        setCountries(fetchedCountries)
        setFormState((state) => {
          if (!state.idType) {
            return state
          }
          const exists = fetchedIdTypes.some((item) => {
            const optionValue = item.code ?? item.id
            return optionValue ? optionValue === state.idType : false
          })
          return exists ? state : { ...state, idType: '' }
        })
      } catch (error) {
        console.error(error)
        if (isAxiosError(error)) {
          console.error(error.response?.data)
        }
        if (!active) return

        setIdTypes([])
        setCountries([])
        setFormState((state) => ({ ...state, idType: '' }))
        setErrorIdTypes('No se pudieron cargar los tipos de documento.')
        setLocationsError('No se pudieron cargar los países. Inténtalo de nuevo.')
        setFailedRequest('countries')
      } finally {
        if (!active) return
        setLoadingIdTypes(false)
        setLoadingCountries(false)
      }
    })()

    return () => {
      active = false
    }
  }, [countryRequestId])

  useEffect(() => {
    if (!selectedCountry) {
      setDepartments([])
      setSelectedDepartment('')
      setCities([])
      setFormState((state) => ({ ...state, homeCity: '' }))
      return
    }

    let active = true
    setLoadingDepartments(true)
    setLocationsError(null)
    setFailedRequest(null)
    setDepartments([])
    setCities([])
    setFormState((state) => ({ ...state, homeCity: '' }))
    ;(async () => {
      try {
        const res = await getDepartments(selectedCountry)
        if (!active) return
        setDepartments(res)
      } catch (error) {
        console.error(error)
        if (isAxiosError(error)) {
          console.error(error.response?.data)
        }
        if (!active) return
        setLocationsError('No se pudieron cargar los departamentos. Inténtalo de nuevo.')
        setFailedRequest('departments')
      } finally {
        if (!active) return
        setLoadingDepartments(false)
      }
    })()

    return () => {
      active = false
    }
  }, [selectedCountry, departmentRequestId])

  useEffect(() => {
    if (!selectedDepartment) {
      setCities([])
      setFormState((state) => ({ ...state, homeCity: '' }))
      return
    }

    let active = true
    setLoadingCities(true)
    setLocationsError(null)
    setFailedRequest(null)
    setCities([])
    setFormState((state) => ({ ...state, homeCity: '' }))
    ;(async () => {
      try {
        const res = await getCities(selectedDepartment)
        if (!active) return
        setCities(res)
      } catch (error) {
        console.error(error)
        if (isAxiosError(error)) {
          console.error(error.response?.data)
        }
        if (!active) return
        setLocationsError('No se pudieron cargar las ciudades. Inténtalo de nuevo.')
        setFailedRequest('cities')
      } finally {
        if (!active) return
        setLoadingCities(false)
      }
    })()

    return () => {
      active = false
    }
  }, [selectedDepartment, cityRequestId])

  const onFieldChange = (e: ChangeEvent<HTMLInputElement>) => {
    const fieldName = e.target.name as keyof CreateUserRequest
    let { value } = e.target

    if (fieldName === 'idNumber') {
      value = value.replace(/\D/g, '')
    } else if (fieldName === 'mobileNumber') {
      value = value.replace(/\D/g, '').slice(0, 10)
    } else if (
      fieldName === 'firstName' ||
      fieldName === 'secondName' ||
      fieldName === 'firstSurname' ||
      fieldName === 'secondSurname'
    ) {
      value = value.replace(/\s+/g, ' ')
    } else if (fieldName === 'email') {
      value = value.replace(/\s+/g, '').toLowerCase()
    }

    setFormState((state) => ({ ...state, [fieldName]: value }))
    setFieldErrors((prev) => {
      if (!prev[fieldName]) return prev
      const { [fieldName]: _removed, ...rest } = prev
      return rest
    })
  }

  const handleBlur = (event: FocusEvent<HTMLInputElement>) => {
    const { name, value } = event.target
    const userFormField = USER_FORM_FIELD_MAP[name]
    if (!userFormField) return

    const overrides = { [userFormField]: value } as Partial<UserForm>
    const { errors } = validateUserForm(buildUserForm(overrides))
    const mappedErrors = mapValidationErrors(errors)
    const targetKey =
      userFormField === 'mobile' ? 'mobileNumber' : (userFormField as FormErrorKey)

    setFieldErrors((prev) => {
      const next = { ...prev }
      const message = mappedErrors[targetKey]

      if (message) {
        next[targetKey] = message
      } else {
        delete next[targetKey]
      }

      return next
    })
  }

  const handleCountryChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const value = event.target.value.trim()
    setLocationsError(null)
    setFailedRequest(null)
    setSelectedCountry(value)
    setSelectedDepartment('')
    setFieldErrors((prev) => {
      const { country, department, homeCity, ...rest } = prev
      return rest
    })
  }

  const handleDepartmentChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const value = event.target.value.trim()
    setLocationsError(null)
    setFailedRequest(null)
    setSelectedDepartment(value)
    setFieldErrors((prev) => {
      const { department, homeCity, ...rest } = prev
      return rest
    })
  }

  const handleCityChange = (event: ChangeEvent<HTMLSelectElement>) => {
    const value = event.target.value.trim()
    setLocationsError(null)
    setFailedRequest(null)
    setFormState((state) => ({ ...state, homeCity: value }))
    setFieldErrors((prev) => {
      const { homeCity, ...rest } = prev
      return rest
    })
  }

  const handleRetryLocations = () => {
    if (failedRequest === 'departments') {
      setDepartmentRequestId((id) => id + 1)
      return
    }

    if (failedRequest === 'cities') {
      setCityRequestId((id) => id + 1)
      return
    }

    setCountryRequestId((id) => id + 1)
  }

  const onSubmit = async (e: FormEvent) => {
    e.preventDefault()
    if (saving) return

    setErr(null)
    setFieldErrors({})

    const trimmedIdType = formState.idType.trim()
    const { errors: validationErrors, cleaned } = validateUserForm(
      buildUserForm({
        countryId: selectedCountry.trim(),
        departmentId: selectedDepartment.trim(),
        cityId: formState.homeCity.trim(),
      }),
    )

    const formData: CreateUserRequest = {
      idType: trimmedIdType,
      idNumber: cleaned.idNumber,
      firstName: cleaned.firstName,
      secondName: cleaned.secondName ?? '',
      firstSurname: cleaned.firstSurname,
      secondSurname: cleaned.secondSurname ?? '',
      homeCity: cleaned.cityId,
      email: cleaned.email,
      mobileNumber: cleaned.mobile,
    }

    setFormState(formData)
    setSelectedCountry(cleaned.countryId)
    setSelectedDepartment(cleaned.departmentId)

    const nextErrors = mapValidationErrors(validationErrors)

    if (!trimmedIdType) nextErrors.idType = REQUIRED_MESSAGE

    if (Object.keys(nextErrors).length > 0) {
      setFieldErrors(nextErrors)
      setErr(REVIEW_FIELDS_MESSAGE)
      return
    }

    const selectedIdTypeOption = idTypes.find((item) => {
      const optionValue = item.code ?? item.id ?? ''
      return optionValue ? optionValue === formData.idType : false
    })

    const registerPayload = {
      documentTypeId: selectedIdTypeOption?.id ?? formData.idType,
      documentTypeName:
        selectedIdTypeOption?.name ?? selectedIdTypeOption?.description ?? undefined,
      documentNumber: formData.idNumber,
      firstName: formData.firstName,
      middleName: formData.secondName || undefined,
      lastName: formData.firstSurname,
      secondLastName: formData.secondSurname || undefined,
      email: formData.email || undefined,
      mobile: formData.mobileNumber || undefined,
      countryId: cleaned.countryId,
      departmentId: cleaned.departmentId,
      cityId: formData.homeCity,
    }

    setSaving(true)
    try {
      const result = await createUser(registerPayload)
      console.debug('Usuario creado con éxito', result)
      // TODO: notificar éxito
      navigate('/users', { replace: true })
    } catch (error: any) {
      const responseData = error?.response?.data as BackendErrorResponse | undefined
      const apiError = (responseData as { data?: BackendErrorResponse } | undefined)?.data ?? responseData

      console.error('Backend error:', error?.response?.status, apiError)

      const message =
        apiError?.userMessage ??
        apiError?.message ??
        error?.userMessage ??
        'Ocurrió un error al registrar el usuario.'

      const details = apiError?.details
      const detailObject =
        details && !Array.isArray(details)
          ? (details as DuplicateFieldDetail)
          : undefined

      let field = error?.duplicateField as string | undefined
      let value = error?.duplicateValue as string | undefined

      if (!field && detailObject) {
        field = detailObject.field
      }

      if (!value && detailObject) {
        value = detailObject.value
      }

      const readable = (f: string) =>
        f === 'identification'
          ? 'número de documento'
          : f === 'phone'
            ? 'número de teléfono'
            : f === 'email'
              ? 'correo'
              : f

      const extra = field && value ? ` (${readable(field)} duplicado: ${value})` : ''

      toast.error(`${message}${extra}`)

      if (field && value) {
        console.info(`Dato duplicado: ${field} = ${value}`)
      }

      setErr(`${message}${extra}`)

      const fieldMap: Record<string, FormErrorKey> = {
        email: 'email',
        phone: 'mobileNumber',
        identification: 'idNumber',
      }

      const mappedField = field ? fieldMap[field] ?? (isFormErrorKey(field) ? field : undefined) : undefined

      if (mappedField) {
        setFieldErrors((prev) => ({
          ...prev,
          [mappedField]: `${message}${extra}`,
        }))

        if (typeof document !== 'undefined') {
          document
            .querySelector<HTMLInputElement | HTMLSelectElement>(`[name="${mappedField}"]`)
            ?.focus()
        }
      } else if (Array.isArray(details)) {
        const backendFieldErrors: Partial<Record<FormErrorKey, string>> = {}

        details.forEach((detail) => {
          const detailField = detail.field
          if (!detailField) return
          if (detailField === 'email' && detail.code === 'duplicate') {
            backendFieldErrors.email = EMAIL_DUPLICATE_MESSAGE
            return
          }
          if (isFormErrorKey(detailField)) {
            backendFieldErrors[detailField] = detail.message ?? REVIEW_FIELDS_MESSAGE
            return
          }
          if (detailField in formData) {
            backendFieldErrors[detailField as FormErrorKey] =
              detail.message ?? REVIEW_FIELDS_MESSAGE
          }
        })

        if (Object.keys(backendFieldErrors).length > 0) {
          setFieldErrors(backendFieldErrors)
          setErr(REVIEW_FIELDS_MESSAGE)
        }
      }
    } finally {
      setSaving(false)
    }
  }

  const hasErrors = Object.keys(fieldErrors).length > 0

  return (
    <main className="page">
      <header className="page-header">
        <div>
          <h1>Registrar usuario</h1>
          <p>
            Completa la información oficial y verifica los datos clave para incorporar nuevos usuarios al
            ecosistema Uco Challenge.
          </p>
        </div>
      </header>

      <form className="form" onSubmit={onSubmit} noValidate>
        <section className="card form-section" aria-labelledby="personal-data">
          <header className="form-section__header">
            <h2 id="personal-data" className="form-section__title">
              Datos personales
            </h2>
            <p className="form-section__description">
              Define la identidad oficial del usuario tal como aparece en los registros de la institución.
            </p>
          </header>

          <div className="form-grid">
            <div
              className={`form-control${fieldErrors.idType ? ' form-control--error' : ''}`}
            >
              <label htmlFor="idType">Tipo de identificación*</label>
              <select
                id="idType"
                name="idType"
                value={formState.idType ?? ''}
                onChange={(event) => {
                  const value = event.target.value
                  setFormState((state) => ({ ...state, idType: value }))
                  setFieldErrors((prev) => {
                    const { idType, ...rest } = prev
                    return rest
                  })
                }}
                disabled={loadingIdTypes || !!errorIdTypes || idTypes.length === 0}
                aria-label="Selecciona el tipo de documento"
                title="Selecciona el tipo de documento"
                required
                className="input select"
                aria-invalid={Boolean(fieldErrors.idType)}
              >
                <option value="">
                  {loadingIdTypes ? 'Cargando tipos...' : 'Selecciona un tipo'}
                </option>
                {idTypes.map((type) => (
                  <option key={type.id ?? type.code} value={type.code ?? type.id ?? ''}>
                    {type.name ?? type.description ?? type.code ?? type.id}
                  </option>
                ))}
              </select>
              {fieldErrors.idType ? (
                <span className="form-error">{fieldErrors.idType}</span>
              ) : null}
              {loadingIdTypes ? (
                <div className="form-helper" role="status" aria-live="polite">
                  <span
                    className="loader__spinner"
                    aria-hidden
                    style={{ width: '24px', height: '24px', borderWidth: '3px' }}
                  />
                  Cargando tipos de documento...
                </div>
              ) : null}
              {errorIdTypes ? (
                <div className="alert alert--info" role="alert">
                  <span aria-hidden>ℹ️</span>
                  <div>
                    <p style={{ margin: 0 }}>{errorIdTypes}</p>
                    <button
                      type="button"
                      className="btn btn-outline"
                      style={{ marginTop: '0.75rem' }}
                      onClick={() => {
                        setCountryRequestId((id) => id + 1)
                      }}
                    >
                      Reintentar
                    </button>
                  </div>
                </div>
              ) : null}
              {!loadingIdTypes && !errorIdTypes && idTypes.length === 0 ? (
                <span className="form-helper">No hay tipos de documento disponibles.</span>
              ) : null}
            </div>

            <label
              className={`form-control${fieldErrors.idNumber ? ' form-control--error' : ''}`}
              htmlFor="idNumber"
            >
              Número de identificación*
              <input
                id="idNumber"
                name="idNumber"
                value={formState.idNumber}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ingresa el número"
                autoComplete="off"
                inputMode="numeric"
                pattern="\\d*"
                required
                aria-invalid={Boolean(fieldErrors.idNumber)}
              />
              {fieldErrors.idNumber ? (
                <span className="form-error">{fieldErrors.idNumber}</span>
              ) : null}
            </label>

            <label
              className={`form-control${fieldErrors.firstName ? ' form-control--error' : ''}`}
              htmlFor="firstName"
            >
              Primer nombre*
              <input
                id="firstName"
                name="firstName"
                value={formState.firstName}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ej. María"
                autoComplete="given-name"
                required
                aria-invalid={Boolean(fieldErrors.firstName)}
              />
              {fieldErrors.firstName ? (
                <span className="form-error">{fieldErrors.firstName}</span>
              ) : null}
            </label>

            <label
              className={`form-control${fieldErrors.secondName ? ' form-control--error' : ''}`}
              htmlFor="secondName"
            >
              Segundo nombre
              <input
                id="secondName"
                name="secondName"
                value={formState.secondName ?? ''}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Opcional"
                autoComplete="given-name"
              />
              {fieldErrors.secondName ? (
                <span className="form-error">{fieldErrors.secondName}</span>
              ) : null}
              <span className="form-helper">Este campo es opcional.</span>
            </label>

            <label
              className={`form-control${fieldErrors.firstSurname ? ' form-control--error' : ''}`}
              htmlFor="firstSurname"
            >
              Primer apellido*
              <input
                id="firstSurname"
                name="firstSurname"
                value={formState.firstSurname}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ej. González"
                autoComplete="family-name"
                required
                aria-invalid={Boolean(fieldErrors.firstSurname)}
              />
              {fieldErrors.firstSurname ? (
                <span className="form-error">{fieldErrors.firstSurname}</span>
              ) : null}
            </label>

            <label
              className={`form-control${fieldErrors.secondSurname ? ' form-control--error' : ''}`}
              htmlFor="secondSurname"
            >
              Segundo apellido
              <input
                id="secondSurname"
                name="secondSurname"
                value={formState.secondSurname ?? ''}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Opcional"
                autoComplete="family-name"
              />
              {fieldErrors.secondSurname ? (
                <span className="form-error">{fieldErrors.secondSurname}</span>
              ) : null}
            </label>
          </div>
        </section>

        <section className="card form-section" aria-labelledby="location-data">
          <header className="form-section__header">
            <h2 id="location-data" className="form-section__title">
              Ubicación
            </h2>
            <p className="form-section__description">
              Selecciona el país, departamento y ciudad que definirán la ubicación oficial del usuario.
            </p>
          </header>

          <div className="form-grid">
            <label
              className={`form-control${fieldErrors.country ? ' form-control--error' : ''}`}
              htmlFor="country"
            >
              País*
              <select
                id="country"
                value={selectedCountry}
                onChange={handleCountryChange}
                disabled={loadingCountries || countries.length === 0}
                aria-busy={loadingCountries}
                required
                aria-invalid={Boolean(fieldErrors.country)}
              >
                <option value="">
                  {loadingCountries ? 'Cargando países...' : 'Selecciona un país'}
                </option>
                {countries.map((country) => (
                  <option key={country.id} value={country.id}>
                    {country.name}
                  </option>
                ))}
              </select>
              {fieldErrors.country ? (
                <span className="form-error">{fieldErrors.country}</span>
              ) : null}
            </label>

            <label
              className={`form-control${fieldErrors.department ? ' form-control--error' : ''}`}
              htmlFor="department"
            >
              Departamento*
              <select
                id="department"
                value={selectedDepartment}
                onChange={handleDepartmentChange}
                disabled={!selectedCountry || loadingDepartments || departments.length === 0}
                aria-busy={loadingDepartments}
                required
                aria-invalid={Boolean(fieldErrors.department)}
              >
                <option value="">
                  {!selectedCountry
                    ? 'Selecciona primero un país'
                    : loadingDepartments
                      ? 'Cargando departamentos...'
                      : 'Selecciona un departamento'}
                </option>
                {departments.map((department) => (
                  <option key={department.id} value={department.id}>
                    {department.name}
                  </option>
                ))}
              </select>
              {fieldErrors.department ? (
                <span className="form-error">{fieldErrors.department}</span>
              ) : null}
            </label>

            <label
              className={`form-control${fieldErrors.homeCity ? ' form-control--error' : ''}`}
              htmlFor="homeCity"
            >
              Ciudad*
              <select
                id="homeCity"
                value={formState.homeCity}
                onChange={handleCityChange}
                disabled={!selectedDepartment || loadingCities || cities.length === 0}
                aria-busy={loadingCities}
                required
                aria-invalid={Boolean(fieldErrors.homeCity)}
              >
                <option value="">
                  {!selectedDepartment
                    ? 'Selecciona primero un departamento'
                    : loadingCities
                      ? 'Cargando ciudades...'
                      : 'Selecciona una ciudad'}
                </option>
                {cities.map((city) => (
                  <option key={city.id} value={city.id}>
                    {city.name}
                  </option>
                ))}
              </select>
              {fieldErrors.homeCity ? (
                <span className="form-error">{fieldErrors.homeCity}</span>
              ) : null}
            </label>
          </div>

          {locationsError && (
            <div className="alert alert--error" role="alert">
              <span aria-hidden>⚠️</span>
              <div>
                <p style={{ margin: 0 }}>{locationsError}</p>
                <button
                  type="button"
                  className="btn btn-outline"
                  style={{ marginTop: '0.75rem' }}
                  onClick={handleRetryLocations}
                >
                  Reintentar carga
                </button>
              </div>
            </div>
          )}
        </section>

        <section className="card form-section" aria-labelledby="contact-data">
          <header className="form-section__header">
            <h2 id="contact-data" className="form-section__title">
              Datos de contacto
            </h2>
            <p className="form-section__description">
              Asegura canales confiables para activar el flujo de verificación por correo y por SMS.
            </p>
          </header>

          <div className="form-grid">
            <label
              className={`form-control${fieldErrors.email ? ' form-control--error' : ''}`}
              htmlFor="email"
            >
              Correo electrónico*
              <input
                id="email"
                type="email"
                name="email"
                value={formState.email}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="usuario@uco.edu.co"
                autoComplete="email"
                pattern={emailPattern}
                required
                aria-invalid={Boolean(fieldErrors.email)}
              />
              {fieldErrors.email ? (
                <span className="form-error">{fieldErrors.email}</span>
              ) : null}
            </label>

            <label
              className={`form-control${fieldErrors.mobileNumber ? ' form-control--error' : ''}`}
              htmlFor="mobileNumber"
            >
              Teléfono móvil*
              <input
                id="mobileNumber"
                name="mobileNumber"
                value={formState.mobileNumber}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ej. 3001234567"
                autoComplete="tel"
                inputMode="numeric"
                pattern={mobilePattern}
                maxLength={10}
                required
                aria-invalid={Boolean(fieldErrors.mobileNumber)}
              />
              {fieldErrors.mobileNumber ? (
                <span className="form-error">{fieldErrors.mobileNumber}</span>
              ) : null}
            </label>
          </div>
        </section>

        {err && (
          <div className="alert alert--error" role="alert">
            <span aria-hidden>⚠️</span>
            <span>{err}</span>
          </div>
        )}

        <div className="form-actions">
          <Link to="/users" className="btn btn-secondary">
            Cancelar
          </Link>
          <button className="btn btn-primary" type="submit" disabled={saving || hasErrors}>
            {saving ? 'Guardando...' : 'Registrar usuario'}
          </button>
        </div>
      </form>
    </main>
  )

}
