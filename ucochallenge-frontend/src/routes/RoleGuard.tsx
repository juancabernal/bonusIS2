import { Navigate, Outlet, useLocation } from 'react-router-dom';
import { useAuth0 } from '@auth0/auth0-react';
import { userHasRole, Roles } from '../auth/roles';

export const RequireAuth = () => {
  const { isAuthenticated, isLoading } = useAuth0();
  const location = useLocation();
  console.debug('RequireAuth: auth state', { isAuthenticated, isLoading })

  if (isLoading) {
    return <main style={{ padding: 24 }}>Cargando autenticación…</main>;
  }
  if (!isAuthenticated) {
    console.warn('RequireAuth: user not authenticated, redirecting to /login', { from: location })
    return <Navigate to="/login" replace state={{ from: location }} />;
  }
  return <Outlet />;
};

export const RequireAdmin = () => {
  const { user, isLoading } = useAuth0();
  const location = useLocation();
  console.debug('RequireAdmin: user and loading', { user, isLoading })

  if (isLoading) {
    return <main style={{ padding: 24 }}>Verificando permisos…</main>;
  }
  if (!userHasRole(user, Roles.Admin)) {
    console.warn('RequireAdmin: user missing admin role -> redirect to login unauthorized', { user })
    return <Navigate to="/login" replace state={{ from: location, unauthorized: true }} />;
  }
  return <Outlet />;
};

export default {};
