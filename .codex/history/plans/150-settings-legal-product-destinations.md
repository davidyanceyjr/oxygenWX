# Plan 150 — Settings legal and product-information destinations

Status: Completed
Cycle ID: 150-settings-legal-product-destinations
Roadmap item: R5.6B
Created: 2026-10-08

## Objective

Add Privacy, Open Source Licenses, and About destinations to the existing
Settings flow. The installed app must let a user open each destination and
return to the same Settings/Home state. Each destination may show only reviewed
project/source text and verified links already supported by the repository;
content that has not been approved or verified remains explicitly unavailable.
This cycle creates navigable information surfaces, not new policy, legal
decisions, or a complete third-party notice catalog.

The independently observable outcome is an installed Home → Settings →
Privacy, Open Source Licenses, or About flow that returns to its originating
Home page, shows only the content inventory below, and performs no
weather/provider work.

## Reviewed content inventory and display boundary

Use this repository inventory as the implementation baseline; do not treat
candidate material as approval for broader legal or source claims:

- **Privacy:** show an explicit “Privacy policy unavailable” state. The
  product specification's privacy principles are product requirements, not
  approved public policy text. The README and current code are not a substitute
  for the R7.1 privacy/permission/exported-component audit. Add no privacy
  claims or external policy link.
- **Open Source Licenses:** show only the two license texts already bundled
  with app font assets: `app/src/main/assets/licenses/fira_sans_OFL.txt`
  alongside the Fira Sans font resources, and
  `app/src/main/assets/licenses/noto_fonts_LICENSE.txt` alongside the Noto
  Sans font resources. Present their exact supplied text under the respective
  font names. Label the section as limited to bundled font license files and
  explicitly state that it is not a complete dependency notice catalog. The
  UI must not display a final license for the Oxygen Weather project; that
  decision belongs to R7.2.
- **About:** show only the manifest app label “Oxygen Weather” and the build's
  `versionName` (currently `1.0.0-alpha01`, read at runtime). Do not imply
  release status, authorship, support contact, or a final project license.
- **Source links:** add no remote source or policy link in this slice. The
  README's statement that this prototype retains Apache-2.0 is explicitly
  provisional for replacement use; the upstream GPL reference concerns the
  design/specification source and does not establish this app's license. The
  repository URL embedded in a network User-Agent is not reviewed public link
  copy. Keep these out of the destinations until appropriate source/legal
  review verifies the target and wording.

If inspection finds these exact asset-to-font pairings or app metadata no
longer match the build, stop displaying the affected item and render its
unavailable state; do not infer a replacement. No additional owner decision
is required for this conservative inventory. Any proposal to show policy
text, a project license, provider attribution, dependency-wide notices, or a
remote link requires owner review and is outside this plan's production scope.

### Verified repository inventory and runtime contract

Repository inspection establishes the current pairing and metadata:

- `app/src/main/res/font/fira_sans_{regular,medium,semibold}.ttf` is paired
  with `app/src/main/assets/licenses/fira_sans_OFL.txt` and displayed under
  **Fira Sans**.
- `app/src/main/res/font/noto_sans_*.ttf` is paired with
  `app/src/main/assets/licenses/noto_fonts_LICENSE.txt` and displayed under
  **Noto Sans**.
- `app/src/main/AndroidManifest.xml` supplies the app label `Oxygen Weather`;
  `app/build.gradle.kts` currently supplies `versionName =
  "1.0.0-alpha01"`. The About surface reads both values from installed
  package metadata. The version string is not duplicated in UI code or test
  expectations as a production fallback.

At implementation time, verify those resource files and runtime metadata
against the built app. If a required asset cannot be opened/read, is empty, or
its paired font resource is absent, show that font's license content as
unavailable and retain the explicit limited-scope notice. Do not show a
partial license as successfully opened. If package metadata does not provide
a nonblank label or version name, show that individual value as unavailable;
do not substitute the current manifest/build literal. Keep the license body
text exactly as bundled (including its original wording and line sequence);
layout wrapping and scrolling are presentation only. A focused test may use a
destination-local asset reader override to exercise read failure through the
same rendering path; production reads remain from the two named app assets.

### Route and state contract

The existing `OxygenWeatherApp` owns Settings routing with saveable
`settingsRoute`, `settingsOpeningPage`, Home pager state, and hourly/daily
window indices. Preserve this single owner and extend its route set as follows:

| Current state | Action | Next state | Required preserved state |
| --- | --- | --- | --- |
| Home | Open Settings | Settings | Capture current Home page once; retain pager and hourly/daily window indices |
| Settings | Open Privacy, Open Source Licenses, or About | Chosen destination | Retain captured Home page and all existing Settings/Home state |
| Any new destination | Visible return or Android Back | Settings | Keep the captured Home page and Settings state |
| Settings | Return to Home or Android Back | Home | Restore the captured Home page and its existing forecast/window state |

Returning from a child destination must not invoke the Settings-to-Home exit
action. Destination opening, reading local assets, returning to Settings, and
leaving Settings are passive: they must not call weather, cache, or alert
operations. Follow the existing installed-flow pattern in
`SettingsDataLocationDestinationsFlowTest` for operation counters and route
assertions. Test the origin from a non-Now page with a non-default hourly or
daily window so state preservation is observable, not inferred from route
labels alone.

## Production boundary

- `app/src/main/java/com/oxygen/weather/ui/OxygenWeatherApp.kt`: add the three
  destinations to the existing Settings route owner and render their bounded
  content using the current theme, contrast, typography, and layout contracts.
- Add a small typed UI model/resource only if needed to keep destination
  content separate from route rendering. Content must trace to reviewed text
  and verified local/remote targets; absent material uses an explicit
  unavailable state.
- Read the two bundled font license assets without editing their legal text.
  About's version comes from installed package metadata rather than a
  duplicated constant.
- `app/src/androidTest/java/com/oxygen/weather/ui/`: focused installed-flow
  coverage for opening each destination, content/link states, and returning to
  the prior Settings/Home state.
- Cycle evidence: `.codex/test-artifacts/150-settings-legal-product-destinations/`.

Do not change provider, repository, persistence, permissions, manifest, or
licensing declarations as part of this UI slice.

## Context budget

Expected execution context remains below the roadmap's 65% split threshold:
one existing Settings route owner, two local text assets, one package metadata
field, and one focused installed-flow class. Keep the test seam for asset-read
failure inside this destination boundary; split only if implementation
uncovers a broader route or content system dependency.

## Functional invariants

- Preserve the Settings route/back behavior and the Home page, forecast,
  location, and Hourly/Daily window state that opened Settings.
- Keep global Home navigation Now → Hourly → Daily → Details. Settings remains
  outside the Home pager and owns no horizontal swipe navigation.
- Keep all current weather values, source/provenance, freshness, alert meaning,
  and request/cache behavior unchanged; opening or leaving an information page
  performs no weather/provider operation.
- Use existing appearance and unit preference behavior. Theme, contrast,
  layout, and effects do not alter content meaning or navigation semantics;
  Effects Off remains opaque, static, and complete.
- Give routes, links, unavailable content, and Back/return actions visible
  labels and meaningful semantics. Applicable interactive targets remain at
  least 48dp; long text and RTL remain usable.
- Do not infer or claim a privacy policy, license decision, provider attribution,
  dependency-license completeness, or other legal status from implementation
  details or unreviewed repository text.

## Implementation steps

1. Inspect the existing Settings routing/content pattern from cycles 148–149.
   Confirm the named font files and license assets are present and paired,
   confirm the app label/version from manifest/build metadata, and record the
   inventory disposition above in cycle evidence. Keep Privacy unavailable;
   exclude the provisional README project-license statement, upstream GPL
   reference, and User-Agent source URL. Do not turn this step into the R7.1 or
   R7.2 audit.
2. Add Privacy, Open Source Licenses, and About entries and destinations to the
   current Settings navigation. Preserve the opening Settings/Home state and
   existing Back behavior.
3. Render About from package metadata and the exact bundled license asset text
   with Fira Sans/Noto Sans labels, a partial-scope notice, and scrollable
   long-text behavior. Render Privacy as unavailable. Add no remote links; a
   local license entry must open only its matching asset and report read
   failure as unavailable.
4. Add focused route/content/semantics tests for displayed metadata, both
   exact license texts and limited-scope notice, unavailable Privacy state,
   asset-read failure, and return navigation. Assert passive navigation does
   not change forecast/repository/cache/alert operation counters.
5. Install and inspect the actual app flow. Capture Settings and all three
   destinations at 360 × 640 dp / font scale 1.0; inspect affected pages at
   font scale 1.3, RTL, and Effects Off. Verify return to the opening Settings
   and Home state, and record exact content/link availability.
6. Run focused and broader checks, inspect captures and final diff, and record
   exact outcomes and unverified boundaries in cycle evidence.

## Acceptance criteria

- Settings exposes Privacy, Open Source Licenses, and About alongside existing
  destinations; each opens and returns through the established Settings flow.
- Privacy visibly identifies its public policy as unavailable. About shows
  only the app label and installed version metadata. Open Source Licenses shows
  the exact bundled Fira Sans and Noto Sans license texts, labels the listing
  as limited to those font assets, and states that it is not a complete
  dependency notice catalog. The Oxygen Weather project license remains
  unresolved and is not presented as settled.
- No unverified remote target is presented as a link. Local license content
  opens from the matching bundled asset; a read failure is represented as
  unavailable, not as a successful open.
- Opening, inspecting, and leaving destinations preserves the prior Settings
  and Home state and does not change forecast/repository/cache/alert operation
  counters.
- Focused installed evidence covers Settings and the three destinations at
  360 × 640 dp / font scale 1.0. Font scale 1.3, RTL, and Effects Off are
  inspected and recorded; no critical content or control becomes clipped,
  obscured, or unreachable in inspected states.
- Focused semantics/interaction checks cover destination names, content or
  unavailable state, link labels/outcomes, and Back/return navigation.
- No product policy, project license decision, source attribution catalog,
  dependency audit, provider behavior, permission, or manifest behavior is
  introduced or implied.

## Verification and evidence

Evidence directory: `.codex/test-artifacts/150-settings-legal-product-destinations/`.

- `verification.md`: changed boundary; exact commands/results; emulator/API,
  viewport, font scale, layout direction, theme/effects; opening and return
  route state; content reviewed/rendered; local asset outcomes;
  operation-counter comparison; limitations.
- Focused Android instrumentation through `python scripts/dev.py android-test`
  exercises `SettingsLegalProductDestinationsFlowTest` through Settings → each
  destination → Settings → originating Home page, displayed metadata, exact
  local license content, unavailable state, and asset-read failure using the
  real app path. Run the focused class with:

  ```sh
  ./gradlew :app:connectedDebugAndroidTest -Pandroid.testInstrumentationRunnerArguments.class=com.oxygen.weather.ui.SettingsLegalProductDestinationsFlowTest
  ```
- Focused semantics assertions cover route names, app label/version, limited
  license scope, unavailable Privacy/project-license states, local license
  content actions, and Back/return navigation.
- Installed captures: Settings, Privacy, Open Source Licenses, and About at
  360 × 640 dp / font scale 1.0, plus recorded inspection at font scale 1.3,
  RTL, and Effects Off.
- Broader checks: `python scripts/dev.py workflow`, `contract`, and `check` when
  the Android SDK/dependencies are available; `git diff --check`; final diff
  and status review. Record exact failures and environment limits.
- Preserve actual captures and command results under the evidence directory;
  compilation or previews alone do not satisfy the installed destination
  objective.

## Risks and assumptions

- R5.6 and R5.6A are complete in cycles 148 and 149; their `PLANNED` labels in
  `docs/ROADMAP.md` are stale. R5.6B is the next eligible dependent item.
- The repository contains license-related material, but that does not establish
  the replacement project's final license or a complete third-party notice
  set. The two bundled font license files are the only legal texts this slice
  displays; cycle 150 must not make the project's license decision or claim
  complete third-party coverage. The later R7.2 audit owns those decisions.
- The repository's product specification includes privacy requirements, but
  those requirements are not automatically an approved public privacy policy.
  The Privacy destination must keep policy text unavailable unless reviewed
  public wording is already supplied.
- No remote links are in the approved display inventory, avoiding unverified
  targets or claims about their current contents.
- The installed deterministic fixture may omit real provider attribution or
  legal content. Tests must retain honest unavailable states rather than add
  sample legal claims to make the pages look populated.

## Out of scope

- R7.1 privacy/manifest/permission/exported-component audit.
- R7.2 current provider terms, attribution research, dependency notice audit,
  or final replacement-repository license decision.
- Authoring, adopting, or legally reviewing a privacy policy or license.
- Adding provider/source terms or dependency notices that are not already
  reviewed and verified.
- New provider, repository, persistence, permission, manifest, or weather/data
  behavior; Settings architecture redesign; unrelated accessibility closure,
  broad visual matrix, or TalkBack service audit.
