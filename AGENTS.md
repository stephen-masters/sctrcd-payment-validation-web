# AGENTS.md

Guidance for anyone working in this repository with a coding agent (Claude
Code, Codex, Cursor, Gemini CLI or whatever else reads this file) and a fair
summary of the house rules for contributors who use none.

## What this repository is

**sctrcd-payment-validation-web** is a small demonstration of Drools rules
behind Spring MVC web services. It is a teaching example, not a library or a
product: nothing is released or deployed.

The point is this. Each rule-based validator builds a Drools knowledge base
from DRL files on the classpath once, then runs every request through a fresh
stateless session with the list of known countries as a global. The rules
insert `PaymentValidationAnnotation` facts; the accumulation rules gather
them onto the request, and the controller returns them as JSON. The
`com.sctrcd.drools.util` package holds the helpers the validators and tests
use: building a knowledge base, and listeners that record which rules fired
and which facts changed.

## Hard rules

### The rules are the demo

Change a `.drl` file only when the behaviour should change, and then change
the tests that pin that behaviour in the same commit. The Drools upgrade
needed no DRL changes; keep it that way unless a newer Drools requires one.

### Keep the endpoints and the port

The README's curl examples (`http://localhost:9090/iban/validate/...`) are
the documented interface, and `PaymentValidationEndpointsTest` runs them.
Keep the paths, the JSON property names and `server.port=9090`.

### No third-party scripts on the pages

The pages are Thymeleaf templates with plain JavaScript served from
`src/main/resources/static`. Do not add a CDN script or a front-end
framework.

### Do not weaken assertions

The rule tests assert which rules fire and which annotations come back. Do
not loosen them to make a change pass.

### Stay on current LTS versions

The project tracks the latest LTS JDK (`java.version` in `pom.xml`, and
`java-version` in the workflows; change them together), the latest Spring
Boot GA release and the latest Apache KIE Drools release (`drools.version`).
Versions come from the Spring Boot parent and the imported Drools BOM: do
not pin a version that either already manages.

### Each rule file gathers its own annotations

`IbanRules.drl` and `BicRules.drl` each end with a low-salience rule that
copies their annotations (matched on `attribute`) onto their own request
fact, without `update()`, which would re-trigger the rules in a loop. The
rule-based IBAN and BIC validators read their results from those requests,
so an annotation inserted without its `PaymentAttribute` is silently lost
outside `/payment/validate`. The validator tests check that every rejection
reaches the result.

## Layout

| Path | What |
|---|---|
| `src/main/java/.../payments/PaymentValidationApplication.java` | `@SpringBootApplication`, scanning `com.sctrcd` |
| `src/main/java/.../payments/config/WebConfig.java` | Locale cookie and interceptor, and the `/` view |
| `src/main/java/.../payments/validation/web/` | The controller |
| `src/main/java/.../payments/validation/{iban,bic,payment}/` | The rule-based and simple validators |
| `src/main/java/.../drools/util/` | Knowledge base and listener helpers |
| `src/main/resources/rules/payments/validation/` | The DRL rules |
| `src/main/resources/templates/` | Thymeleaf pages |
| `src/main/resources/static/` | CSS and JavaScript, served under `/resources/**` |
| `.githooks/commit-msg` | Conventional Commits check, shared from `scattercode/release-pipelines` |

## Checks

```bash
./mvnw spotless:apply   # format the Java sources and the POM
./mvnw verify           # everything below, and the tests
```

`./mvnw verify` runs, in order:

- the **enforcer**: the JDK is at least `java.version`, Maven is at least
  3.9, and dependency versions converge;
- the **Spotless** check (palantir-java-format for Java, sortpom for
  `pom.xml`) in the `validate` phase;
- the **compiler** with `-Xlint:all -Werror`, so any warning fails the build;
- the **tests**, then the executable jar.

It needs JDK 25; the Maven wrapper fetches Maven itself. All of it must
pass before a commit. The lefthook hooks run the format check before each
commit and the full build before each push; CI runs the build and a Trivy
scan on every push and pull request.

## Conventions

- British English; sentence case for headings.
- Conventional Commits, enforced by `.githooks/commit-msg` and, for pull
  request titles, by `pr-title.yml`. Dependency updates are `build:`,
  workflow changes `ci:`.
- `.githooks/commit-msg` is a byte-for-byte copy of the template in
  `scattercode/release-pipelines`; CI checks it has not drifted. Change it
  there, not here.
- `main` is protected: changes arrive through pull requests with the CI
  checks passing.
- Hook setup in a fresh clone: `lefthook install`, or without lefthook,
  `git config core.hooksPath .githooks` for the commit message check alone.
