// # Rewrite Vite config to proxy REST + WS to the backend inside Codespaces
cat > web-client/vite.config.js <<'EOF'
import { defineConfig } from 'vite'
import react from '@vitejs/plugin-react'

export default defineConfig({
  plugins: [react()],
  server: {
    port: 5173,
    host: true,
    proxy: {
      '/users': { target: 'http://localhost:8080', changeOrigin: true },
      '/conversations': { target: 'http://localhost:8080', changeOrigin: true },
      '/media': { target: 'http://localhost:8080', changeOrigin: true },
      '/ws': { target: 'http://localhost:8080', ws: true, changeOrigin: true }
    }
  }
})
EOF

// # Point SockJS to the same-origin path (Vite will proxy /ws to the backend)
sed -i "s#new SockJS('http://localhost:8080/ws')#new SockJS('/ws')#g" web-client/src/App.jsx
