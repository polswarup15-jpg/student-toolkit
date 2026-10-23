# Phase 3 — Floating Focus Timer

Status: implementation pending. This document defines the agreed scope; it is not a claim that the feature is built or tested.

## Scope
- Android floating overlay visible above other apps, with explicit user-granted draw-over-apps permission and an accessible permission explanation.
- Draggable compact widget with elapsed focused time, Play, Pause/Resume, and Close (hide without discarding progress).
- Foreground service with visible ongoing notification and Stop action; comply with Android version-specific foreground service requirements and restrictions.
- Monotonic elapsed-time accounting; paused intervals never count. Avoid double-counting across activity/service lifecycle changes. Persist session state safely.
- Keep existing minimum 10 minutes of verified active focus time before saving; distinguish overlay visibility from verified activity, and avoid treating background elapsed time alone as proof of study.
- Theme selection under Settings > Appearance, persisted locally. Overlay and in-app UI use the same palette; support dark, blue, green, purple, and legible contrast.
- No internet or cloud dependency. Overlay is optional; the normal in-app timer remains usable when permission is denied.

## Implementation order
1. Inspect current focus modes, settings, manifest, Gradle and tests; establish regression baseline.
2. Extract timer state to service-safe model, preserve Pomodoro/stopwatch/countdown behavior and verification rules.
3. Implement foreground service, notification, permissions and lifecycle handling.
4. Add draggable overlay with controls and accessibility labels.
5. Add locally persisted theme setting and dynamic palette updates.
6. Add unit/instrumentation tests and emulator checks; then physical-device review.

## Acceptance checklist
- Permission granted/denied/revoked paths work without crash.
- Timer controls synchronize between overlay, notification and Focus page.
- Paused time excluded, session remains correct on hide/reopen, app navigation, screen lock and process lifecycle.
- Stop and save follow the existing verified-session policy; no unverified session is credited.
- All four themes update the overlay, with readable controls.
- No unintended data loss in subjects, homework, notes or other V1.0 modules.
- Build and automated test results recorded; physical-device PASS requires user confirmation.

Estimated additional effort: 6–9 development days including theme support and testing; estimate, not elapsed progress.
