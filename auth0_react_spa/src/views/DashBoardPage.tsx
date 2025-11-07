// src/views/DashboardPage.tsx

import { useAuth0 } from "@auth0/auth0-react";
import { Navigate } from "react-router-dom";
import Header from "../components/header/Header";
import UsersAdmin from "../types/UsersAdmins";

export default function DashboardPage() {
  const { isAuthenticated, isLoading, user } = useAuth0();

  if (isLoading) {
    return (
      <div className="min-h-screen grid place-items-center bg-[#0f0f12] text-gray-200">
        <div className="animate-pulse rounded-xl bg-[#141418] px-6 py-4">
          Cargando sesión…
        </div>
      </div>
    );
  }

  if (!isAuthenticated) {
    return <Navigate to="/" replace />;
  }

  return (
    <>
      <Header subtitle="Dashboard" isAuthenticated={isAuthenticated} />

      <div className="min-h-screen bg-[#0f0f12] text-gray-100">
        <main className="mx-auto max-w-6xl px-4 pb-20 pt-10 space-y-8">
          <header className="rounded-2xl border border-gray-800 bg-[#141418] p-6">
            <h1 className="text-2xl font-bold text-white">Panel de Administrador</h1>
            <p className="mt-2 text-sm text-gray-400">
              Bienvenido{user?.name ? `, ${user.name}` : ""}. Acceso concedido por el gateway.
            </p>
          </header>

          <UsersAdmin />

          <footer className="mt-10 rounded-2xl border border-gray-800 bg-[#141418] p-6 text-center text-sm text-gray-500">
            © {new Date().getFullYear()} Uco Challenge · Dashboard
          </footer>
        </main>
      </div>
    </>
  );
}
