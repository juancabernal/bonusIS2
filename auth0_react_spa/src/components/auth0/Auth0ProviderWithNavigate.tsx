// Auth0ProviderWithNavigate.tsx
import { AppState, Auth0Provider } from "@auth0/auth0-react";
import { useNavigate } from "react-router-dom";

type Props = { children: JSX.Element };

export const Auth0ProviderWithNavigate = ({ children }: Props) => {
  const navigate = useNavigate();

  const domain = import.meta.env.VITE_AUTH0_DOMAIN!;
  const clientId = import.meta.env.VITE_AUTH0_CLIENT_ID!;
  const redirectUri = import.meta.env.VITE_AUTH0_CALLBACK_URL!;
  const audience = import.meta.env.VITE_AUTH0_AUDIENCE!;
  const scope = "read:users read:settings"; // ajusta a tus scopes reales

  const onRedirectCallback = (appState: AppState | undefined) => {
    navigate(appState?.returnTo || "/", { replace: true });
  };

  return (
    <Auth0Provider
      domain={domain}
      clientId={clientId}
      authorizationParams={{
        audience,
        scope,
        redirect_uri: redirectUri,
      }}
      cacheLocation="memory"  
      useRefreshTokens={true}   
      useRefreshTokensFallback={false}  
      onRedirectCallback={onRedirectCallback}
    >
      {children}
    </Auth0Provider>
  );
};
