Payment validation web application
==================================

This project is a Spring Boot web application that shows Drools rules at work behind a web
service. It validates IBANs, BICs and foreign exchange payments with the rules in
`src/main/resources/rules/payments/validation/`:

| Rules | What they check |
|---|---|
| `IbanRules.drl` | The Mod-97 checksum, a known country code, and the UK BBAN structure |
| `BicRules.drl` | The ISO 9362 structure and a known country code |
| `PaymentRules.drl` | Gathers the IBAN and BIC annotations onto the payment and rejects it if any is a rejection |

It needs Java 25 and uses Spring Boot 4 and Apache KIE Drools 10. The Maven wrapper downloads Maven
itself, so to run a full build, including the format check and the tests:

    ./mvnw clean verify

Then start the application on port 9090, either from the build or straight from the sources:

    java -jar target/sctrcd-payment-validation-web-1.0.0-SNAPSHOT.jar
    ./mvnw spring-boot:run

Now you can validate IBANs. For instance:

    curl http://localhost:9090/iban/validate/GB29NWBK60161331926819

... will tell you that the IBAN is valid. At least it's valid from the perspective of a
Mod-97 check and has the right kind of structure.

    curl http://localhost:9090/iban/validate/GB29NWBK60161331926820

... should tell you that it failed the Mod-97 check.

The other endpoints are:

| Endpoint | Returns |
|---|---|
| `GET /` | The welcome page |
| `GET /iban/validator` | A page with a form that validates an IBAN through the service |
| `GET /iban/validate/{iban}` | The IBAN rules' verdict, as JSON |
| `GET /bic/validate/{bic}` | A simple (non-rule) BIC check, as JSON |
| `GET /payment/validate?iban=...&bic=...` | The verdict of all three rule files on a payment, as JSON |

`PaymentValidationEndpointsTest` runs the curl examples above, and the rest of the endpoints,
through MockMvc.

If the format check fails, `./mvnw spotless:apply` fixes it. To install the git hooks, which
check formatting before each commit and run the build before each push:

    lefthook install

See [AGENTS.md](AGENTS.md) for the conventions and [SECURITY.md](SECURITY.md) for reporting a
vulnerability.

Licensed under the [MIT License](LICENSE): copy and adapt it freely.
