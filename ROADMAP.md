# Student Toolkit release roadmap

## V1.0 — first Galaxy Store release
Offline-first; no account, internet connection, Firebase, or cloud sync required. Core scope: Dashboard, Subjects and tags, Homework, Notes, Timetable, Exam Countdown, Focus Timer (Pomodoro/stopwatch/countdown with verified local sessions), Progress Analytics/Study Score, Settings and Privacy. Complete automated verification, integration testing, and install/use testing on a physical Android device before declaring release-ready. Do not claim Settings/Privacy verified until the GitHub emulator workflow passes.

## V1.1 — optional cloud and sign-in (planned, not implemented)
Estimate: 5–8 additional development days, subject to review. Evaluate Firebase free-tier limits and pricing before implementation. Preserve guest/offline mode and local-first operation. Offer optional Google/email sign-in, user-controlled backup/restore, secure per-user cloud data access, conflict-safe sync, account deletion and data deletion, clear privacy disclosure and recovery testing. Proposed initial per-user text-data quota: 5 MB, subject to validation against project-wide free-tier limits. Do not require account creation to use core functions; do not upload existing local data without explicit user consent. No guarantee of indefinite free cloud capacity.

## V1.5 — later roadmap
Social/referral features only after separate scope and safety/privacy review.

V1.1 does not delay V1.0. V1.0 added development days from this roadmap decision: 0.
