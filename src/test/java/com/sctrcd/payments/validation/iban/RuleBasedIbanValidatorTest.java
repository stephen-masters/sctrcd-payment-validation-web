package com.sctrcd.payments.validation.iban;

import org.junit.Test;

import com.sctrcd.payments.facts.AnnotationLevel;
import com.sctrcd.payments.facts.PaymentAttribute;
import com.sctrcd.payments.validation.ValidationTestHelper;
import com.sctrcd.payments.validation.iban.IbanMod97Check;
import com.sctrcd.payments.validation.iban.IbanUtil;
import com.sctrcd.payments.validation.iban.IbanValidationResult;
import com.sctrcd.payments.validation.iban.IbanValidator;
import com.sctrcd.payments.validation.iban.RuleBasedIbanValidator;


import static org.junit.Assert.*;

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
