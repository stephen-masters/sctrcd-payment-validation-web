# Security policy

## Reporting a vulnerability

Please report suspected vulnerabilities privately through GitHub's
[private vulnerability reporting](https://github.com/stephen-masters/sctrcd-payment-validation-web/security/advisories/new)
(Security tab → "Report a vulnerability"). Do not open a public issue for a
security problem.

## Supported versions

This is a demonstration project with no releases. Only `main` is maintained.

## What is done to keep it safe

- Dependency versions come from the Spring Boot parent and the Drools BOM,
  kept current by Dependabot, which also keeps the Maven wrapper and the
  workflow actions current.
- Trivy scans the dependencies for HIGH and CRITICAL vulnerabilities on
  every push and pull request, and weekly on a schedule, and CI publishes a
  CycloneDX SBOM.
- The pages load no third-party scripts: the one script is plain
  JavaScript served by the application itself.
- The application holds no data and no credentials. It listens on port 9090
  and is meant to be run locally, not exposed to the internet.
