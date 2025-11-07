import { useEffect, useMemo, useState } from "react";
import { useAuth0 } from "@auth0/auth0-react";
import { useNavigate } from "react-router-dom";
import axios from "axios";

let fetchedOnce = false;

export default function AuthGate() {
  const { isAuthenticated, isLoading, getAccessTokenSilently, logout } = useAuth0();
  const navigate = useNavigate();

  const baseURL = import.meta.env.VITE_API_SERVER_URL as string;
  const audience = import.meta.env.VITE_AUTH0_AUDIENCE as string;

  const api = useMemo(() => axios.create({ baseURL }), [baseURL]);

  const [statusMsg, setStatusMsg] = useState("Verificando permisos con el gateway…");
  const [errorMsg, setErrorMsg] = useState<string | null>(null);
  const [denied, setDenied] = useState(false);

  useEffect(() => {
    let active = true;

    (async () => {
      if (isLoading) return;


      if (!isAuthenticated) {
        navigate("/", { replace: true });
        return;
      }

      if (fetchedOnce) {
        return;
      }
      fetchedOnce = true;

      try {
        setStatusMsg("Obteniendo token…");
        const token = await getAccessTokenSilently({
          authorizationParams: { audience },
          // scope: "admin:read",  // ← si tu backend lo valida, lo puedes añadir
          // cacheMode: "off",     // ← si notas que se cachea mal
        });

        const url = "/api/admin/users";
        setStatusMsg("Validando autorización…");

        // 🔸 No esperamos body → pedimos texto plano
        const res = await api.get(url, {
          headers: { Authorization: `Bearer ${token}` },
          validateStatus: () => true,
          responseType: "text",
        });


        if (res.status === 200) {
          navigate("/dashboard", { replace: true });
          return;
        }

        if (res.status === 401) {
          setErrorMsg("Tu sesión no es válida o expiró.");
          navigate("/", { replace: true });
          return;
        }

        if (res.status === 403) {
          setDenied(true);
          setErrorMsg("No tienes permisos para acceder a esta sección.");
          setStatusMsg("Cerrando sesión…");
          setTimeout(async () => {
            await logout({ logoutParams: { returnTo: window.location.origin } });
          }, 5000);
          return;
        }

        // Otros códigos inesperados
        setErrorMsg(`Respuesta inesperada del servidor: ${res.status}`);
        setStatusMsg("Error inesperado");
      } catch (e) {
        if (!active) return;
        setErrorMsg("No se pudo contactar al servidor.");
        setStatusMsg("Error de red");
      }
    })();

    return () => {
      active = false;
    };
  }, [isAuthenticated, isLoading, getAccessTokenSilently, api, audience, navigate, logout, baseURL]);

  return (
    <div className="min-h-screen grid place-items-center bg-[#0f0f12] text-gray-200 px-4">
      <div className="w-full max-w-lg rounded-2xl border border-gray-800 bg-[#141418] p-6 shadow-lg">
        <h1 className="text-xl font-semibold text-white">Autorizando acceso…</h1>
        <p className="mt-2 text-sm text-gray-400">{statusMsg}</p>

        {!errorMsg && (
          <div className="mt-6 h-2 w-full rounded bg-gray-800">
            <div className="h-2 w-2/3 rounded bg-gradient-to-r from-indigo-500 via-blue-500 to-purple-600 animate-pulse" />
          </div>
        )}

        {errorMsg && (
          <div className="mt-6 rounded-xl border border-red-800 bg-red-900/20 p-4">
            <p className="text-sm text-red-200">{errorMsg}</p>
            {denied && (
              <p className="mt-2 text-xs text-red-300">
                Saliendo de la sesión y volviendo al inicio…
              </p>
            )}
          </div>
        )}
      </div>
    </div>
  );
}
