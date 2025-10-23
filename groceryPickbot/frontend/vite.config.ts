import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

export default defineConfig({
  plugins: [react()],
  build: {
    outDir: 'dist'
  },
  server: {
    port: 3000,
    proxy: {
      '/products': 'http://localhost:8080',
      '/orders': 'http://localhost:8080',
      '/routes': 'http://localhost:8080'
    }
  }
});