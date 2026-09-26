# Shared content and state rules

**Authority:** [specification](../../SPECIFICATION.md), [adopted UI specification](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md), [current presentation contracts](../../../app/src/main/java/com/oxygen/weather/presentation/HomePresentation.kt) and [load states](../../../app/src/main/java/com/oxygen/weather/presentation/HomeLoadState.kt). These are **accepted by authority** rules. Page placement remains TP.1B/C work. The current normal app does not yet bind every typed state to rendering; this is a design grammar, not a claim of implemented behavior.

## Supported slots

| Surface | Supplied presentation facts | Absent behavior |
| --- | --- | --- |
| Shared shell | selected page name; current location; `sourceLine`, `updatedLine`; outer `StatusPresentation` when present | No synthetic timestamp, selected location, or source in `FailedWithoutData`. |
| Now | `CurrentPresentation` condition, temperature, apparent, humidity, dew point, precipitation headline/support, wind headline/support; field availability and spoken summary | A missing field reads “Unavailable” only where its labeled slot is shown; optional supporting slot may be omitted. No invented advisory/UV/AQI. |
| Hourly | supplied `HourlyWindowPresentation.rangeLabel`; at most six actual `entries`, each time, condition, temperature, optional precipitation and spoken summary; supplied `hourlyDateJumps` | No blank/padded entry. No date control for an unrepresented date. Do not derive a time-series chart from display strings. |
| Daily | supplied `DailyWindowPresentation.rangeLabel`; at most five actual `entries`, each day, condition, low, high, precipitation and spoken summary | No blank/padded day or weather inferred from a decorative icon. |
| Details | `detailGroups` in their supplied order, each titled with label/value/supporting text; `sourceLine`, `updatedLine` | Omit an absent group/metric. Keep Conditions, Forecast pattern, Historical context distinct. No fabricated historical reference. |

The fixture contains a full 72-hour/10-day horizon, but it does not define other production-state examples. Details' derived values are experimental contextual output; no reference art may make them look like official observations or alerts. The presentation layer currently formats Metric/US/UK boundary functions separately; the Home mapper still emits its current fixed units. Designs label supplied strings and leave unit-preset application to its roadmap slice. Source: `HomePresentation.kt`, `WeatherUnits.kt`, [roadmap R1.3A](../../ROADMAP.md).

## State matrix

| Input state | Visible treatment | Semantics and omission |
| --- | --- | --- |
| `HomeLoadState.Loading` | Named page shell and visible supplied loading status. Weather slots wait for data. | Announce exactly `status.accessibilitySummary`; do not show fixture values as loading placeholders. |
| `LiveData` with `Complete` | Supplied weather and source/update plus live/freshness status. | Page identity, status, and each visible fact have matching meaningful semantics. |
| `LiveData` or `CachedData` with `Partial` | Supplied entries only; identify a short hourly and/or daily horizon in visible text. | Do not imply a 72-hour/10-day horizon. If the typed horizon distinguishes only complete/partial, do not invent a missing count or end time. |
| `CachedData` | Supplied weather retained; visible “saved” origin and supplied current/stale/unknown freshness status. | Do not label saved data as fresh live data. Preserve source/update text from nested content when available. |
| `RefreshFailedWithRetainedData` | Retained weather plus exact supplied failure/origin/freshness status. | Status text and accessibility summary match. No invented successful refresh time. |
| `FailedWithoutData` | Named shell and supplied failure status. | No location, weather, source, or update claim: this type supplies status only. |
| Nested `Unavailable` | “Weather data unavailable” and supplied location/source/update fields. | Do not treat provenance or timestamps alone as weather; no invented measurements. |
| Missing field inside data | Show the typed `Unavailable` text in a required labeled field; omit optional decoration or optional supporting slot. | Never substitute 0. A null condition has no weather mark. |
| Official alert | Only an authoritative alert record may produce an “official alert” state, with issuer, event, time, and source as supplied. | Current Home presentation has no alert slot. TP.1B/C reserve no fake alert card; later R4 integration defines the slot and unsupported/no-alert/failure distinctions. |

A missing optional precipitation probability can remove an optional hourly subline. A daily row still exposes the supplied precipitation meaning, including “Precipitation unavailable”. Current mapper suppresses hourly precipitation text for a reported zero while `fieldAvailability` retains the reported zero; page designs must not label that as an unknown value. These choices are grounded in `HourlyEntryPresentation`, `DailyEntryPresentation`, and `PresentationField`.

## Navigation and accessibility

The outer Home pager owns horizontal swipes. Hourly/Daily window changes use visible Earlier/Later controls; Hourly date jumps choose the first represented window for the date. Control targets meet 48 dp guidance where applicable, keep discernible names, and expose selected/disabled state in text or semantics as well as visual treatment. Page identity stays visibly named and receives page semantics. Android Back steps toward Now. Chronology in the supplied lists remains earliest-to-latest in RTL; mirrored alignment must not reverse data or reinterpret Earlier/Later. Source: [specification §4](../../SPECIFICATION.md#4-home-presentation-contract), [adopted UI specification](../../OXYGEN_UI_SPECIFICATION_ADOPTED.md). **Status: accepted by authority.**

Decorative mark, grid, glow, and backdrop have no independent spoken weather meaning and do not intercept taps. Visible labels carry all important facts; spoken summaries must not claim facts absent from the presentation model. Non-color cues include status wording, named page controls, selected state, and textual unavailable values. At large font or compact height, vertical scroll may reveal secondary content; primary facts and controls must not clip. Effects Off preserves all text, controls, alert meaning, and provenance on opaque static surfaces. Source: [specification §§3.5–3.6](../../SPECIFICATION.md), [theme contract](../THEME_DESIGN_CONTRACT.md). **Status: accepted by authority.**

## Exclusions for TP.1A–C

No new provider/cache/alert path, settings control, manual location search, radar/map, UV, AQI, activity advice, gauge, invented chart series, geocoordinate line, historical analog placeholder, or cross-page overview card is designed as a live fact. No screen composition from the retired Atmosphere Deck is reused. Page-specific slot position, truncation behavior, measurements, and complete examples belong to TP.1B/C and TP.1D.
