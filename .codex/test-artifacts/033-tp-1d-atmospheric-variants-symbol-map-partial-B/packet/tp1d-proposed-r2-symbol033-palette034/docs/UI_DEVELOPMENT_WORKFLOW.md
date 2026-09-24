# UI Development Workflow

Visual Compose work is accepted from the installed/rendered application path, not from source inspection or previews alone.

## Fast visual loop

Use this while converging on one bounded visual concern:

```text
define the visual objective
-> capture a baseline
-> inspect the owning Compose/presentation code
-> make one bounded change
-> compile/build as needed
-> install and launch
-> capture the rendered result
-> compare with the objective
-> revise
```

Do not run every repository gate after each spacing or typography edit. Compilation only proves the code builds; it does not prove the visual result is correct.

## Verification loop

Once the visual result has converged:

```text
run focused tests/checks
-> install the current build
-> render the authoritative state
-> capture final screenshot evidence
-> verify layout/accessibility constraints
-> run broader checks appropriate to the slice
-> retain evidence
-> commit
```

## Slice acceptance template

Every visual task should identify three categories.

### Functional invariants

Examples: weather values, condition identity, provenance, update time, navigation behavior, refresh behavior, provider/cache behavior, and accessibility meaning.

### Visual objectives

Examples: establish hierarchy, make current temperature dominant, reduce tertiary information weight, improve grouping, or make a forecast comparison easier to scan.

### Layout/environment constraints

Examples: compact phone width, font scale 1.3/2.0, long location text, RTL, Effects Off, no overlap/clipping, and no unintended nested horizontal scrolling.

## Evidence expectations

For UI changes, retain evidence from the installed app whenever an Android environment is available. A useful candidate matrix includes:

- 360x640dp at font scale 1.0;
- 360x640dp at font scale 1.3;
- a large-font case when the slice can be stressed by text growth;
- RTL when ordering/navigation is affected;
- Effects Off once the preference is implemented.

Screenshots are presentation evidence, not functional proof. Pair them with focused automated checks for the presentation/domain boundary that changed.

## Cross-platform commands

Use:

```sh
python scripts/dev.py build
python scripts/dev.py test
python scripts/dev.py lint
python scripts/dev.py contract
python scripts/dev.py check
python scripts/dev.py run
python scripts/dev.py screenshot --output .codex/test-artifacts/<cycle-id>/home.png
```

The Python command chooses the repository Gradle launcher appropriate for the host OS, selects an available JDK 17+ when the shell default is older, and uses adb from `PATH`, `ANDROID_SDK_ROOT`, or `ANDROID_HOME` for run/screenshot operations. The repository's headless emulator helper defaults to `.android/avd/oxygen_starter` and `.android-sdk` within this checkout.
