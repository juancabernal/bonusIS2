import React from 'react';
import ReactDOM from 'react-dom/client';
import { BrowserRouter } from 'react-router-dom';
import Auth0ProviderWithNavigate from './auth/Auth0ProviderWithNavigate';
import App from './App';
import './index.css';

// Debug temporal: log cuando React recibe un "type" undefined (mejorado)
const _createElement = (React as any).createElement;
(React as any).createElement = function (type: any, props: any, ...children: any[]) {
  if (typeof type === 'undefined' || type === undefined) {
    const stack = new Error().stack || '';
    console.error('DEBUG: React.createElement recibió type=undefined', {
      props,
      type,
      stack
    });
    // Muestra las primeras líneas de la pila para identificar el archivo/fila
    const lines = stack.split('\n').map(l => l.trim()).filter(Boolean);
    console.error('DEBUG: stack lines (primeras 8):', lines.slice(0, 8));
  } else if (typeof type === 'object' && type !== null && !('$$typeof' in type)) {
    console.warn('DEBUG: React.createElement recibió un object no esperado como type', type);
  }
  return _createElement.apply(this, [type, props, ...children]);
};

ReactDOM.createRoot(document.getElementById('root')!).render(
  <React.StrictMode>
    <BrowserRouter>
      <Auth0ProviderWithNavigate>
        <App />
      </Auth0ProviderWithNavigate>
    </BrowserRouter>
  </React.StrictMode>
);
