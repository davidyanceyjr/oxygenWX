# Security Policy

## Reporting a vulnerability

Do not open a public issue for a vulnerability that could expose user data, secrets, credentials, or enable code execution.

If the repository host exposes a private vulnerability-reporting or security-advisory feature, use it. Otherwise, contact a maintainer through a private channel documented by the repository owner before disclosing technical details publicly.

## Scope

The current prototype uses deterministic offline weather data and does not require credentials. Future network providers must keep secrets outside source control and must validate untrusted network data before it reaches rendering or persistence layers.
