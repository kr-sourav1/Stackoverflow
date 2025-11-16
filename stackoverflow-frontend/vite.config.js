import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

// https://vite.dev/config/
export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    strictPort: true,
    proxy: {
      // proxy search requests to backend on port 9090 to avoid CORS during dev
      '/search': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        secure: false,
      },
      '/question': {
        target: 'http://localhost:9090',
        changeOrigin: true,
        secure: false,
      }
    }
  }
})
