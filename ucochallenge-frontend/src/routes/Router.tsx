import { Routes, Route } from 'react-router-dom';
import Home from '../pages/Home';
import Login from '../pages/Login';
import NotAuthorized from '../pages/NotAuthorized';
import Dashboard from '../pages/Dashboard';
import UsersListPage from '../pages/users/UsersListPage';
import UserCreatePage from '../pages/users/UserCreatePage';
import VerifyContactPage from '../pages/VerifyContactPage';
import { RequireAuth, RequireAdmin } from './RoleGuard';

export default function AppRouter() {
  return (
    <Routes>
      {/* Públicas */}
      <Route path="/" element={<Home />} />
      <Route path="/login" element={<Login />} />
      <Route path="/not-authorized" element={<NotAuthorized />} />
      <Route path="/verify" element={<VerifyContactPage />} />

      {/* Protegidas: primero auth, luego admin */}
      <Route element={<RequireAuth />}>
        <Route element={<RequireAdmin />}>
          <Route path="/dashboard" element={<Dashboard />} />
          <Route path="/users" element={<UsersListPage />} />
          <Route path="/users/new" element={<UserCreatePage />} />
        </Route>
      </Route>

      {/* Fallback */}
      <Route path="*" element={<Home />} />
    </Routes>
  );
}
