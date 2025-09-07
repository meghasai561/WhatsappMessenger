import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
    proxy: {
      '/users': { target: 'https://shiny-guacamole-jp5jqrv7xj43pjq9-8080.app.github.dev', changeOrigin: true },
      '/conversations': { target: 'https://shiny-guacamole-jp5jqrv7xj43pjq9-8080.app.github.dev', changeOrigin: true },
      '/media': { target: 'https://shiny-guacamole-jp5jqrv7xj43pjq9-8080.app.github.dev', changeOrigin: true },
      '/api': {target:'https://shiny-guacamole-jp5jqrv7xj43pjq9-8080.app.github.dev',changeOrigin: true,secure: false},
      '/ws': { target: 'wss://shiny-guacamole-jp5jqrv7xj43pjq9-8080.app.github.dev',changeOrigin: true,secure: false}
    }
  },
  define: {
    global: 'window'
  }
})
