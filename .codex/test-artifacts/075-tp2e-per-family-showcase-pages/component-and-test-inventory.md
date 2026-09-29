# Component and test inventory — cycle 075

## Six independently rendered test-only screens

- Page identity: `ProductionPageHeader`, `ProductionPageSelector`.
- Current conditions: `ProductionCurrentHero`, `ProductionMetricTile`.
- Forecast windows: `ProductionHourlyEntry`, `ProductionDailyRow`, `ProductionWindowControls`, `ProductionHourlyDateSelector`.
- Source and inspection: `ProductionSourceFreshnessPanel`, `ProductionInspectionMetricGroup` for the existing non-empty Conditions and Forecast pattern groups.
- Weather mark: `ProductionWeatherMark` beside the condition text and identity of existing typed daily fixture entry 0.
- Backdrop: `ProductionBackdrop` fills the screen behind a labeled foreground sample/action.

All components remain existing production APIs in `ProductionMonitorComponents.kt`, `ProductionDetailsComponents.kt`, and `ProductionWeatherVisuals.kt`. Only instrumentation code changes.

## Fixtures reused

- Current presentation and representative metric strings from `ProductionSharedComponentsTest`.
- Chronological hourly/daily entries from `ProductionForecastComponentsTest`.
- Source/update text and typed Details metric groups from `ProductionDetailsComponentsTest`.
- Resolver/theme identities from `resolveTheme` and `WeatherThemeId.entries`.
- `DateJumpPresentation` uses the same Monday/Tuesday labels and window indices used by existing forecast instrumentation tests.

The source fixture companion members are exposed only within the `androidTest` source set so this host shares those exact values. No second meteorological fixture set is introduced.

## Reused regression coverage

`ProductionSharedComponentsTest` covers selector semantics and targets, current values, and metric omission. `ProductionForecastComponentsTest` covers forecast facts/order, window/date actions, and target sizes. `ProductionDetailsComponentsTest` covers provenance/update separation, typed group ordering, and missing/omitted values. `ProductionWeatherMarkTest` covers decorative semantics and pointer behavior. `ProductionBackdropAtmosphericGlassTest`, `ProductionBackdropEffectsOffTest`, `ProductionBackdropMinimalInstrumentTest`, and `ProductionBackdropTerminalTest` cover backdrop styles/Effects Off. JVM `ThemeResolverTest` and `ProductionWeatherVisualsTest` cover resolver/visual mapping.
