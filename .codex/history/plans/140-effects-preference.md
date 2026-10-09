# Plan 140 — Persisted effects preference

Status: Completed
Cycle ID: 140-effects-preference
Roadmap item: R5.4
Created: 2026-10-07

## Objective

Persist and restore the user's Off, Subtle, or Full effects preference, expose it
in the existing Appearance settings destination, and apply it to the production
five-theme resolver/rendering path. The independently observable result is that
the selected setting survives Activity recreation/app relaunch and updates the
resolved appearance without changing weather state or causing a fetch.

## Production boundary

Activity-owned appearance preference state and SharedPreferences adapter;
the Appearance settings controls; and the app-composition bridge into
`ThemeEffectsLevel`/`resolveTheme`. Include only the smallest required pager
transition adaptation for the existing `EffectsLevel` consumer. No weather,
provider, repository, cache, or forecast mapping boundary changes.

## Resolved design decisions from repository inspection

- The persisted/user-facing source of truth is the resolver's existing
  `ThemeEffectsLevel` (`OFF`, `SUBTLE`, `FULL`). Remove or adapt the legacy
  `ui.EffectsLevel` bridge so it cannot truncate Full; avoid maintaining two
  independent effect preferences.
- Preserve the current app default as `SUBTLE` when storage is absent, invalid,
  or unreadable. This matches `OxygenWeatherApp`'s parameter default and
  `ThemePreferences`/`resolveTheme` defaults. Existing stored launch behavior
  has no persisted effects choice and defaults to Subtle.
- Pass the selected level unchanged to `resolveTheme` for every theme. The
  resolver owns capability behavior: `supportsFullMotion` is true for
  Atmospheric and Glass; false for Minimal OLED, Instrument, and Terminal.
  Full remains the selected/resolved effects level for all themes, while its
  motion resolves to the declared preferred motion when Full motion is not
  supported (and remains OFF for themes whose preferred motion is OFF).
  Do not map those themes to Effects Off: Off additionally forces a solid
  backdrop and opaque panels, which is a distinct user choice.
- Retain theme-native Subtle behavior: the resolver produces no motion for
  themes whose declared `preferredMotion` is OFF. Effects preference is not a
  command to animate every theme.
- The prior two-value `EffectsLevel` pager rule is generalized: Off uses an
  immediate page transition; Subtle and Full use the existing animated
  transition. The user choice drives the resolver even when a theme caps its
  motion capability.

## Functional invariants

- Selected forecast identity, canonical values, provenance, freshness,
  chronology, navigation semantics, and alert meaning are identical at every
  effects level.
- Selecting or restoring effects does not request weather or mutate cached
  weather; theme/contrast/effects remain independent preferences.
- Off resolves a solid/opaque backdrop and surfaces with motion OFF, retaining
  all content, controls, and semantics.
- Subtle and Full are passed through to the resolver. Full motion respects each
  theme's declared capability and preferred-motion floor as documented above.
- Missing, invalid, or unreadable stored state safely defaults to Subtle;
  persistence write failure leaves the immediate in-memory choice applied and
  reports failure through the existing preference outcome pattern.
- System reduced-motion policy is not integrated in this cycle (R5.4A).

## Implementation steps

1. Inspect the Activity-owned theme/contrast preference lifecycle, Settings
   Appearance destination, app composition, legacy `EffectsLevel` consumers,
   resolver, and theme catalog. Preserve the established default and
   SharedPreferences/result conventions.
2. Add a platform-neutral effects preference store/selection using
   `ThemeEffectsLevel`, with stable storage IDs and deterministic handling of
   absent, invalid, read-failure, and write-failure cases. Add the Android
   SharedPreferences adapter and wire Activity initialization, state updates,
   and test seams alongside theme/contrast.
3. Replace the two-level app bridge with the selected three-level value passed
   directly to `resolveTheme`; update the pager transition input to preserve
   immediate Off and animated Subtle/Full behavior. Do not introduce
   theme-specific branches in Compose components or map unsupported Full to
   Off.
4. Add visible Off/Subtle/Full Appearance controls with selected-state
   semantics and applicable 48dp targets. Selection applies immediately and
   persists independently of theme/contrast.
5. Add focused preference, resolver/capability, UI interaction, weather-state
   invariance, and no-refetch checks. Install and inspect the actual
   Appearance/Now path for all three choices; retain artifacts and exact run
   metadata in the cycle evidence directory.

## Acceptance criteria

- Store/selection tests cover round-trip persistence for all three values,
  Activity-level restoration, absent/invalid defaults, read/write failures,
  and immediate selection behavior consistent with theme/contrast stores.
- Resolver/application tests cover all 15 theme × effects combinations and
  assert both the selected `effects` value and the exact resolved backdrop,
  opacity, and motion behavior. Full-motion support is asserted against the
  catalog declarations (Atmospheric/Glass supported; Minimal OLED/Instrument/
  Terminal capped); preferred motion OFF remains OFF.
- Appearance UI tests prove all choices are visible/selectable, selected state
  is exposed accessibly, and Off retains required content/controls/semantics.
- State/request-counter checks prove selection and restoration preserve
  forecast identity, values, provenance, navigation, and request count.
- Installed compact Now/Appearance evidence shows each choice selected and
  applied. Evidence explicitly identifies cases and limitations; screenshots
  are not used as a substitute for deterministic state checks.
- No system reduced-motion integration is implemented or claimed.

## Verification and evidence

Focused checks:

- Run the relevant preference-store/selection JVM tests, theme resolver tests,
  and focused Appearance/settings UI tests added or identified during step 1.
- Run the existing focused theme-resolver and preference tests while iterating.

Before cycle close:

- `python scripts/dev.py workflow`
- `python scripts/dev.py test` (the repository JVM suite, including focused
  tests; report pre-existing/unrelated failures distinctly)
- `python scripts/dev.py contract`
- `python scripts/dev.py check` when the Android SDK/dependencies are
  available; report exact unavailable boundary otherwise.
- `git diff --check` and final diff inspection.
- Install the debug app through the real app path. Capture Appearance and Now
  for Off/Subtle/Full with a deterministic forecast fixture at the supported
  compact baseline, 360 × 640 dp, font scale 1.0, LTR, Standard contrast,
  Standard layout. Use a stable theme (Atmospheric) for the three-way visual
  comparison, and include one Full-capability matrix assertion for all five
  themes in automated tests. Record device/API/build identity, viewport and
  density, locale/direction, font scale, theme, contrast, layout, fixture,
  selected effect, exact commands, and request-counter result.

Store commands/results, screenshots, and a concise visual/state review under
`.codex/test-artifacts/140-effects-preference/`; link retained evidence from
the eventual cycle history record. Do not claim large-font, RTL, service-level
TalkBack, or system reduced-motion verification unless separately run.

## Risks and assumptions

- The legacy enum is used by the app entry parameter, pager behavior, launch
  helper, and debug/compatibility tests. Inspection must identify every call
  site and migrate or preserve test-only seams without retaining a second
  persisted source of truth.
- The resolver's Full capability currently governs motion, while backdrop and
  panel resolution still respond to the selected effects level. Tests must
  preserve that declared resolver contract rather than infer that unsupported
  Full equals Off.
- The app currently forces Minimal OLED and Terminal to resolver Off when the
  legacy value is Subtle. Removing that adapter changes their current default
  backdrop/surface treatment to the resolver's declared Subtle treatment;
  this is necessary for a truthful independent Subtle preference and must be
  checked against the theme contract in installed evidence. Their preferred
  motion remains OFF.
- Visual acceptance requires installed evidence; JVM tests and compilation do
  not establish the rendered result.

## Out of scope

- System reduced-motion/disabled-animation policy (R5.4A).
- Ambient-background foundation or theme-specific background treatments
  (R5.4B–R5.4E).
- Simple layout, Settings information architecture, and other destinations.
- Changes to weather fetching, cache, values, provenance, or provider behavior.
- Redesign/reinterpretation of the five themes or meteorological meaning.
- TalkBack service-level audit and broad compact/large-font/RTL matrix work
  tracked by R6; ordinary accessible semantics for the new controls remain in
  scope.
