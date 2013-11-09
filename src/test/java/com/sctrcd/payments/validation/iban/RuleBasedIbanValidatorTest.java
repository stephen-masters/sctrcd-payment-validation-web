package com.sctrcd.payments.validation.iban;

import java.util.List;

import org.junit.Test;

import com.sctrcd.payments.facts.AnnotationLevel;
import com.sctrcd.payments.facts.PaymentAttribute;
import com.sctrcd.payments.facts.PaymentValidationAnnotation;
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
            assertNoIbanRejections(result.getAnnotations());
            assertTrue(result.isValid());
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
            for (PaymentValidationAnnotation annotation : result.getAnnotations()) {
                assertFalse(annotation.isValid());
            }
            assertFalse(result.isValid());
        }
    }
    
    /**
     * Iterates through the annotations on a payment and validates that none of
     * them are rejections of the IBAN field.
     * 
     * @param annotations All the annotations added to a payment.
     */
    private void assertNoIbanRejections(
            List<PaymentValidationAnnotation> annotations) {
        for (PaymentValidationAnnotation ann : annotations) {
            if (ann.getAttribute() == PaymentAttribute.iban) {
                assertTrue(ann.isValid());
            }
        }
    }
    
}
