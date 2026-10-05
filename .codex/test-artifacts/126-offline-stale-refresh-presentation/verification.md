# Cycle 126 verification — offline and stale refresh presentation

## Implementation

- `CachedForecastFreshness` classifies only `cachedAt` against one injected UTC clock reading: age `< 2 hours` is CURRENT, `>= 2 hours` is STALE, and a future cache timestamp or clock failure is UNKNOWN.
- The typed classification reaches the selected-location cache presentation and Details forecast context. Status text names the cache age class and says refresh continues. Cache time and provider retrieval time remain separate. Cached forecast presentation continues to expose current conditions as unavailable.
- `AndroidForecastCacheStore` accepts a clock so the real Activity restoration fixture can seed deterministic cache timestamps. The default remains `Clock.systemUTC()`.

## Automated verification

- `python scripts/dev.py workflow` — passed; cycle 126 ACTIVE before close.
- `python scripts/dev.py test` — passed (full debug unit test suite).
- `python scripts/dev.py check` — passed (workflow, source contract, unit tests, debug lint, and debug assemble).
- `:app:connectedDebugAndroidTest` focused Activity tests — both passed individually:
  - `CachedForecastRestorationTest#recentCacheJustUnderBoundaryRendersForecastOnlyBeforeLiveReplacement`
  - `CachedForecastRestorationTest#staleCacheAtExactTwoHourBoundaryIsReportedOnNowAndDetails`
- `CachedForecastFreshnessTest` covers one nanosecond below the threshold, exact equality, future cache time, and clock failure. `ForecastContextMapperTest` verifies stale freshness propagation while preserving cache/provider timestamps.
- `git diff --check` — passed.

The repository-wide connected instrumentation suite was started once, then stopped after 13 of 42 tests because its runtime was several minutes and this cycle only requires the focused restoration path. A first attempt to run the focused class together encountered fixture-order/timing failures; each corrected case then passed when run by method filter. The recent case returns to Now before verifying live success.

## Installed evidence

- Device: local `oxygen_starter` Android Emulator, API 37 x86_64; debug build.
- Viewport: 945 × 1680 px at 420 dpi = 360 × 640 dp. Font scale 1.0 for recent captures; 1.3 for stale captures.
- Effects: Off using the existing instrumentation override. Stale captures use RTL via the existing Activity test hook.
- Selected location: Chicago (`cycle-125-chicago`). Normalized forecast cache seeded through `AndroidForecastCacheStore`; forecast provider retrieval instant is `2026-10-04T14:05:00Z`.
- Fixed cache clock: `2026-10-05T15:00:00Z`. Recent case uses a cache instant one nanosecond younger than the two-hour boundary. Stale case uses exactly two hours.
- Transport: the real selected-location Activity path enters a test transport that waits on a latch. The pending cached state is captured before the latch is released. The recent case then releases it to return a canned live success (`7 °C`). The transport call count is unchanged by inspecting/navigating the cached state.
- The final exact-boundary stale instrumentation run also disabled emulator Wi-Fi and mobile data (`svc wifi disable`, `svc data disable`; `dumpsys wifi` reported `Wi-Fi is disabled`). The transport override makes no external weather request and remains latch-controlled while cached content is shown.

Screenshots, captured from the installed Activity composition:

- `recent-now.png` and `recent-details.png` — font scale 1.0, LTR.
- `stale-now-font1.3-rtl.png` and `stale-details-font1.3-rtl.png` — font scale 1.3, RTL.

The screenshots were pulled from the preceding passing run with the same cache, clock, transport, viewport, font, RTL, and Effects Off settings. A final rerun with Wi-Fi/data disabled passed the stale assertions; that rerun's screenshot copies were not pulled again.

Visual inspection found the cache origin, cached time, provider retrieval time, freshness, and refresh-pending status readable in Details at both standard and large font. The stale RTL Details capture retains all facts without clipping. The Now page remains scrollable; the forecast context is below the primary current-condition area.

## Unverified boundaries

- The full 42-case connected instrumentation suite did not complete.
- No live external provider request was performed; the transport was deliberately replaced with a latch-controlled test response.
- TalkBack service-level speech traversal was not run.
