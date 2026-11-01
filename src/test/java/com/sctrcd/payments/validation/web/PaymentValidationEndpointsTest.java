package com.sctrcd.payments.validation.web;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.hasItem;
import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.cookie;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.view;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;

/**
 * Drives every endpoint through the full Spring MVC stack, starting with the
 * curl examples in the README.
 */
@SpringBootTest
@AutoConfigureMockMvc
public class PaymentValidationEndpointsTest {

    private static final String MOD_97_RULE = "IBAN failed the Mod-97 checksum test.";

    @Autowired
    private MockMvc mvc;

    /**
     * README: <code>curl http://localhost:9090/iban/validate/GB29NWBK60161331926819</code>
     * says that the IBAN is valid.
     */
    @Test
    public void readmeExampleOfAValidIban() throws Exception {
        mvc.perform(get("/iban/validate/GB29NWBK60161331926819"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.iban").value("GB29NWBK60161331926819"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.annotations", empty()));
    }

    /**
     * README: <code>curl http://localhost:9090/iban/validate/GB29NWBK60161331926820</code>
     * says that the IBAN failed the Mod-97 check.
     */
    @Test
    public void readmeExampleOfAnIbanFailingMod97() throws Exception {
        mvc.perform(get("/iban/validate/GB29NWBK60161331926820"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("GB29NWBK60161331926820"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.annotations", hasSize(1)))
                .andExpect(jsonPath("$.annotations[0].ruleName").value(MOD_97_RULE))
                .andExpect(jsonPath("$.annotations[0].level").value("REJECT"))
                .andExpect(jsonPath("$.annotations[0].attribute").value("iban"))
                .andExpect(jsonPath("$.annotations[0].message").value("The IBAN is not valid."))
                .andExpect(jsonPath("$.annotations[0].valid").value(false));
    }

    @Test
    public void acceptsAnIbanWrittenInGroupsOfFour() throws Exception {
        mvc.perform(get("/iban/validate/{iban}", "GB29 NWBK 6016 1331 9268 19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.iban").value("GB29 NWBK 6016 1331 9268 19"))
                .andExpect(jsonPath("$.valid").value(true));
    }

    @Test
    public void rejectsAUkIbanWithoutTheBbanStructure() throws Exception {
        // Passes Mod-97, but a letter of the bank code has become a digit.
        mvc.perform(get("/iban/validate/{iban}", "GB29 NWB0 6016 1331 9268 19"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath(
                        "$.annotations[*].ruleName", hasItem("IBAN is for UK, but doesn't have BBAN structure.")));
    }

    @Test
    public void rejectsAnIbanWithAnUnknownCountryCode() throws Exception {
        mvc.perform(get("/iban/validate/XX29NWBK60161331926819"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath(
                        "$.annotations[*].ruleName", hasItem("IBAN doesn't begin with a valid country ISO code.")));
    }

    @Test
    public void acceptsAValidBic() throws Exception {
        mvc.perform(get("/bic/validate/HLFXESMM"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bic").value("HLFXESMM"))
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.annotations", empty()));
    }

    @Test
    public void rejectsABicWithTheWrongStructure() throws Exception {
        mvc.perform(get("/bic/validate/HLFXESM"))
                .andExpect(status().isOk())
                .andExpect(content().contentTypeCompatibleWith(MediaType.APPLICATION_JSON))
                .andExpect(jsonPath("$.bic").value("HLFXESM"))
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.annotations[0].ruleName").value("ISO_9362 check"))
                .andExpect(jsonPath("$.annotations[0].level").value("REJECT"))
                .andExpect(jsonPath("$.annotations[0].attribute").value("bic"));
    }

    @Test
    public void acceptsAPaymentWithAValidIban() throws Exception {
        mvc.perform(get("/payment/validate")
                        .param("iban", "GB29 NWBK 6016 1331 9268 19")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(true))
                .andExpect(jsonPath("$.annotations", empty()));
    }

    @Test
    public void rejectsAPaymentWithAnIbanFailingMod97() throws Exception {
        mvc.perform(get("/payment/validate")
                        .param("iban", "GB29 NWBK 6016 1331 9268 20")
                        .accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.annotations[*].ruleName", hasItem(MOD_97_RULE)))
                .andExpect(jsonPath("$.annotations[*].attribute", hasItem("iban")));
    }

    @Test
    public void rejectsAPaymentWithABadlyStructuredBic() throws Exception {
        mvc.perform(get("/payment/validate").param("bic", "HLFXESM").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath("$.annotations[*].ruleName", hasItem("BIC doesn't follow ISO 9362 structure.")))
                .andExpect(jsonPath("$.annotations[*].attribute", hasItem("bic")));
    }

    @Test
    public void rejectsAPaymentWithABicForAnUnknownCountry() throws Exception {
        mvc.perform(get("/payment/validate").param("bic", "HLFXEXMM").accept(MediaType.APPLICATION_JSON))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.valid").value(false))
                .andExpect(jsonPath(
                        "$.annotations[?(@.ruleName == \"BIC doesn't contain a valid country ISO code.\")].attribute",
                        hasItem("bic")));
    }

    @Test
    public void servesTheWelcomePage() throws Exception {
        mvc.perform(get("/").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("welcome"))
                .andExpect(content().string(containsString("<title>Scattercode FX</title>")))
                .andExpect(content().string(containsString("href=\"/iban/validator\"")))
                .andExpect(content().string(containsString("href=\"/resources/css/application.css\"")));
    }

    @Test
    public void servesTheIbanValidatorPage() throws Exception {
        mvc.perform(get("/iban/validator").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(view().name("iban/validator"))
                .andExpect(content().string(containsString("id=\"ibanRequest\"")))
                .andExpect(content().string(containsString("data-validate-url=\"/iban/validate/\"")))
                .andExpect(content().string(containsString("src=\"/resources/js/iban.js\"")));
    }

    @Test
    public void servesTheStaticResourcesUnderTheirOldPaths() throws Exception {
        mvc.perform(get("/resources/js/iban.js"))
                .andExpect(status().isOk())
                .andExpect(content().string(containsString("/iban/validate/")));
        mvc.perform(get("/resources/css/iban.css")).andExpect(status().isOk());
        mvc.perform(get("/resources/css/application.css")).andExpect(status().isOk());
    }

    @Test
    public void remembersALocaleChangeInACookie() throws Exception {
        mvc.perform(get("/").param("locale", "de").accept(MediaType.TEXT_HTML))
                .andExpect(status().isOk())
                .andExpect(cookie().value("org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE", "de"));
    }
}
