# Signal Room feature roadmap

Impact and effort are estimates. The repo already has microphone permission handling, device selection, frame counts, input-level hints, failure recovery, local JSON report export, and explicit no-audio-storage behavior. Those are baseline features and are not counted as new work.

| # | Feature | User problem | Competitor gap to test | Impact | Effort |
|---:|---|---|---|---|---|
| 1 | Round-trip latency percentiles | A sent/echoed count does not show delay spikes | RTC vendor dashboards are broader than a one-path debug run | High | Medium |
| 2 | Packet sequence and loss estimate | A connection can echo slowly or drop data without explaining it | One-session troubleshooting is often manual | High | Medium |
| 3 | Synthetic tone mode without microphone | Developers need a repeatable test without granting mic access | Most examples start with a real capture device | High | Medium |
| 4 | Local mock echo server and scenario switcher | Developers need to reproduce timeout, close, and overload cases | Managed services make failure reproduction harder | High | Medium |
| 5 | Test endpoint configuration panel | Switching between local/staging/prod endpoints needs code edits | SDK portals emphasize deployment setup | High | Low |
| 6 | Endpoint readiness and WebSocket preflight | Users need to separate asleep server, bad origin, and bad path | Generic health checks do not prove the audio route | High | Medium |
| 7 | Browser capability checklist | Unsupported APIs cause confusing first-run failures | Device/browser support is distributed across vendor docs | Medium | Medium |
| 8 | Safe report redaction controls | Shared reports can contain network or environment details | Diagnostic exports can expose more than intended | High | Medium |
| 9 | One-click sanitized support bundle | Developers need reproducible issue context without audio | Existing SDK diagnostics are service-specific | Medium | Medium |
| 10 | Multi-endpoint comparison run | Network path differences need side-by-side comparison | Provider dashboards focus on their own network | Medium | High |
| 11 | Browser smoke-test automation | Releases need repeatable mic/permission/server checks | Manual testing varies by machine | Medium | High |
| 12 | Workspace saved test profiles | Teams repeat the same endpoint and expected thresholds | Broad platforms have team dashboards but complex setup | Medium | Medium |
| 13 | Authenticated customer diagnostic sessions | Public demo caps cannot safely serve private endpoints | A simple origin allowlist is not user authentication | High | High |
| 14 | Uptime and synthetic alert integration | Teams need to know when their own endpoint fails | Paid observability tools can be costly for a narrow route | Medium | High |
| 15 | Paid support/pilot workflow | Developers need a bounded path from diagnostic to expert help | Large SDK vendors sell infrastructure, not this independent audit | Medium | Medium |

## Build order and guardrails

Do not implement the remaining items until interviews confirm a recurring paid problem. Keep all new reports local by default. Never record, upload, replay, or retain microphone audio. Do not advertise secure, authenticated, production, or SLA-backed service until those gates are independently implemented and verified.
