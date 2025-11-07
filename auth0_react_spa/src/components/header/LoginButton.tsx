import { useAuth0 } from "@auth0/auth0-react";

type Props = {
  onLogin?: () => void;
  nextPath?: string; // por defecto /dashboard
};

const LoginButton = ({ onLogin, nextPath = "/auth/gate" }: Props) => {
  const { loginWithRedirect } = useAuth0();

  const handleLogin = () => {
    onLogin?.();
    console.log("▶️ Login click → returnTo:", nextPath);
    loginWithRedirect({
      appState: { returnTo: nextPath },
    });
  };

  return (
    <button
      onClick={handleLogin}
      className="rounded-lg bg-gradient-to-r from-indigo-500 via-blue-500 to-purple-600 px-4 py-2 text-sm font-medium text-white shadow-md transition hover:opacity-90 focus:outline-none focus:ring-2 focus:ring-purple-600"
    >
      Iniciar sesión
    </button>
  );
};

export default LoginButton;
