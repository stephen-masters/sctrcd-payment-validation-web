package com.sctrcd.payments.validation.bic;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import com.sctrcd.payments.facts.AnnotationLevel;
import com.sctrcd.payments.facts.PaymentAttribute;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

public class SimpleBicValidatorTest {

    private final SimpleBicValidator validator = new SimpleBicValidator();

    @ParameterizedTest
    @ValueSource(strings = {"HLFXESMM", "HLFXESMM123", "DEUTDEFF", "DEUTDEFF500", "hlfxesmm"})
    public void acceptsBicsThatFollowIso9362(String bic) {
        assertTrue(SimpleBicValidator.isValid(bic));

        BicValidationResult result = validator.validate(bic);
        assertTrue(result.isValid());
        assertTrue(result.getAnnotations().isEmpty());
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "HLFXESM", "HLFXESMM1", "HLFXESMM1234", "1234ESMM", "HLFX12MM"})
    public void rejectsBicsThatDoNotFollowIso9362(String bic) {
        assertFalse(SimpleBicValidator.isValid(bic));

        BicValidationResult result = validator.validate(bic);
        assertFalse(result.isValid());
        assertEquals(1, result.getAnnotations().size());
        assertEquals(AnnotationLevel.REJECT, result.getAnnotations().get(0).getLevel());
        assertEquals(PaymentAttribute.bic, result.getAnnotations().get(0).getAttribute());
    }

    @org.junit.jupiter.api.Test
    public void rejectsANullBic() {
        assertFalse(SimpleBicValidator.isValid(null));
    }
}
