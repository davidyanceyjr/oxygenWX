# Open-Meteo geocoding provider documentation check

Verified: 2026-10-04

This is a documentation check for the manual location search candidate. The
provider documentation and free service terms were rechecked on the date above.

## Endpoint and query contract

The documented search endpoint is
[`https://geocoding-api.open-meteo.com/v1/search`](https://open-meteo.com/en/docs/geocoding-api).
It requires `name` (a location name or postal code). Optional parameters include
`count` (default 10, maximum 100), `format` (JSON by default, protobuf also
supported), `language` (default `en`), and `countryCode` (ISO-3166-1 alpha-2).
The `apikey` parameter is documented as needed only for commercial use of
reserved customer resources; it is not required for the free non-commercial
endpoint. Empty and single-character searches return no matches. Two-character
names are exact matches; longer names use normalized prefix matching.

The response includes location name, WGS84 latitude/longitude, timezone,
country and administrative-area fields when available. The provider describes
location data as based on GeoNames; credit GeoNames when presenting results.

## Terms, license, and request ceilings

Open-Meteo's [Terms of Use](https://open-meteo.com/en/terms) specify that the
free API is only for non-commercial purposes and set ceilings of fewer than
10,000 calls/day, 5,000 calls/hour, and 600 calls/minute. Its [pricing page](https://open-meteo.com/en/pricing)
also lists 300,000 free calls/month. The current [Geocoding API
page](https://open-meteo.com/en/docs/geocoding-api) lists non-commercial,
commercial, and self-hosted usage options.

The terms and pricing page state that API data is licensed under [CC BY
4.0](https://creativecommons.org/licenses/by/4.0/). Give appropriate credit
and indicate modifications as required by that license. Open-Meteo notes that
commercial use requires a paid plan; the free endpoint is not suitable for a
commercial app under its current terms.

## Direct references

- [Geocoding API documentation](https://open-meteo.com/en/docs/geocoding-api)
- [Open-Meteo Terms of Use](https://open-meteo.com/en/terms)
- [Open-Meteo pricing and API limits](https://open-meteo.com/en/pricing)
- [Creative Commons Attribution 4.0 International](https://creativecommons.org/licenses/by/4.0/)
