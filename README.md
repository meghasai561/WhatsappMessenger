# Messenger Demo (Run in the Browser — no local installs)

This is a **minimal, runnable** WhatsApp‑style messenger you can run entirely in the cloud (GitHub Codespaces or Gitpod). It includes:

- **Java 21 Spring Boot** backend (WebSocket + REST). Uses **H2** in-memory DB for the demo (no installs). 
- **React (Vite)** web client that runs in the browser. 
- Optional **docker-compose** for Postgres/Redis if you later want persistence.
- A **devcontainer** so Codespaces boots with Java 21 + Node + Maven preinstalled.

> Designed for zero local setup. Open in Codespaces/Gitpod and press run.

## Quickstart (GitHub Codespaces)

1. Push this repo to GitHub.
2. Click **Code → Create codespace on main**.
3. When it opens, run these in the codespace terminal:

```bash
# Terminal 1: start backend
cd server
./mvnw spring-boot:run

# Terminal 2: start web client
cd ../web-client
npm install
npm run dev
```

4. In Codespaces, click the forwarded port for the Vite dev server (usually **5173**) to open the app in your browser.
5. Open another browser tab/window to simulate a second user.

### Gitpod

Click "New Workspace" in Gitpod for this repo, then run the same commands as above.

## What works in this demo

- Create direct or group conversations.
- Send/receive text messages in real time (WebSocket).
- Forward messages.
- Attach and send images/video/audio (stored on the server filesystem in `/tmp/uploads`), then rendered/downloaded by the client.
- Delivery status (sent/delivered/read) simplified for demo.
- Basic typing indicator & presence (ephemeral, demo‑level).

> This demo uses **H2 in-memory** storage. Restarting the backend clears data. For persistence, switch to `docker-compose` with Postgres/Redis (included stubs).

## Default URLs

- Backend: `http://localhost:8080`
- Web client: `http://localhost:5173`

## Notes

- Authentication is simplified (demo tokens). For production, add JWT + refresh, database migrations, etc.
- End‑to‑end encryption, push notifications, and calls are stubbed with clear extension points in code comments.
