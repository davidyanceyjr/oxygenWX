# Cycle 075 installed capture manifest

The installed runtime manifest is [manifest.txt](manifest.txt). It records device/build identity, the 360 × 640 px (360 × 640 dp at 160 dpi) display, font and layout settings, application version, each family/theme pairing, dimensions, and SHA-256.

The 30 successful installed PNGs are in `installed/png/`; `installed/raw/oxygen-weather-tp2e-075/` preserves the full MediaStore pull, including the five superseded identity images emitted by the initial semantic-assertion-mismatch attempt. The canonical 30 PNGs were selected by matching all manifest hashes. Their hashes are also listed in [sha256sums.txt](installed/sha256sums.txt).

All canonical files decode as 360 × 640 px. All 30 SHA-256 values match the runtime manifest.
