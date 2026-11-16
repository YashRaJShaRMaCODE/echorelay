# Signal Room

## Browser audio path diagnostic

Signal Room checks whether a browser can capture microphone input, send bounded audio frames over WebSocket, and receive an echo. The user starts the session explicitly and downloads a local diagnostic report.

### Intended user

Frontend, browser, and real-time application developers who need to isolate the basic browser-to-WebSocket audio path before debugging their own product.

### Why try it

- No audio recording or retention.
- Device and network recovery guidance.
- Frame counters and a local JSON report.
- Optional research signup does not receive microphone audio or counters.

### Limits

This is a demo, not a secure conferencing platform, paid audio API, or SLA-backed service. The free host can sleep; the origin allowlist is not authentication; and only one echo path is tested. Use non-sensitive audio.

### Business hypothesis

A privacy-preserving diagnostics tool or fixed-scope WebRTC troubleshooting service may help teams; buyer demand and pricing are unvalidated. Interview developers before adding accounts, paid plans, telemetry, or production claims.
