import { useAuth0 } from "@auth0/auth0-react";

const LogoutButton = ({ onLogout }: { onLogout?: () => void }) => {
  const { logout } = useAuth0();

  const handleLogout = () => {
    if (onLogout) onLogout();
    logout({
      logoutParams: {
        returnTo: window.location.origin, // redirige al inicio
      },
    });
  };

  return (
    <button
      onClick={handleLogout}
      className="rounded-lg bg-gray-800 px-4 py-2 text-sm font-medium text-white shadow-md transition hover:bg-gray-900 focus:outline-none focus:ring-2 focus:ring-gray-600"
    >
      Cerrar sesión
    </button>
  );
};

export default LogoutButton;
