# History — 223-sq14-static-cross-page-interactions

Status: Completed
Cycle ID: 223-sq14-static-cross-page-interactions
Roadmap item: SQ.14
Closed: 2026-10-22
Plan: .codex/plans/223-sq14-static-cross-page-interactions.md
Evidence: .codex/test-artifacts/223-sq14-static-cross-page-interactions/

## Outcome

Verified the installed Smoked Quartz static cross-page interaction matrix across Now, Hourly, Daily, and Details at None, Subtle, and Full on the Pixel 3 XL. Navigation, window/date controls, menus, routes, and return behavior passed the installed checks; no demonstrated product defect required a source change.

## Verification

Focused ProductionHomeCompositionTest: 8/9 passed; ProductionDailyDetailsCompositionTest: 3/4 passed; ProductionForecastCompositionFlowTest: 2/4 passed, including the retained-cache/no-extra-requests check. The two interaction-test non-passes were classified from missing input/target setup evidence and then manually verified through the installed app. Installed screenshots, fresh UI hierarchies, matrix, device configuration, and per-run logs are under .codex/test-artifacts/223-sq14-static-cross-page-interactions/. Gradle compiled the app and Android test APKs; git diff --check passed.

## Limitations / not verified

No product correction was made. The outside-tap Compose test did not establish injectInputEvent acceptance; installed adb positive control passed. A Daily theme-loop helper looked for Settings Return before scrolling; the installed visible return and Back paths passed. Two forecast/search tests outside SQ.14 failed (extra Open-Meteo semantics node and 10-second search-flow timeout); the in-scope cache/request invariance method passed. Large font, RTL, Simple layout, reduced motion, accessibility service traversal, and full suites remain unverified/out of scope for SQ.14 and are reserved for their planned acceptance or SQ.V.

## Follow-up

Proceed to SQ.15 only after reviewing this matrix; SQ.V remains the separately planned full-suite verification session.
