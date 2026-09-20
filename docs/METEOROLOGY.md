# Meteorological Data Rules

The app may create new explanatory summaries, but meteorological semantics must remain explicit.

## Data classes

Keep these concepts distinct:

- observation;
- model estimate/current forecast product;
- forecast;
- official alert;
- climatological/historical statistic;
- derived/heuristic signal.

Do not blend them without recording the transformation and provenance.

## Current derived signals

- **Seasonal temperature percentile:** empirical rank of current temperature inside supplied comparable historical samples. Requires at least 20 finite samples.
- **Thermal departure:** current temperature minus historical normal temperature.
- **Pressure departure:** current pressure minus historical normal pressure.
- **Pressure tendency:** forecast pressure change over an actual three-hour timestamp interval.
- **Thermal momentum:** forecast temperature change over an actual three-hour timestamp interval.
- **Persistence index (0–100):** heuristic based on step-to-step change in temperature, pressure, precipitation probability, and wind.
- **Forecast volatility (0–100):** heuristic based on 12-hour spread and condition transitions.
- **Atmosphere texture:** categorical descriptor (`settled`, `turning`, `restless`, `saturated`, `variable`) derived from forecast behavior.
- **Analog years:** historical references supplied by the historical data boundary; this layer only normalizes the list.

These are not NWS/WMO standard products.

## Missing data

Do not use zero as a substitute for an unavailable horizon or historical sample. Derived signals should be nullable/unavailable when their required inputs are missing or insufficient.

## New derived metrics

Document the input fields, units, lookback/lookahead interval, expected range, missing-data behavior, provenance, and limitation. Prefer physically meaningful quantities or transparent composites over opaque scores.

Never infer an official severe-weather alert from forecast conditions. Official alerts require an authoritative alert provider and retained issuer/timing/provenance.
