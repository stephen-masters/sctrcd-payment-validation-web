package com.sctrcd.payments.validation.iban;

import static org.junit.jupiter.api.Assertions.*;

import com.sctrcd.payments.facts.AnnotationLevel;
import com.sctrcd.payments.facts.PaymentAttribute;
import com.sctrcd.payments.validation.ValidationTestHelper;
import org.junit.jupiter.api.Test;

/**
 * Unit tests for the {@link FxPaymentValidator}.
 *
 * @author Stephen Masters
 */
public class RuleBasedIbanValidatorTest {

    IbanValidator validator = new RuleBasedIbanValidator();

    @Test
    public final void shouldAcceptValidIbans() {
        for (String iban : SimpleIbanValidatorTest.validIbans) {
            assertTrue(IbanMod97Check.isValid(iban));
            IbanValidationResult result = validator.validateIban(IbanUtil.sanitize(iban));

            assertTrue(result.isValid());
            ValidationTestHelper.assertNoRejectionsForAttribute(result.getAnnotations(), PaymentAttribute.iban);
        }
    }

    @Test
    public void tmp() {
        assertTrue(AnnotationLevel.REJECT.ordinal() > AnnotationLevel.WARN.ordinal());
    }

    @Test
    public final void shouldRejectInvalidIbans() {
        for (String iban : SimpleIbanValidatorTest.invalidIbans) {
            assertFalse(IbanMod97Check.isValid(iban));
            IbanValidationResult result = validator.validateIban(IbanUtil.sanitize(iban));

            assertFalse(result.isValid());
            ValidationTestHelper.assertAtLeastOneRejectionForAttribute(result.getAnnotations(), PaymentAttribute.iban);
        }
    }
}
