import { useNavigate } from "react-router-dom";

const ProfileButton = () => {
  const navigate = useNavigate();

  return (
    <button
      onClick={() => navigate("/cliente/perfil")}
      className="rounded-lg border border-gray-700 px-4 py-2 text-sm font-medium text-gray-200 transition hover:text-white hover:border-gray-500 focus:outline-none focus:ring-2 focus:ring-purple-600"
    >
      Perfil
    </button>
  );
};

export default ProfileButton;
