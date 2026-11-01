package com.sctrcd.payments.validation.bic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sctrcd.payments.facts.AnnotationLevel;
import com.sctrcd.payments.facts.PaymentAttribute;
import com.sctrcd.payments.facts.PaymentValidationAnnotation;
import java.util.List;
import org.junit.jupiter.api.Test;

/**
 * The rule-based validator on its own, outside a payment: the BIC rules'
 * annotations must reach the result.
 */
public class RuleBasedBicValidatorTest {

    private final RuleBasedBicValidator validator = new RuleBasedBicValidator();

    @Test
    public void acceptsAValidBic() {
        BicValidationResult result = validator.validate("HLFXESMM");

        assertTrue(result.isValid());
        assertTrue(result.getAnnotations().isEmpty());
    }

    @Test
    public void rejectsABicWithTheWrongStructure() {
        assertRejectedBy(validator.validate("HLFXESM"), "BIC doesn't follow ISO 9362 structure.");
    }

    @Test
    public void rejectsABicWithAnUnknownCountry() {
        assertRejectedBy(validator.validate("HLFXEXMM"), "BIC doesn't contain a valid country ISO code.");
    }

    private static void assertRejectedBy(BicValidationResult result, String ruleName) {
        assertFalse(result.isValid());

        List<PaymentValidationAnnotation> annotations = result.getAnnotations();
        assertEquals(1, annotations.size(), annotations.toString());
        assertEquals(ruleName, annotations.get(0).getRuleName());
        assertEquals(AnnotationLevel.REJECT, annotations.get(0).getLevel());
        assertEquals(PaymentAttribute.bic, annotations.get(0).getAttribute());
    }
}
