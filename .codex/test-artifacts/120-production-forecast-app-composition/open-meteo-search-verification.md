# Cycle 115 verification

Date: 2026-10-04

## Commands and results

- `python scripts/dev.py workflow` — passed before activation with state PLANNED; passed again after activation with state ACTIVE.
- `python scripts/dev.py contract` — passed.
- `python scripts/dev.py test --tests '*LocationSearch*'` — the developer entry point does not accept Gradle test filter arguments (argparse rejected `--tests`).
- `python scripts/dev.py test` — passed; all unit tests completed (145 at that run). This caught and drove correction of non-2xx result classification.
- `ANDROID_HOME=<repo>/.android-sdk JAVA_HOME=/usr/lib/jvm/java-27-openjdk PATH=/usr/lib/jvm/java-27-openjdk/bin:$PATH ./gradlew :app:testDebugUnitTest --tests '*LocationSearch*'` — passed after focused fixture updates.
- `python scripts/dev.py check` — passed after replacing API 33 `ByteArrayOutputStream.toString(Charset)` with the compatible `String(bytes, UTF_8)` constructor. The check ran workflow, source contract, unit tests, debug build, and lint successfully.
- `git diff --check` — passed after the final focused test run.

## Scope and evidence

All search tests use fixture strings/resources and an injected transport; no network lookup was performed. The tests cover encoded query/locale, ordered candidate mapping, invalid-record filtering, empty results, all-invalid records, HTTP/provider/transport/malformed/oversize failures, invalid contracts, and geocoding transport isolation.

Provider documentation evidence and direct links: `provider-documentation.md`. The adapter caps decoded response input at 65,536 bytes, enough for more than 6 KiB per result at the provider's documented default of ten results, including all mapped candidate fields.

Installed UI evidence is not applicable: this cycle adds no UI. No manual location-to-weather handoff, persistence, or forecast path exists in this cycle.
