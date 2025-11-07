// src/components/layout/Header.tsx
import LoginButton from "./LoginButton";
import LogoutButton from "./LogoutButton";
import ProfileButton from "./ProfileButton";

type HeaderProps = {
  subtitle?: string;         // "Dashboard" | "Inicio" | etc.
  isAuthenticated?: boolean; // cambia botón Login/Logout
  onLogin?: () => void;
  onLogout?: () => void;
};

export default function Header({
  subtitle,
  isAuthenticated = false,
  onLogin,
  onLogout,
}: HeaderProps) {
  return (
    <header className="sticky top-0 z-20 border-b border-gray-800 bg-[#0f0f12]/95 backdrop-blur-md">
      <nav className="mx-auto flex h-16 max-w-6xl items-center justify-between px-4 text-gray-100">
        {/* Brand */}
        <div className="flex items-center gap-3">
          {/* Icono circular con gradiente */}
          <div className="grid h-9 w-9 place-items-center rounded-xl bg-gradient-to-r from-indigo-500 via-blue-500 to-purple-600 text-white shadow-md">
            <svg
              viewBox="0 0 24 24"
              className="h-5 w-5"
              fill="currentColor"
              aria-hidden="true"
            >
              <path d="M12 2a10 10 0 1 0 6.32 17.9l3.39.94a1 1 0 0 0 1.21-1.04l-.47-3.62A10 10 0 0 0 12 2zm-1 5h2v6h-2V7zm1 10a1.5 1.5 0 1 1 0-3 1.5 1.5 0 0 1 0 3z" />
            </svg>
          </div>

          {/* Títulos */}
          <div className="flex flex-col leading-tight">
            <span className="text-lg font-semibold tracking-tight text-white">
              Uco Challenge
            </span>
            {subtitle && (
              <span className="text-xs text-gray-400">{subtitle}</span>
            )}
          </div>
        </div>

        {/* Botón de autenticación */}
        <div>
          {isAuthenticated ? (
            <div className="flex items-center gap-3">
              <LogoutButton onLogout={onLogout} />
              <ProfileButton />
            </div>
          ) : (
            <div className="flex items-center gap-3">
              <LoginButton onLogin={onLogin} nextPath="/auth/gate" />
            </div>
          )}
        </div>
      </nav>
    </header>
  );
}
