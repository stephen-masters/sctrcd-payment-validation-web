package com.sctrcd.payments.validation;

import static org.junit.Assert.assertTrue;

import java.util.List;

import com.sctrcd.payments.facts.PaymentAttribute;
import com.sctrcd.payments.facts.PaymentValidationAnnotation;

public class ValidationTestHelper {

    public static void assertAtLeastOneRejectionForAttribute(
            List<PaymentValidationAnnotation> annotations,
            PaymentAttribute attribute) {
        for (PaymentValidationAnnotation ann : annotations) {
            if (ann.getAttribute() == attribute && !ann.isValid()) {
                return;
            }
        }
        assertTrue("Failed to find a rejection for the " + attribute + " attribute.", false);
    }
    
    /**
     * Iterates through the annotations on a payment and validates that none of
     * them are rejections of the IBAN field.
     * 
     * @param annotations All the annotations added to a payment.
     */
    public static void assertNoRejectionsForAttribute(
            List<PaymentValidationAnnotation> annotations,
            PaymentAttribute attribute) {
        for (PaymentValidationAnnotation ann : annotations) {
            if (ann.getAttribute() == attribute) {
                assertTrue(ann.isValid());
            }
        }
    }

}
