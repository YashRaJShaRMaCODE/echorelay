import assert from "node:assert/strict";
import test from "node:test";
import { buildDiagnosticReport } from "./diagnostic-report.js";

test("diagnostic report includes useful counters without microphone or audio data", () => {
  const report = JSON.parse(buildDiagnosticReport({
    capturedAt: "2026-11-15T09:00:00.000Z",
    status: "live",
    networkOnline: true,
    framesSent: 42,
    framesEchoed: 40,
    framesDropped: 2,
  }));

  assert.deepEqual(report.frameCounts, { sent: 42, echoed: 40, dropped: 2 });
  assert.equal(report.status, "live");
  assert.equal("audio" in report, false);
  assert.equal("microphoneId" in report, false);
  assert.equal("deviceId" in report, false);
});
