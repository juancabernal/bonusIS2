import React, { useCallback, useEffect, useMemo, useRef, useState } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import {
  CatalogItem,
  Page,
  RegisterUserResponse,
  UserCreateInput,
  makeApi,
  VerificationAttemptResponse,

} from "../api/client";
import { User } from "./users";

type Filters = {
  page: number;
  size: number;


};

type UserFormState = {
  idType: string;
  idNumber: string;
  firstName: string;
  secondName: string;
  firstSurname: string;
  secondSurname: string;
  homeCity: string;
  email: string;
  mobileNumber: string;
};
type VerificationModalState = {
  open: boolean;
  userId: string;
  type: "email" | "mobile";
  contact: string;
  code: string;
  loading: boolean;
  status: VerificationAttemptResponse | null;
  error: string | null;
};

const initialFilters: Filters = {
  page: 1,
  size: 10,


};

const emptyVerificationModal = (): VerificationModalState => ({
  open: false,
  userId: "",
  type: "email",
  contact: "",
  code: "",
  loading: false,
  status: null,
  error: null,
});

const emptyForm = (): UserFormState => ({
  idType: "",
  idNumber: "",
  firstName: "",
  secondName: "",
  firstSurname: "",
  secondSurname: "",
  homeCity: "",
  email: "",
  mobileNumber: "",
});

export default function UsersAdmin() {

  function extractBackendMessage(error: any): string {
    const FALLBACK = "No se pudo crear el usuario.";
    // Priorizar mensajes diseñados para el usuario
    try {
      if (error?.response?.data) {
        const data = error.response.data as any;
        return (
          data.userMessage ||
          data.technicalMessage ||
          data.message ||
          data.error ||
          error?.message ||
          FALLBACK
        );
      }
    } catch {
      // ignore
    }

    // fallback por si algo viene roto
    return error?.message || FALLBACK;
  }

  // Util: detectar timeouts de Axios
  // (eliminado: ahora detectamos timeouts directamente comprobando error.code === 'ECONNABORTED' en createUser)

  const { getAccessTokenSilently } = useAuth0();

  const baseURL = import.meta.env.VITE_API_SERVER_URL as string;
  const audience = import.meta.env.VITE_AUTH0_AUDIENCE as string;

  const api = useMemo(
    () =>
      makeApi(baseURL, async () => {
        const token = await getAccessTokenSilently({
          authorizationParams: { audience },
        });
        return token;
      }),
    [baseURL, audience, getAccessTokenSilently]
  );

  const [filters, setFilters] = useState<Filters>(initialFilters);
  const [nameFilter, setNameFilter] = useState<string>("");
  const [emailFilter, setEmailFilter] = useState<string>("");
  const [phoneFilter, setPhoneFilter] = useState<string>("");
  const [idTypeFilter, setIdTypeFilter] = useState<string>("");
  const [pageData, setPageData] = useState<Page<User> | null>(null);
  const [loading, setLoading] = useState(false);
  const [err, setErr] = useState<string | null>(null);

  const [openNew, setOpenNew] = useState(false);
  const [creating, setCreating] = useState(false);
  const [form, setForm] = useState<UserFormState>(emptyForm);
  const [formErr, setFormErr] = useState<string | null>(null);
  const [creationResult, setCreationResult] = useState<RegisterUserResponse | null>(null);
  const [foundAfterError, setFoundAfterError] = useState<RegisterUserResponse | null>(null);
  const [actionLoading, setActionLoading] = useState<Record<string, boolean>>({});
  const [feedbackMessages, setFeedbackMessages] = useState<
    Record<string, { variant: "success" | "error"; message: string }>
  >({});
  // Catálogos y estados relacionados
  const [idTypes, setIdTypes] = useState<CatalogItem[]>([]);
  const [countries, setCountries] = useState<CatalogItem[]>([]);
  const [departments, setDepartments] = useState<CatalogItem[]>([]);
  const [cities, setCities] = useState<CatalogItem[]>([]);
  const [filterCities, setFilterCities] = useState<CatalogItem[]>([]);
  const [selectedCountry, setSelectedCountry] = useState<string>("");
  const [selectedDepartment, setSelectedDepartment] = useState<string>("");
  const [filterDepartment, setFilterDepartment] = useState<string>("");
  const [catalogLoading, setCatalogLoading] = useState(false);
  const [catalogErr, setCatalogErr] = useState<string | null>(null);

  // Evita errores de 'variable no usada' en tiempo de compilación/linter
  useEffect(() => {
    // Referenciamos las variables para que el linter/TS no las marque como sin usar.
    // No hacen nada aquí: su uso real está en los efectos de carga de catálogos.
    void filterCities;
    void filterDepartment;
    void setFilterDepartment;
    void setFilterCities;
  }, [filterCities, filterDepartment]);
  const [countdown, setCountdown] = useState<Record<string, number>>({});
  const timersRef = useRef<Map<string, number>>(new Map());
  const successBannerTimer = useRef<number | null>(null);
  const [verificationModal, setVerificationModal] = useState<VerificationModalState>(emptyVerificationModal());
  const countdownKeyFor = useCallback((userId: string, type: "email" | "mobile") => `${userId}-${type}`, []);
  const clearCountdown = useCallback((key: string) => {
    const existingTimer = timersRef.current.get(key);
    if (existingTimer) {
      clearInterval(existingTimer);
      timersRef.current.delete(key);
    }
    setCountdown((current) => {
      if (!(key in current)) {
        return current;
      }
      const { [key]: _removed, ...rest } = current;
      return rest;
    });
  }, []);



  // 🔁 carga de usuarios paginada contra el backend (con soporte para filtros cliente)
  const fetchUsers = useCallback(async () => {
    // helper que recolecta páginas hasta un tope para búsquedas cliente
    const fetchAllUsersForSearch = async (pageSize: number, maxPages: number) => {
      const collected: User[] = [];
      let page = 0;
      try {
        while (true) {
          const p = await api.listUsers({ page, size: pageSize });
          collected.push(...(p.items ?? []));
          if (page >= (p.totalPages - 1)) break;
          page++;
          if (page >= maxPages) break;
        }
      } catch (err) {
        console.warn({ event: 'fetchAllUsersForSearch:partial', err });
      }
      return collected;
    };

    try {
      setLoading(true);
      setErr(null);
      const page0 = Math.max(filters.page - 1, 0);
      const size = filters.size;

      const nameQ = (nameFilter || "").trim().toLowerCase();
      const emailQ = (emailFilter || "").trim().toLowerCase();
      const phoneQ = (phoneFilter || "").trim().toLowerCase();
      const idTypeQ = (idTypeFilter || "").trim();

      const anyFilter = Boolean(nameQ || emailQ || phoneQ || idTypeQ);
      if (!anyFilter) {
        const data = await api.listUsers({ page: page0, size });
        setPageData(data);
        return;
      }

      const SEARCH_FETCH_SIZE = Number(import.meta.env.VITE_SEARCH_FETCH_SIZE ?? 100);
      const SEARCH_MAX_PAGES = Number(import.meta.env.VITE_SEARCH_MAX_PAGES ?? 20);
      const items = await fetchAllUsersForSearch(Math.max(size, SEARCH_FETCH_SIZE), SEARCH_MAX_PAGES);

      const filtered = items.filter((u) => {
        const name = String(u.fullName ?? "").toLowerCase();
        const email = String(u.email ?? "").toLowerCase();
        const phone = String(u.mobileNumber ?? "").toLowerCase();
        const idType = String(u.idTypeId ?? "");

        const matchesName = nameQ ? name.includes(nameQ) : true;
        const matchesEmail = emailQ ? email.includes(emailQ) : true;
        const matchesPhone = phoneQ ? phone.includes(phoneQ) : true;
        const matchesIdType = idTypeQ ? idType === idTypeQ : true;

        return matchesName && matchesEmail && matchesPhone && matchesIdType;
      });

      const totalItems = filtered.length;
      const totalPages = Math.max(1, Math.ceil(totalItems / size));
      const start = page0 * size;
      const pageItems = filtered.slice(start, start + size);
      setPageData({ items: pageItems, page: filters.page, size, totalItems, totalPages });
    } catch (e: any) {
      setErr(e?.message || "No se pudo cargar usuarios.");
    } finally {
      setLoading(false);
    }
  }, [api, filters, nameFilter, emailFilter, phoneFilter, idTypeFilter]);

  // 🔔 debounce para evitar spam al backend al tipear
  const debounceRef = useRef<number | null>(null);
  useEffect(() => {
    if (debounceRef.current) window.clearTimeout(debounceRef.current);
    debounceRef.current = window.setTimeout(() => {
      fetchUsers();
    }, 350);
    return () => {
      if (debounceRef.current) window.clearTimeout(debounceRef.current);
    };
  }, [fetchUsers, nameFilter, emailFilter, phoneFilter, idTypeFilter]);

  // catálogos
  useEffect(() => {
    let active = true;
    (async () => {
      try {
        setCatalogLoading(true);
        setCatalogErr(null);
        const [idTypeOptions, countryOptions] = await Promise.all([
          api.listIdTypes(),
          api.listCountries(),
        ]);
        if (!active) return;
        setIdTypes(idTypeOptions);
        setCountries(countryOptions);
      } catch (error: any) {
        if (active) setCatalogErr(error?.message || "No se pudieron cargar los catálogos.");
      } finally {
        if (active) setCatalogLoading(false);
      }
    })();
    return () => {
      active = false;
    };
  }, [api]);

  useEffect(() => {
    if (!selectedCountry) {
      setDepartments([]);
      setSelectedDepartment("");
      setCities([]);
      setForm((current) => ({ ...current, homeCity: "" }));
      return;
    }

    let active = true;
    (async () => {
      try {
        setCatalogLoading(true);
        setCatalogErr(null);
        const departmentOptions = await api.listDepartments(selectedCountry);
        if (active) {
          setDepartments(departmentOptions);
        }
      } catch (error: any) {
        if (active) {
          setCatalogErr(error?.message || "No se pudieron cargar los catálogos.");
        }
      } finally {
        if (active) {
          setCatalogLoading(false);
        }
      }
    })();

    return () => {
      active = false;
    };
  }, [selectedCountry, api]);

  useEffect(() => {
    if (!selectedDepartment) {
      setCities([]);
      return;
    }

    let active = true;
    (async () => {
      try {
        setCatalogLoading(true);
        setCatalogErr(null);
        const cityOptions = await api.listCitiesByDepartment(selectedDepartment);
        if (active) {
          setCities(cityOptions);
        }
      } catch (error: any) {
        if (active) {
          setCatalogErr(error?.message || "No se pudieron cargar los catálogos.");
        }
      } finally {
        if (active) {
          setCatalogLoading(false);
        }
      }
    })();

    return () => {
      active = false;
    };
  }, [selectedDepartment, api]);

  useEffect(() => {
    if (!filterDepartment) {
      setFilterCities([]);
      return;
    }

    let active = true;
    (async () => {
      try {
        setCatalogLoading(true);
        setCatalogErr(null);
        const cityOptions = await api.listCitiesByDepartment(filterDepartment);
        if (active) {
          setFilterCities(cityOptions);
        }
      } catch (error: any) {
        if (active) {
          setCatalogErr(error?.message || "No se pudieron cargar los catálogos.");
        }
      } finally {
        if (active) {
          setCatalogLoading(false);
        }
      }
    })();

    return () => {
      active = false;
    };
  }, [filterDepartment, api]);

  // tamaño de página
  // (Eliminado: declaración duplicada de onChangePageSize)

  // paginación
  // (Eliminado: declaración duplicada de nextPage y prevPage)

  // cambios de filtros individuales -> siempre page=1


  // -------- resto (confirmaciones, creación) sin cambios relevantes --------
  // (Removed duplicate startCountdown function)

  const startCountdown = useCallback(
    (key: string, seconds: number) => {
      const sanitizedSeconds = Math.max(Number.isFinite(seconds) ? Math.floor(seconds) : 0, 0);
      clearCountdown(key);
      if (sanitizedSeconds === 0) {
        return;
      }
      setCountdown((current) => ({ ...current, [key]: sanitizedSeconds }));

      const intervalId = window.setInterval(() => {
        setCountdown((current) => {
          const currentValue = current[key] ?? 0;
          const nextValue = Math.max(currentValue - 1, 0);
          const updated = { ...current, [key]: nextValue };
          if (nextValue <= 0) {
            clearInterval(intervalId);
            timersRef.current.delete(key);
            const { [key]: _removed, ...rest } = updated;
            return rest;
          }
          return updated;
        });
      }, 1000);

      timersRef.current.set(key, intervalId);
    },
    [clearCountdown]
  );

  const closeVerificationModal = useCallback(() => {
    setVerificationModal(emptyVerificationModal());
  }, []);



  useEffect(() => {
    return () => {
      timersRef.current.forEach((intervalId) => clearInterval(intervalId));
      timersRef.current.clear();
      if (successBannerTimer.current) {
        clearTimeout(successBannerTimer.current);
        successBannerTimer.current = null;
      }
    };
  }, []);

  // Auto-hide creationResult banner after 5s
  useEffect(() => {
    if (!creationResult) return;
    // clear existing timer
    if (successBannerTimer.current) {
      clearTimeout(successBannerTimer.current);
      successBannerTimer.current = null;
    }
    successBannerTimer.current = window.setTimeout(() => {
      setCreationResult(null);
    }, 5000);

    return () => {
      if (successBannerTimer.current) {
        clearTimeout(successBannerTimer.current);
        successBannerTimer.current = null;
      }
    };
  }, [creationResult]);

  const resetForm = () => {
    setForm(emptyForm());
    setFormErr(null);
    setSelectedCountry("");
    setSelectedDepartment("");
    setDepartments([]);
    setCities([]);
  };

  const handleCloseAndRefreshAfterFound = async () => {
    // usuario detectado en backend: cerrar modal y refrescar lista bajo control del usuario
    setOpenNew(false);
    resetForm();
    setFoundAfterError(null);
    setFilters((f) => ({ ...f, page: 1 }));
    await fetchUsers();
  };

  const handleKeepModalAfterFound = () => {
    // mantener modal abierto y limpiar la nota encontrada
    setFoundAfterError(null);
    setFormErr(null);
  };

  const verificationCountdown = verificationModal.open
    ? countdown[countdownKeyFor(verificationModal.userId, verificationModal.type)] ?? 0
    : 0;
  const isResendingCode = verificationModal.open
    ? Boolean(actionLoading[countdownKeyFor(verificationModal.userId, verificationModal.type)])
    : false;

  // ✅ buildPayload ahora está dentro del componente y puede usar idTypes y cities
  const buildPayload = (form: UserFormState): UserCreateInput => {
    const sanitize = (value?: string) => (value ?? "").trim();
    const optional = (value?: string) => {
      const trimmed = sanitize(value);
      return trimmed.length > 0 ? trimmed : undefined;
    };

    const idTypeItem = idTypes.find((t) => t.id === form.idType);
    const idTypeId = optional(idTypeItem?.id || form.idType);

    return {
      idTypeId,
      idTypeName: idTypeId ? optional(idTypeItem?.name) : undefined,
      idNumber: sanitize(form.idNumber),
      firstName: sanitize(form.firstName),
      middleName: optional(form.secondName),
      lastName: sanitize(form.firstSurname),
      secondLastName: optional(form.secondSurname),
      email: optional(form.email),
      mobile: optional(form.mobileNumber),
      countryId: sanitize(selectedCountry),
      departmentId: sanitize(selectedDepartment),
      cityId: sanitize(form.homeCity),
    };
  };
  // (Removed duplicate fetchUsers and related useEffect)

  const onChangePageSize = (e: React.ChangeEvent<HTMLSelectElement>) => {
    const value = Number(e.target.value);
    setFilters((prev) => ({ ...prev, size: value, page: 1 }));
  };

  const nextPage = () => {
    if (!pageData) return;
    if (filters.page < pageData.totalPages) {
      setFilters((f) => ({ ...f, page: f.page + 1 }));
    }
  };

  const prevPage = () => {
    if (filters.page > 1) {
      setFilters((f) => ({ ...f, page: f.page - 1 }));
    }
  };

  const updateFeedback = (userId: string, payload: { variant: "success" | "error"; message: string } | null) => {
    setFeedbackMessages((prev) => {
      if (!payload) {
        const { [userId]: _removed, ...rest } = prev;
        return rest;
      }
      return { ...prev, [userId]: payload };
    });
  };

  const handleRequestConfirmation = async (userId: string, type: "email" | "mobile") => {
    if (!userId) {
      return;
    }

    const key = countdownKeyFor(userId, type);
    const targetUser = pageData?.items?.find((u) => u.userId === userId);
    if (!targetUser) {
      updateFeedback(userId, { variant: "error", message: "No se encontró el usuario seleccionado." });
      return;
    }
    if (type === "email" && !targetUser.email) {
      updateFeedback(userId, { variant: "error", message: "El usuario no tiene un correo electrónico registrado." });
      return;
    }
    if (type === "mobile" && !targetUser.mobileNumber) {
      updateFeedback(userId, { variant: "error", message: "El usuario no tiene un teléfono móvil registrado." });
      return;
    }

    setActionLoading((prev) => ({ ...prev, [key]: true }));
    updateFeedback(userId, null);
    try {
      const response =
        type === "email"
          ? await api.requestEmailConfirmation(userId)
          : await api.requestMobileConfirmation(userId);
      updateFeedback(userId, { variant: "success", message: response.message });
      startCountdown(key, 0);
      setVerificationModal({
        open: true,
        userId,
        type,
        contact: type === "email" ? targetUser.email : targetUser.mobileNumber || "",
        code: "",
        loading: false,
        status: null,
        error: null,
      });
    } catch (error: any) {
      const message =
        error?.message ||
        (type === "email"
          ? "No fue posible solicitar la validación del correo electrónico."
          : "No fue posible solicitar la validación del teléfono móvil.");
      updateFeedback(userId, { variant: "error", message });

      console.error(`Error solicitando validación de ${type}:`, error);
      closeVerificationModal();
    } finally {
      setActionLoading((prev) => {
        const { [key]: _r, ...rest } = prev;
        return rest;
      });
    }
  };

  const handleVerificationCodeChange = (value: string) => {
    setVerificationModal((prev) => ({ ...prev, code: value, error: null }));
  };

  const handleVerifyCode = async () => {
    if (!verificationModal.open || !verificationModal.userId) {
      return;
    }
    const trimmedCode = verificationModal.code.trim();
    if (!trimmedCode) {
      setVerificationModal((prev) => ({ ...prev, error: "Ingresa el código enviado." }));
      return;
    }

    const key = countdownKeyFor(verificationModal.userId, verificationModal.type);
    setVerificationModal((prev) => ({ ...prev, loading: true, error: null }));

    try {
      const response =
        verificationModal.type === "email"
          ? await api.validateEmailConfirmation(verificationModal.userId, trimmedCode)
          : await api.validateMobileConfirmation(verificationModal.userId, trimmedCode);

      setVerificationModal((prev) => ({
        ...prev,
        loading: false,
        status: response,
        code: response.success ? "" : prev.code,
        error: null,
      }));

      updateFeedback(verificationModal.userId, {
        variant: response.success ? "success" : "error",
        message: response.message,
      });

      if (response.success) {
        clearCountdown(key);
        await fetchUsers();
      }
    } catch (error: any) {
      const message = error?.message || "No fue posible validar el código.";
      setVerificationModal((prev) => ({ ...prev, loading: false, error: message }));
    }
  };


  const validateForm = (state: UserFormState) => {
    if (!state.idType.trim()) return "Selecciona el tipo de identificación.";
    if (!state.idNumber.trim()) return "Ingresa el número de identificación.";
    if (!state.firstName.trim()) return "El primer nombre es obligatorio.";
    if (!state.firstSurname.trim()) return "El primer apellido es obligatorio.";
    if (!selectedCountry.trim()) return "Selecciona el país de residencia.";
    if (!selectedDepartment.trim()) return "Selecciona el departamento de residencia.";
    if (!state.homeCity.trim()) return "Selecciona la ciudad de residencia.";
    if (!state.email.trim()) return "El correo es obligatorio.";
    if (!/^\S+@\S+\.\S+$/.test(state.email.trim())) return "Correo inválido.";

    return null;
  };

  const createUser = async () => {
    const validation = validateForm(form);
    if (validation) {
      setFormErr(validation);
      return;
    }

    // snapshot para verificación post-timeout (declaro fuera del try para usar en catch)
    const snapshot = { email: form.email?.trim(), idNumber: form.idNumber?.trim() };

    try {
      setFormErr(null);
      setCreating(true);
      const payload = buildPayload(form);
      const result = await api.createUser(payload);
      setCreationResult(result);
      setOpenNew(false);
      resetForm();
      setFilters((f) => ({ ...f, page: 1 }));
      await fetchUsers(); // éxito real → refresco
    } catch (error: any) {
      // Si es timeout, intentamos verificar si el usuario quedó creado consultando directamente al backend
      if (error?.code === 'ECONNABORTED') {
        try {
          const found = await api.findUserLocally({ email: snapshot.email, idNumber: snapshot.idNumber });
          if (found) {
            setCreationResult({ userId: found.userId, fullName: found.fullName, email: found.email });
            setFormErr(null);
            setOpenNew(false);
            resetForm();
            setFilters((f) => ({ ...f, page: 1 }));
            await fetchUsers();
            return;
          }
        } catch (_) { /* noop */ }
      }

      // Si el backend responde con status 409 o 400 con mensajes claros
      const status = error?.response?.status;
      const backendMsg = extractBackendMessage(error);
      if (status === 409) {
        // conflicto: correo o documento duplicado
        setFormErr(backendMsg || "Ya existe un usuario con esos datos.");
        return;
      }
      if (status === 400) {
        // validaciones conocidas
        setFormErr(backendMsg || "Datos de entrada inválidos.");
        return;
      }

      // Si el backend devuelve 5xx, muchas veces es un error genérico.
      // Intentamos verificar si el usuario quedó creado consultando directamente al backend.
      if (status && status >= 500) {
        try {
          const found = await api.findUserLocally({ email: snapshot.email, idNumber: snapshot.idNumber });
          if (found) {
            setCreationResult({ userId: found.userId, fullName: found.fullName, email: found.email });
            setFormErr(null);
            setOpenNew(false);
            resetForm();
            setFilters((f) => ({ ...f, page: 1 }));
            await fetchUsers();
            return;
          }
        } catch (lookupErr) {
          console.warn({ event: "createUser:5xx-lookup-failed", err: lookupErr });
        }

        // Si no encontramos nada, mostramos el mensaje genérico y dejamos modal abierto
        setFormErr("Se presentó un error inesperado. Intenta nuevamente.");
        return;
      }

      // fallback
      console.error("Error al crear usuario:", error);
      setFormErr(backendMsg);
    } finally {
      setCreating(false);
    }
  };

  return (
    <section className="space-y-6">
      {/* Encabezado + tamaño */}
      <div className="rounded-2xl border border-gray-800 bg-[#141418] p-4">
        <div className="flex flex-col gap-3 md:flex-row md:items-center md:justify-between">
          <div className="text-sm text-gray-400 md:flex-1 md:pr-6 min-w-0">
            <p className="truncate">{pageData ? `Mostrando ${pageData.items.length} usuarios de ${pageData.totalItems}` : "Sin datos"}</p>
          </div>
          <div className="flex items-center gap-3 w-full md:w-auto">
            <label className="text-sm text-gray-300 flex items-center gap-2">
              Tamaño página
              <select
                name="size"
                value={filters.size}
                onChange={onChangePageSize}
                className="ml-2 rounded-lg border border-gray-700 bg-[#0f0f12] px-2 py-1 text-sm text-gray-100 outline-none focus:border-gray-500"
                aria-label="Tamaño de página"
              >
                <option value={5}>5</option>
                <option value={10}>10</option>
                <option value={20}>20</option>
                <option value={50}>50</option>
              </select>
            </label>

            <div className="flex items-center gap-2 flex-1 min-w-0">
              <div className="relative flex-shrink-0">
                <svg className="absolute left-2 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" viewBox="0 0 24 24" fill="none" aria-hidden>
                  <path d="M21 21l-4.35-4.35" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                  <circle cx="11" cy="11" r="5" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
                <input
                  type="text"
                  value={nameFilter}
                  onChange={(e) => { setNameFilter(e.target.value); setFilters((f) => ({ ...f, page: 1 })); }}
                  placeholder="Nombre"
                  className="w-44 md:w-52 rounded-lg border border-gray-700 bg-[#0f0f12] pl-9 pr-2 py-1 text-sm text-gray-100 outline-none focus:border-indigo-500"
                  aria-label="Buscar por nombre"
                />
              </div>

              <div className="relative flex-shrink-0">
                <svg className="absolute left-2 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" viewBox="0 0 24 24" fill="none" aria-hidden>
                  <path d="M16 12a4 4 0 10-8 0 4 4 0 008 0z" stroke="currentColor" strokeWidth="1.2" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
                <input
                  type="text"
                  value={emailFilter}
                  onChange={(e) => { setEmailFilter(e.target.value); setFilters((f) => ({ ...f, page: 1 })); }}
                  placeholder="Correo"
                  className="w-52 rounded-lg border border-gray-700 bg-[#0f0f12] pl-9 pr-2 py-1 text-sm text-gray-100 outline-none focus:border-indigo-500"
                  aria-label="Buscar por correo"
                />
              </div>

              <div className="relative flex-shrink-0">
                <svg className="absolute left-2 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" viewBox="0 0 24 24" fill="none" aria-hidden>
                  <path d="M2 8.5A5.5 5.5 0 017.5 3h0A5.5 5.5 0 0113 8.5v7A5.5 5.5 0 017.5 21h0A5.5 5.5 0 012 15.5v-7z" stroke="currentColor" strokeWidth="1" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
                <input
                  type="text"
                  value={phoneFilter}
                  onChange={(e) => { setPhoneFilter(e.target.value); setFilters((f) => ({ ...f, page: 1 })); }}
                  placeholder="Celular"
                  className="w-40 rounded-lg border border-gray-700 bg-[#0f0f12] pl-9 pr-2 py-1 text-sm text-gray-100 outline-none focus:border-indigo-500"
                  aria-label="Buscar por celular"
                />
              </div>

              <div className="relative flex-shrink-0">
                <select
                  value={idTypeFilter}
                  onChange={(e) => { setIdTypeFilter(e.target.value); setFilters((f) => ({ ...f, page: 1 })); }}
                  className="w-44 rounded-lg border border-gray-700 bg-[#0f0f12] pl-3 pr-8 py-1 text-sm text-gray-100 outline-none focus:border-indigo-500 appearance-none"
                  aria-label="Filtrar por tipo de identificación"
                >
                  <option value="">Tipo ID</option>
                  {idTypes.map((t) => (
                    <option key={t.id} value={t.id}>{t.name}</option>
                  ))}
                </select>
                <svg className="pointer-events-none absolute right-2 top-1/2 -translate-y-1/2 h-4 w-4 text-gray-400" viewBox="0 0 24 24" fill="none" aria-hidden>
                  <path d="M6 9l6 6 6-6" stroke="currentColor" strokeWidth="1.5" strokeLinecap="round" strokeLinejoin="round" />
                </svg>
              </div>
            </div>

            <div className="flex-shrink-0">
              <button
                onClick={() => { setOpenNew(true); setCreationResult(null); }}
                className="ml-2 rounded-lg bg-gradient-to-r from-indigo-500 via-blue-500 to-purple-600 px-4 py-2 text-sm font-semibold text-white shadow-md transition hover:opacity-95 focus:outline-none focus:ring-2 focus:ring-purple-600"
                aria-label="Crear nuevo usuario"
              >
                <span className="mr-1 font-bold">+</span> Nuevo usuario
              </button>
            </div>
          </div>
        </div>
      </div>
      {/* Banner de éxito (creación) - arriba de la lista */}
      {creationResult && (
        <div className="mb-4 rounded-xl border border-emerald-800 bg-emerald-900/30 px-4 py-3 text-sm text-emerald-200">
          Usuario <strong>{creationResult.fullName}</strong> registrado con ID {creationResult.userId}.
        </div>
      )}

      {/* Tabla de usuarios */}
      <div className="overflow-hidden rounded-2xl border border-gray-800">
        <div className="overflow-x-auto">
          <table className="min-w-full divide-y divide-gray-800">
            <thead className="bg-[#141418]">
              <tr className="text-left text-xs font-semibold uppercase tracking-wider text-gray-400">
                <th className="px-4 py-3">Nombre</th>
                <th className="px-4 py-3">Correo</th>
                <th className="px-4 py-3">Teléfono</th>
                <th className="px-4 py-3">Tipo identificación</th>
                <th className="px-4 py-3">Número identificación</th>
                <th className="px-4 py-3">Confirmaciones</th>
              </tr>
            </thead>
            <tbody className="divide-y divide-gray-800 bg-[#0f0f12]">
              {loading && (
                <tr>
                  <td colSpan={6} className="px-4 py-6 text-center text-sm text-gray-400">Cargando…</td>
                </tr>
              )}

              {!loading && err && (
                <tr>
                  <td colSpan={6} className="px-4 py-6 text-center text-sm text-red-300">{err}</td>
                </tr>
              )}

              {!loading && !err && pageData?.items?.length === 0 && (
                <tr>
                  <td colSpan={6} className="px-4 py-6 text-center text-sm text-gray-400">Sin resultados</td>
                </tr>
              )}

              {!loading && !err && pageData?.items?.map((user) => (
                <tr key={user.userId} className="hover:bg-[#121217]">
                  <td className="px-4 py-3 text-sm text-gray-100">{user.fullName}</td>
                  <td className="px-4 py-3 text-sm text-gray-300">{user.email}</td>
                  <td className="px-4 py-3 text-sm text-gray-300">{user.mobileNumber || "—"}</td>
                  <td className="px-4 py-3 text-sm text-gray-300">
                    {user.idTypeId
                      ? idTypes.find((t) => t.id === user.idTypeId)?.name || "Desconocido"
                      : "—"}
                  </td>
                  <td className="px-4 py-3 text-sm text-gray-300">{user.idNumber || "—"}</td>
                  <td className="px-4 py-3 text-sm text-gray-300">
                    <div className="flex flex-col gap-3">
                      <div className="flex flex-wrap items-center gap-2">
                        <span className={user.emailConfirmed ? "text-emerald-400" : "text-yellow-400"}>
                          Correo {user.emailConfirmed ? "confirmado" : "pendiente"}
                        </span>
                        <span className="text-gray-500">·</span>
                        <span className={user.mobileNumberConfirmed ? "text-emerald-400" : "text-yellow-400"}>
                          Móvil {user.mobileNumberConfirmed ? "confirmado" : "pendiente"}
                        </span>
                      </div>

                      <div className="flex flex-wrap gap-2">
                        <button
                          type="button"
                          onClick={() => handleRequestConfirmation(user.userId, "email")}
                          disabled={Boolean(actionLoading[countdownKeyFor(user.userId, "email")]) || !user.email}
                          className="rounded-lg border border-gray-700 px-3 py-1.5 text-xs font-medium text-gray-100 transition hover:border-gray-500 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          {actionLoading[countdownKeyFor(user.userId, "email")] ? "Enviando…" : "Validar correo"}
                        </button>
                        <button
                          type="button"
                          onClick={() => handleRequestConfirmation(user.userId, "mobile")}
                          disabled={Boolean(actionLoading[countdownKeyFor(user.userId, "mobile")]) || !user.mobileNumber}
                          className="rounded-lg border border-gray-700 px-3 py-1.5 text-xs font-medium text-gray-100 transition hover:border-gray-500 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
                        >
                          {actionLoading[countdownKeyFor(user.userId, "mobile")] ? "Enviando…" : "Validar móvil"}
                        </button>
                      </div>

                      {feedbackMessages[user.userId]?.message && (
                        <p
                          className={`text-xs ${feedbackMessages[user.userId]?.variant === "error" ? "text-rose-400" : "text-emerald-400"
                            }`}
                        >
                          {feedbackMessages[user.userId]?.message}
                        </p>
                      )}
                      {(countdown[countdownKeyFor(user.userId, "email")] || countdown[countdownKeyFor(user.userId, "mobile")]) && (
                        <div className="text-xs text-indigo-300">
                          {countdown[countdownKeyFor(user.userId, "email")] && countdown[countdownKeyFor(user.userId, "email")] > 0 && (
                            <span>
                              Correo: tu código vence en {countdown[countdownKeyFor(user.userId, "email")]}s
                            </span>
                          )}
                          {countdown[countdownKeyFor(user.userId, "email")] &&
                            countdown[countdownKeyFor(user.userId, "email")] > 0 &&
                            countdown[countdownKeyFor(user.userId, "mobile")] &&
                            countdown[countdownKeyFor(user.userId, "mobile")] > 0 && (
                              <span className="mx-2 text-gray-600">·</span>
                            )}
                          {countdown[countdownKeyFor(user.userId, "mobile")] && countdown[countdownKeyFor(user.userId, "mobile")] > 0 && (
                            <span>
                              Teléfono: tu código vence en {countdown[countdownKeyFor(user.userId, "mobile")]}s
                            </span>
                          )}
                        </div>
                      )}
                    </div>
                  </td>
                </tr>
              ))}
            </tbody>
          </table>
        </div>

        {verificationModal.open && (
          <div className="fixed inset-0 z-50 grid place-items-center bg-black/60 px-4">
            <div className="w-full max-w-md rounded-2xl border border-gray-800 bg-[#141418] p-6 shadow-xl">
              <div className="flex items-center justify-between">
                <div>
                  <h2 className="text-lg font-semibold text-white">
                    {verificationModal.type === "email"
                      ? "Validar correo electrónico"
                      : "Validar teléfono móvil"}
                  </h2>
                  <p className="mt-1 text-sm text-gray-300">
                    Ingresa el código enviado a
                    <span className="ml-1 font-medium text-white">
                      {verificationModal.contact || "el contacto registrado"}
                    </span>
                    .
                  </p>
                  {verificationCountdown > 0 && (
                    <p className="mt-2 text-xs text-indigo-300">
                      El código vence en {verificationCountdown}s.
                    </p>
                  )}
                </div>
                <button
                  type="button"
                  onClick={closeVerificationModal}
                  className="rounded-full border border-gray-700 p-2 text-gray-400 transition hover:border-gray-500 hover:text-white"
                >
                  ✕
                </button>
              </div>

              <div className="mt-4 space-y-3">
                <input
                  type="text"
                  inputMode="numeric"
                  maxLength={6}
                  autoFocus
                  value={verificationModal.code}
                  onChange={(event) => handleVerificationCodeChange(event.target.value)}
                  onKeyDown={(event) => {
                    if (event.key === "Enter") {
                      event.preventDefault();
                      handleVerifyCode();
                    }
                  }}
                  className="w-full rounded-lg border border-gray-700 bg-[#0d0d11] px-3 py-2 text-sm text-gray-100 outline-none focus:border-indigo-500 focus:ring-1 focus:ring-indigo-500"
                  placeholder="Código de 6 dígitos"
                />

                {verificationModal.error && (
                  <p className="text-sm text-rose-400">{verificationModal.error}</p>
                )}

                {verificationModal.status && (
                  <div className={`text-sm ${verificationModal.status.success ? "text-emerald-400" : "text-amber-300"}`}>
                    {verificationModal.status.message}
                  </div>
                )}
              </div>

              <div className="mt-6 flex flex-wrap items-center gap-2">
                <button
                  type="button"
                  onClick={handleVerifyCode}
                  disabled={verificationModal.loading}
                  className="rounded-lg bg-indigo-600 px-4 py-2 text-sm font-medium text-white transition hover:bg-indigo-500 disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {verificationModal.loading ? "Validando…" : "Validar código"}
                </button>
                <button
                  type="button"
                  onClick={() => handleRequestConfirmation(verificationModal.userId, verificationModal.type)}
                  disabled={verificationModal.loading || isResendingCode}
                  className="rounded-lg border border-gray-700 px-4 py-2 text-sm font-medium text-gray-200 transition hover:border-gray-500 hover:text-white disabled:cursor-not-allowed disabled:opacity-50"
                >
                  {isResendingCode ? "Reenviando…" : "Reenviar código"}
                </button>
                <button
                  type="button"
                  onClick={closeVerificationModal}
                  className="rounded-lg border border-gray-700 px-4 py-2 text-sm text-gray-300 transition hover:border-gray-500 hover:text-white"
                >
                  Cerrar
                </button>
              </div>
            </div>
          </div>
        )}


        <div className="flex items-center justify-between bg-[#141418] px-4 py-3">
          <div className="text-xs text-gray-400">
            {pageData ? `Mostrando página ${filters.page} de ${pageData.totalPages}` : "—"}
          </div>
          <div className="flex items-center gap-2">
            <button
              onClick={prevPage}
              disabled={!pageData || filters.page <= 1}
              className="rounded-lg border border-gray-700 px-3 py-1.5 text-sm text-gray-200 disabled:opacity-40 hover:text-white hover:border-gray-500"
            >
              ← Anterior
            </button>
            <button
              onClick={nextPage}
              disabled={!pageData || filters.page >= (pageData?.totalPages || 1)}
              className="rounded-lg border border-gray-700 px-3 py-1.5 text-sm text-gray-200 disabled:opacity-40 hover:text-white hover:border-gray-500"
            >
              Siguiente →
            </button>
          </div>
        </div>
      </div>

      {/* Modal de creación */}
      {
        openNew && (
          <div className="fixed inset-0 z-50 grid place-items-center bg-black/50 px-4">
            <div className="w-full max-w-3xl rounded-2xl border border-gray-800 bg-[#141418] p-6">
              <div className="flex items-center justify-between">
                <div>
                  <h3 className="text-lg font-semibold text-white">Registrar nuevo usuario</h3>
                  {catalogErr && <p className="mt-1 text-sm text-yellow-400">{catalogErr}</p>}
                </div>
                <button
                  onClick={() => { setOpenNew(false); resetForm(); setCreationResult(null); }}
                  className="rounded-lg border border-gray-700 px-2 py-1 text-sm text-gray-200 hover:text-white hover:border-gray-500"
                >
                  Cerrar
                </button>
              </div>

              <div className="mt-4 grid grid-cols-1 gap-3 md:grid-cols-2">
                {/* 📄 Campos del formulario de creación */}
                <label className="flex flex-col text-sm text-gray-300">
                  Tipo de identificación *
                  <select
                    value={form.idType}
                    disabled={catalogLoading}
                    onChange={(e) => setForm((f) => ({ ...f, idType: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                  >
                    <option value="">Selecciona…</option>
                    {idTypes.map((opt, idx) => (
                      <option key={`${opt?.id ?? "null"}-${idx}`} value={opt.id}>
                        {opt.name}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Número de identificación *
                  <input
                    value={form.idNumber}
                    onChange={(e) => setForm((f) => ({ ...f, idNumber: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                    placeholder="Ej: 1234567890"
                  />
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Primer nombre *
                  <input
                    value={form.firstName}
                    onChange={(e) => setForm((f) => ({ ...f, firstName: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                    placeholder="Ej: Ana"
                  />
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Segundo nombre
                  <input
                    value={form.secondName}
                    onChange={(e) => setForm((f) => ({ ...f, secondName: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                    placeholder="(opcional)"
                  />
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Primer apellido *
                  <input
                    value={form.firstSurname}
                    onChange={(e) => setForm((f) => ({ ...f, firstSurname: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                    placeholder="Ej: Pérez"
                  />
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Segundo apellido
                  <input
                    value={form.secondSurname}
                    onChange={(e) => setForm((f) => ({ ...f, secondSurname: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                    placeholder="(opcional)"
                  />
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  País *
                  <select
                    value={selectedCountry}
                    disabled={catalogLoading}
                    onChange={(e) => {
                      const country = e.target.value;
                      setSelectedCountry(country);
                      setSelectedDepartment("");
                      setForm((f) => ({ ...f, homeCity: "" }));
                    }}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                  >
                    <option value="">Selecciona…</option>
                    {countries.map((opt) => (
                      <option key={opt.id} value={opt.id}>
                        {opt.name}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Departamento *
                  <select
                    value={selectedDepartment}
                    disabled={catalogLoading || !selectedCountry}
                    onChange={(e) => {
                      const dept = e.target.value;
                      setSelectedDepartment(dept);
                      setForm((f) => ({ ...f, homeCity: "" }));
                    }}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                  >
                    <option value="">Selecciona…</option>
                    {departments.map((opt) => (
                      <option key={opt.id} value={opt.id}>
                        {opt.name}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Ciudad de residencia *
                  <select
                    value={form.homeCity}
                    disabled={catalogLoading || !selectedDepartment}
                    onChange={(e) => setForm((f) => ({ ...f, homeCity: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                  >
                    <option value="">Selecciona…</option>
                    {cities.map((opt) => (
                      <option key={opt.id} value={opt.id}>
                        {opt.name}
                      </option>
                    ))}
                  </select>
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Correo electrónico *
                  <input
                    type="email"
                    value={form.email}
                    onChange={(e) => setForm((f) => ({ ...f, email: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                    placeholder="nombre@dominio.com"
                  />
                </label>

                <label className="flex flex-col text-sm text-gray-300">
                  Teléfono móvil
                  <input
                    value={form.mobileNumber}
                    onChange={(e) => setForm((f) => ({ ...f, mobileNumber: e.target.value }))}
                    className="mt-1 rounded-lg border border-gray-700 bg-[#0f0f12] px-3 py-2 text-sm text-gray-100 outline-none focus:border-gray-500"
                    placeholder="Ej: 3001234567"
                  />
                </label>

              </div>

              {foundAfterError ? (
                <div className="mt-3 rounded-md bg-yellow-900/30 p-3 text-sm text-yellow-100">
                  <p>
                    Parece que el usuario <strong>{foundAfterError.fullName}</strong> fue creado en el servidor (ID {foundAfterError.userId}).
                    No cerré el modal automáticamente.
                  </p>
                  <div className="mt-2 flex gap-2">
                    <button
                      onClick={handleCloseAndRefreshAfterFound}
                      className="rounded-lg bg-emerald-700 px-3 py-1 text-sm font-medium text-white hover:opacity-90"
                    >
                      Cerrar y ver lista
                    </button>
                    <button
                      onClick={handleKeepModalAfterFound}
                      className="rounded-lg border border-yellow-700 px-3 py-1 text-sm font-medium text-yellow-100 hover:opacity-90"
                    >
                      Mantener abierto
                    </button>
                  </div>
                </div>
              ) : formErr && (
                <p className="mt-3 text-sm text-red-300">{formErr}</p>
              )}

              <div className="mt-5 flex items-center justify-end gap-3">
                <button
                  onClick={() => { setOpenNew(false); resetForm(); }}
                  className="rounded-lg border border-gray-700 px-4 py-2 text-sm font-medium text-gray-200 hover:text-white hover:border-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-600"
                >
                  Cancelar
                </button>
                <button
                  onClick={createUser}
                  disabled={creating}
                  className="rounded-lg bg-gradient-to-r from-indigo-500 via-blue-500 to-purple-600 px-4 py-2 text-sm font-medium text-white disabled:opacity-50"
                >
                  {creating ? "Creando…" : "Crear usuario"}
                </button>
              </div>
            </div>
          </div >
        )
      }

      {
        /* removed: moved banner above the users table */
      }
    </section >
  );
}
