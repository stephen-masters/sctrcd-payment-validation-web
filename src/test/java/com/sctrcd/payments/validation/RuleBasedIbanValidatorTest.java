package com.sctrcd.payments.validation;

import org.junit.Test;


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
            assertTrue(Mod97Check.isValid(iban));
            IbanValidationResult result = validator.validateIban(IbanUtil.sanitize(iban));
            assertTrue(result.isValid());
        }
    }
    
    @Test
    public final void shouldRejectInvalidIbans() {
        for (String iban : SimpleIbanValidatorTest.invalidIbans) {
            assertFalse(Mod97Check.isValid(iban));
            IbanValidationResult result = validator.validateIban(IbanUtil.sanitize(iban));
            assertFalse(result.isValid());
        }
    }

}
