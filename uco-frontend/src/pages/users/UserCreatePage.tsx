import { ChangeEvent, FocusEvent, FormEvent, useEffect, useState } from 'react'
import { Link, useNavigate } from 'react-router-dom'
import { createUser } from '../../api/users'
import { toast } from 'react-toastify'
import { useIdTypes } from '@/hooks/useIdTypes'
import { useCountries } from '@/hooks/useCountries'
import { useDepartments } from '@/hooks/useDepartments'
import { useCities } from '@/hooks/useCities'
import { EMAIL_REGEX, MOBILE_CO_REGEX, validateUserForm, type UserForm } from '@/utils/validators'
import styles from './UserCreatePage.module.css'

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

  const [selectedCountry, setSelectedCountry] = useState('')
  const [selectedDepartment, setSelectedDepartment] = useState('')

  const {
    data: idTypes,
    loading: loadingIdTypes,
    error: idTypesError,
    refresh: refreshIdTypes,
    lastUpdated: idTypesLastUpdated,
  } = useIdTypes()

  // show a brief badge when idTypes refreshes with new data
  const [showIdTypesBadge, setShowIdTypesBadge] = useState(false)
  useEffect(() => {
    if (!idTypesLastUpdated) return
    setShowIdTypesBadge(true)
    const t = setTimeout(() => setShowIdTypesBadge(false), 1500)
    return () => clearTimeout(t)
  }, [idTypesLastUpdated])

  const {
    data: countries,
    loading: loadingCountries,
    error: countriesError,
    refresh: refreshCountries,
    lastUpdated: countriesLastUpdated,
  } = useCountries()

  const [showCountriesBadge, setShowCountriesBadge] = useState(false)
  useEffect(() => {
    if (!countriesLastUpdated) return
    setShowCountriesBadge(true)
    const t = setTimeout(() => setShowCountriesBadge(false), 1500)
    return () => clearTimeout(t)
  }, [countriesLastUpdated])

  const {
    data: departments,
    loading: loadingDepartments,
    error: departmentsError,
    refresh: refreshDepartments,
    lastUpdated: departmentsLastUpdated,
  } = useDepartments(selectedCountry)

  const [showDepartmentsBadge, setShowDepartmentsBadge] = useState(false)
  useEffect(() => {
    if (!departmentsLastUpdated) return
    setShowDepartmentsBadge(true)
    const t = setTimeout(() => setShowDepartmentsBadge(false), 1500)
    return () => clearTimeout(t)
  }, [departmentsLastUpdated])

  const {
    data: cities,
    loading: loadingCities,
    error: citiesError,
    refresh: refreshCities,
    lastUpdated: citiesLastUpdated,
  } = useCities(selectedDepartment)

  const [showCitiesBadge, setShowCitiesBadge] = useState(false)
  useEffect(() => {
    if (!citiesLastUpdated) return
    setShowCitiesBadge(true)
    const t = setTimeout(() => setShowCitiesBadge(false), 1500)
    return () => clearTimeout(t)
  }, [citiesLastUpdated])

  const errorIdTypes = idTypesError

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
    setFormState((state) => {
      if (!state.idType) {
        return state
      }

      const exists = idTypes.some((item) => {
        const optionValue = item.code ?? item.id
        return optionValue ? optionValue === state.idType : false
      })

      return exists ? state : { ...state, idType: '' }
    })
  }, [idTypes])

  useEffect(() => {
    if (countriesError) {
      setLocationsError('No se pudieron cargar los países. Inténtalo de nuevo.')
      setFailedRequest('countries')
      return
    }

    if (departmentsError) {
      setLocationsError('No se pudieron cargar los departamentos. Inténtalo de nuevo.')
      setFailedRequest('departments')
      return
    }

    if (citiesError) {
      setLocationsError('No se pudieron cargar las ciudades. Inténtalo de nuevo.')
      setFailedRequest('cities')
      return
    }

    setLocationsError(null)
    setFailedRequest(null)
  }, [countriesError, departmentsError, citiesError])

  useEffect(() => {
    setSelectedDepartment('')
    setFormState((state) => ({ ...state, homeCity: '' }))

    setFieldErrors((prev) => {
      const { country, department, homeCity, ...rest } = prev
      return rest
    })
  }, [selectedCountry])

  useEffect(() => {
    if (!selectedDepartment) {
      setFormState((state) => ({ ...state, homeCity: '' }))
      return
    }

    setFieldErrors((prev) => {
      const { department, homeCity, ...rest } = prev
      return rest
    })
  }, [selectedDepartment])

  useEffect(() => {
    if (!selectedCountry) {
      return
    }

    const exists = countries.some((country) => country.id === selectedCountry)
    if (!exists) {
      setSelectedCountry('')
    }
  }, [countries, selectedCountry])

  useEffect(() => {
    if (!selectedDepartment) {
      return
    }

    const exists = departments.some((department) => department.id === selectedDepartment)
    if (!exists) {
      setSelectedDepartment('')
    }
  }, [departments, selectedDepartment])

  useEffect(() => {
    setFormState((state) => {
      if (!state.homeCity) {
        return state
      }

      const exists = cities.some((city) => city.id === state.homeCity)
      return exists ? state : { ...state, homeCity: '' }
    })
  }, [cities])

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
    setFormState((state) => ({ ...state, homeCity: '' }))
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
    setFormState((state) => ({ ...state, homeCity: '' }))
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
    setLocationsError(null)
    setFailedRequest(null)

    if (failedRequest === 'departments') {
      refreshDepartments()
      return
    }

    if (failedRequest === 'cities') {
      refreshCities()
      return
    }

    refreshCountries()
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
      toast.success('Usuario registrado correctamente')
      navigate('/users', {
        replace: true,
        state: {
          refresh: Date.now(),
          feedback: { type: 'success', message: 'Usuario registrado correctamente.' },
        },
      })
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

  return (
    <main className={`page ${styles.page}`}>
      <header className={styles.header}>
        <h1>Registrar usuario</h1>
        <p>
          Completa la información oficial y verifica los datos clave para incorporar nuevos usuarios al
          ecosistema UCO Challenge.
        </p>
      </header>

      {err ? (
        <div className={styles.alert} role="alert">
          <span aria-hidden>⚠</span>
          <span>{err}</span>
        </div>
      ) : null}

      <form className={styles.form} onSubmit={onSubmit} noValidate>
        <section className={styles.section} aria-labelledby="identity-data">
          <div className={styles.sectionHeader}>
            <h2 id="identity-data" className={styles.sectionTitle}>
              Datos de identificación
            </h2>
            <p className={styles.sectionDescription}>
              Selecciona el tipo de documento y número oficial que identifican al usuario.
            </p>
          </div>

          <div className={styles.fieldGrid}>
            <div className={`${styles.field} ${fieldErrors.idType ? styles.fieldError : ''}`}>
              <span className={styles.label}>
                Tipo de identificación*
                {showIdTypesBadge ? (
                  <span className={`${styles.updateBadge} ${styles.updateBadgeBlink}`} aria-hidden>
                    Actualizado
                  </span>
                ) : null}
              </span>
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
                className={styles.control}
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
              {fieldErrors.idType ? <span className={styles.error}>{fieldErrors.idType}</span> : null}
              {loadingIdTypes ? (
                <div className={styles.loadingHint} role="status" aria-live="polite">
                  <span className="loader__spinner" aria-hidden style={{ width: '22px', height: '22px', borderWidth: '3px' }} />
                  Cargando tipos de documento...
                </div>
              ) : null}
              {errorIdTypes ? (
                <div className={styles.inlineAlert} role="alert">
                  <span>{errorIdTypes}</span>
                  <button
                    type="button"
                    className={`button button--ghost ${styles.retryButton}`.trim()}
                    onClick={() => {
                      refreshIdTypes()
                    }}
                  >
                    Reintentar
                  </button>
                </div>
              ) : null}
              {!loadingIdTypes && !errorIdTypes && idTypes.length === 0 ? (
                <span className={styles.helper}>No hay tipos de documento disponibles.</span>
              ) : null}
            </div>

            <div className={`${styles.field} ${fieldErrors.idNumber ? styles.fieldError : ''}`}>
              <span className={styles.label}>Número de identificación*</span>
              <input
                id="idNumber"
                name="idNumber"
                value={formState.idNumber}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ej. 1234567890"
                inputMode="numeric"
                autoComplete="off"
                required
                className={styles.control}
                aria-invalid={Boolean(fieldErrors.idNumber)}
              />
              {fieldErrors.idNumber ? <span className={styles.error}>{fieldErrors.idNumber}</span> : null}
            </div>
          </div>
        </section>

        <section className={styles.section} aria-labelledby="personal-data">
          <div className={styles.sectionHeader}>
            <h2 id="personal-data" className={styles.sectionTitle}>
              Datos personales
            </h2>
            <p className={styles.sectionDescription}>
              Define los nombres y apellidos tal como aparecen en la documentación oficial.
            </p>
          </div>

          <div className={styles.fieldGrid}>
            <div className={`${styles.field} ${fieldErrors.firstName ? styles.fieldError : ''}`}>
              <span className={styles.label}>Primer nombre*</span>
              <input
                id="firstName"
                name="firstName"
                value={formState.firstName}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ej. Ana"
                autoComplete="given-name"
                required
                className={styles.control}
                aria-invalid={Boolean(fieldErrors.firstName)}
              />
              {fieldErrors.firstName ? <span className={styles.error}>{fieldErrors.firstName}</span> : null}
            </div>

            <div className={`${styles.field} ${fieldErrors.secondName ? styles.fieldError : ''}`}>
              <span className={styles.label}>Segundo nombre</span>
              <input
                id="secondName"
                name="secondName"
                value={formState.secondName ?? ''}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Opcional"
                autoComplete="given-name"
                className={styles.control}
              />
              {fieldErrors.secondName ? <span className={styles.error}>{fieldErrors.secondName}</span> : null}
              <span className={styles.helper}>Este campo es opcional.</span>
            </div>

            <div className={`${styles.field} ${fieldErrors.firstSurname ? styles.fieldError : ''}`}>
              <span className={styles.label}>Primer apellido*</span>
              <input
                id="firstSurname"
                name="firstSurname"
                value={formState.firstSurname}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ej. González"
                autoComplete="family-name"
                required
                className={styles.control}
                aria-invalid={Boolean(fieldErrors.firstSurname)}
              />
              {fieldErrors.firstSurname ? <span className={styles.error}>{fieldErrors.firstSurname}</span> : null}
            </div>

            <div className={`${styles.field} ${fieldErrors.secondSurname ? styles.fieldError : ''}`}>
              <span className={styles.label}>Segundo apellido</span>
              <input
                id="secondSurname"
                name="secondSurname"
                value={formState.secondSurname ?? ''}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Opcional"
                autoComplete="family-name"
                className={styles.control}
              />
              {fieldErrors.secondSurname ? <span className={styles.error}>{fieldErrors.secondSurname}</span> : null}
              <span className={styles.helper}>Este campo es opcional.</span>
            </div>
          </div>
        </section>

        <section className={styles.section} aria-labelledby="location-data">
          <div className={styles.sectionHeader}>
            <h2 id="location-data" className={styles.sectionTitle}>
              Ubicación
            </h2>
            <p className={styles.sectionDescription}>
              Selecciona país, departamento y ciudad para asociar la residencia del usuario.
            </p>
          </div>

          <div className={styles.fieldGrid}>
            <div className={`${styles.field} ${fieldErrors.country ? styles.fieldError : ''}`}>
              <span className={styles.label}>
                País*
                {showCountriesBadge ? (
                  <span className={`${styles.updateBadge} ${styles.updateBadgeBlink}`} aria-hidden>
                    Actualizado
                  </span>
                ) : null}
              </span>
              <select
                id="country"
                value={selectedCountry}
                onChange={handleCountryChange}
                disabled={loadingCountries || countries.length === 0}
                aria-busy={loadingCountries}
                required
                className={styles.control}
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
              {fieldErrors.country ? <span className={styles.error}>{fieldErrors.country}</span> : null}
            </div>

            <div className={`${styles.field} ${fieldErrors.department ? styles.fieldError : ''}`}>
              <span className={styles.label}>
                Departamento*
                {showDepartmentsBadge ? (
                  <span className={`${styles.updateBadge} ${styles.updateBadgeBlink}`} aria-hidden>
                    Actualizado
                  </span>
                ) : null}
              </span>
              <select
                id="department"
                value={selectedDepartment}
                onChange={handleDepartmentChange}
                disabled={!selectedCountry || loadingDepartments || departments.length === 0}
                aria-busy={loadingDepartments}
                required
                className={styles.control}
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
              {fieldErrors.department ? <span className={styles.error}>{fieldErrors.department}</span> : null}
            </div>

            <div className={`${styles.field} ${fieldErrors.homeCity ? styles.fieldError : ''}`}>
              <span className={styles.label}>
                Ciudad*
                {showCitiesBadge ? (
                  <span className={`${styles.updateBadge} ${styles.updateBadgeBlink}`} aria-hidden>
                    Actualizado
                  </span>
                ) : null}
              </span>
              <select
                id="homeCity"
                value={formState.homeCity}
                onChange={handleCityChange}
                disabled={!selectedDepartment || loadingCities || cities.length === 0}
                aria-busy={loadingCities}
                required
                className={styles.control}
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
              {fieldErrors.homeCity ? <span className={styles.error}>{fieldErrors.homeCity}</span> : null}
            </div>
          </div>

          {locationsError ? (
            <div className={styles.inlineAlert} role="alert">
              <span>{locationsError}</span>
              <button
                type="button"
                className={`button button--ghost ${styles.retryButton}`.trim()}
                onClick={handleRetryLocations}
              >
                Reintentar
              </button>
            </div>
          ) : null}
        </section>

        <section className={styles.section} aria-labelledby="contact-data">
          <div className={styles.sectionHeader}>
            <h2 id="contact-data" className={styles.sectionTitle}>
              Contacto
            </h2>
            <p className={styles.sectionDescription}>
              Datos utilizados para los flujos de verificación vía correo y SMS.
            </p>
          </div>

          <div className={styles.fieldGrid}>
            <div className={`${styles.field} ${fieldErrors.email ? styles.fieldError : ''}`}>
              <span className={styles.label}>Correo electrónico</span>
              <input
                id="email"
                name="email"
                type="email"
                value={formState.email}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="usuario@uco.edu.co"
                autoComplete="email"
                className={styles.control}
                aria-invalid={Boolean(fieldErrors.email)}
                pattern={emailPattern}
              />
              {fieldErrors.email ? <span className={styles.error}>{fieldErrors.email}</span> : null}
              <span className={styles.helper}>Utiliza un correo institucional o de contacto directo.</span>
            </div>

            <div className={`${styles.field} ${fieldErrors.mobileNumber ? styles.fieldError : ''}`}>
              <span className={styles.label}>Número de móvil</span>
              <input
                id="mobileNumber"
                name="mobileNumber"
                value={formState.mobileNumber}
                onChange={onFieldChange}
                onBlur={handleBlur}
                placeholder="Ej. 3001234567"
                inputMode="numeric"
                pattern={mobilePattern}
                className={styles.control}
                aria-invalid={Boolean(fieldErrors.mobileNumber)}
              />
              {fieldErrors.mobileNumber ? <span className={styles.error}>{fieldErrors.mobileNumber}</span> : null}
              <span className={styles.helper}>Formato 10 dígitos. Utilizado para verificación vía SMS.</span>
            </div>
          </div>
        </section>

        <div className={styles.actions}>
          <Link className="button button--ghost" to="/users">
            Cancelar
          </Link>
          <button className="button button--primary" type="submit" disabled={saving} aria-busy={saving}>
            {saving ? 'Registrando...' : 'Registrar usuario'}
          </button>
        </div>
      </form>
    </main>
  )

}
