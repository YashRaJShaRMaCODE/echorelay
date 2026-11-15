export function buildDiagnosticReport({
  capturedAt,
  status,
  networkOnline,
  framesSent,
  framesEchoed,
  framesDropped,
}) {
  return JSON.stringify({
    schemaVersion: 1,
    service: "Signal Room",
    capturedAt,
    status,
    networkOnline,
    frameCounts: { sent: framesSent, echoed: framesEchoed, dropped: framesDropped },
    privacy: "Contains no audio, microphone labels, or device identifiers.",
  }, null, 2);
}
