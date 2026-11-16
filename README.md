# Signal Room

Signal Room checks the path from a browser microphone to a WebSocket echo server. Choose an input, start a session, and watch the live signal and echoed-frame count.

**Live app:** [real-audio-streaming.vercel.app](https://real-audio-streaming.vercel.app/)

**Backend health:** [real-audio-streaming.onrender.com/health](https://real-audio-streaming.onrender.com/health)

**WebSocket:** `wss://real-audio-streaming.onrender.com/ws/audio`

## What it does

- Requests microphone access only after you select **Start audio**.
- Lets you choose an input device after permission is granted.
- Streams mono audio frames over a binary WebSocket and counts the frames echoed back.
- Shows sent, echoed and locally dropped frames so a slow connection or busy browser audio queue is easier to diagnose.
- Shows when microphone input is quiet, low or clearly detected, so a silent input is easier to spot before relying on echo counts.
- Gives specific permission and device recovery steps for common microphone failures.
- Stops a session if the microphone or internet connection drops; recovery always needs a manual retry.
- Waits up to 75 seconds for the free echo server to wake, then gives a recovery message instead of leaving the page stuck.
- Stops the stream and releases the microphone when you select **Stop** or leave the page.
- Honors reduced-motion preferences and shows connection state to assistive technology.

## Audio and privacy

The backend echoes audio frames for connectivity testing. It does not play the audio back, record it, transcode it, or persist it. Audio is transmitted to the configured server while a session is active; only use non-sensitive test audio. The browser asks for microphone permission before capture starts.

The optional research signup below the tool sends only an email after explicit consent. A separate unchecked option can request related-product links and sends only the product ID after selection. Neither path collects microphone audio, input-device names, or diagnostic counters. Both stay hidden unless the frontend is built with `VITE_LEADHUB_URL` set to the HTTPS LeadHub origin.

The server accepts frames up to 32 KiB, closes a connection that exceeds 30 frames or 256 KiB of audio per rolling second, and allows at most 20 concurrent audio sessions by default. When full, new sockets receive close code `1013` and can be retried later. Set `AUDIO_MAX_CONNECTIONS` to tune the per-instance cap. The origin allowlist controls which browser origins may connect; it is not user authentication or access control. Do not expose this demo as a secure audio service.

## Run locally

Requirements: Java 17+ and Node.js with npm.

Start the backend in one terminal:

```powershell
cd backend
.\mvnw.cmd spring-boot:run
```

Start the frontend in another terminal:

```powershell
cd frontend
npm ci
npm run dev
```

The frontend defaults to `ws://localhost:8080/ws/audio` in development. The backend allows `http://localhost:5173` by default.

## Checks

```powershell
cd backend
.\mvnw.cmd test

cd ..\frontend
npm run lint
npm test
npm run build
```

## Deployment

- **Frontend:** Vercel, with `frontend` as the root directory. `VITE_WS_URL` can override the production WebSocket endpoint.
- **Backend:** Render Blueprint from `render.yaml`. Set `AUDIO_ALLOWED_ORIGINS` to the exact frontend origin; `/health` is the health check.

The frontend uses HTTPS and connects over WSS. Its Content Security Policy permits same-origin styles, the configured WebSocket host, and the `leadhub.onrender.com` origin for consented signup. The backend is configured on Render Free: it can sleep after 15 minutes idle and take about a minute to wake. Render says Free services are for testing and hobby previews, not production; this deployment is only a demo, even though it uses HTTPS/WSS.

## Product readiness

Signal Room currently checks one browser-to-server audio path. It has no user accounts, team workspaces, service guarantees, customer telemetry or paid plans; the origin allowlist is not authentication. It intentionally does not retain audio. Treat this as a developer demo, not a secure or paid audio-streaming service. A commercial developer tool would still need authenticated sessions, abuse/capacity controls, a tested uptime plan and customer validation before billing.

LeadHub is a research channel only. A paid diagnostic/support offer and developer-team product need customer validation before adding accounts or billing.

`free-for.dev` is useful for discovering CI and hosting offers, but a listed free tier is not a production promise. Verify each provider's current limits before using it for customer traffic.
