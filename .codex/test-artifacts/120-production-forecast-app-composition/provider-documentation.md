# Provider documentation used by cycle 120

Verified 2026-10-04 during plan review against the providers' official documentation.

## Open-Meteo forecast (configured)

- Endpoint: `https://api.open-meteo.com/v1/forecast`
- Forecast API docs: https://open-meteo.com/en/docs
- Terms of Use: https://open-meteo.com/en/terms
- Licence: https://creativecommons.org/licenses/by/4.0/
- Plan review recorded non-commercial-only use for the free service, CC BY 4.0 attribution, and published request limits. The current UI exposes the returned provider name as source context. Recheck terms before release.

Open-Meteo geocoding documentation and term verification remain in
`open-meteo-provider-documentation.md` (cycle 115 evidence).

## MET Norway fallback (deferred)

- Compact endpoint candidate: `https://api.met.no/weatherapi/locationforecast/2.0/compact`
- Official API documentation: https://api.met.no/weatherapi/locationforecast/2.0/documentation
- HOWTO: https://api.met.no/doc/locationforecast/HowTO
- Terms: https://docs.api.met.no/doc/TermsOfService.html
- Cycle 113's adapter evidence records the requirement to identify the client,
  cache responses, honor cache headers, and avoid requests before expiry. The
  current adapter does not preserve those response headers. The normal app
  composition therefore does not instantiate or call MET Norway.

The owner's Open-Meteo-only disposition for this cycle is recorded in
`owner-decision.md`.
