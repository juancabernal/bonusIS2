import { Route, Routes } from "react-router-dom";

import Home from "./views/Home";
import AdminPage from "./views/AdminPage";
import ClientPage from "./views/ClientPage";
import ErrorPage from "./views/ErrorPage";
import ClientProfilePage from "./views/ClientProfilePage";
import CallbackPage from "./components/auth0/CallbackPage";
import { AuthenticationGuard } from "./components/auth0/AuthenticationGuard";
import DashboardPage from "./views/DashBoardPage";
import AuthGate from "./views/AuthGate";

function App() {
  return (
    <>
      <Routes>
        <Route path="/" element={<Home />} />
        <Route
          path="/admin"
          element={<AuthenticationGuard component={AdminPage} />}
        />
        <Route
          path="/cliente"
          element={<AuthenticationGuard component={ClientPage} />}
        />
        <Route
          path="/auth/gate"
          element={<AuthenticationGuard component={AuthGate} />}
        />
        <Route
          path="/dashboard"
          element={<AuthenticationGuard component={DashboardPage} />}
        />
        <Route
          path="/cliente/perfil"
          element={<AuthenticationGuard component={ClientProfilePage} />}
        />
        <Route path="/callback" element={<CallbackPage />} />
        <Route path="*" element={<ErrorPage />} />
      </Routes>
    </>
  );
}

export default App;
