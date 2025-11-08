import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react-swc';
import { fileURLToPath, URL } from 'url';

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  resolve: {
    alias: {
      '@': fileURLToPath(new URL('./src', import.meta.url)),
      'react-router-dom': 'react-router-dom',
      'react-router': 'react-router', // Simplificación: solo asegurarse de que el alias apunte correctamente
    },
  },
  optimizeDeps: {
    include: ['react-router', 'react-router-dom'],
  },
});
