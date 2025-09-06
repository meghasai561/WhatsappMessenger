import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    proxy: {
      '/users': 'http://localhost:8080',
      '/conversations': 'http://localhost:8080',
      '/media': 'http://localhost:8080'
    }
  }
})
