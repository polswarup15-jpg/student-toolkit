# Student Toolkit V1.0 quality gates

Status: acceptance criteria only; no unverified test is marked passed.

## Non-negotiable release blockers
- Zero known critical crashes, data-loss defects, or broken core navigation paths.
- All advertised V1.0 functions implemented and verified.
- Core workflows function offline without login, Firebase or cloud dependency.
- Existing subjects, homework, notes, timetable, exams and study sessions survive supported upgrades; verify schema migration with populated databases.
- No unnecessary permissions; overlay denial must leave core timer usable.
- Automated build, APK validation and all instrumentation tests pass on the release candidate.
- Physical Android installation, upgrade, navigation, persistence and timer tests pass.
- Owner approves signing, store disclosures and release submission.

## Proposed measured performance targets
- Cold launch under 3 seconds on an agreed budget test device (measure multiple launches).
- Typical page transition under 300 ms; target smooth 60 FPS animations where device supports it.
- Measure and record results; do not claim a pass from source inspection alone.

## Per-feature completion definition
A feature is 100% only when implementation, UI, animation (where relevant), functional tests, regression checks, persistence, device testing and GitHub verification are complete. Report pending items explicitly.

## Progress reporting
For each feature record: implemented, automated-test result, device-test result, regressions, blockers, evidence (commit/run), next action and estimated added days for proposed scope changes. Percentages must distinguish implementation from verified release readiness.

## Scope discipline
V1.0 first. No V1.1/V2.0 functionality, new permissions, cloud provider or paid dependency without separate owner approval. No automatic merge or release submission. Benchmark targets are goals, not assertions of current performance.
