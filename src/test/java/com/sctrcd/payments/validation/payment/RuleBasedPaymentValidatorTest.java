package com.sctrcd.payments.validation.payment;

import org.junit.Test;

import com.sctrcd.payments.facts.Payment;
import com.sctrcd.payments.facts.PaymentAttribute;
import com.sctrcd.payments.validation.ValidationTestHelper;
import com.sctrcd.payments.validation.payment.FxPaymentValidationResult;
import com.sctrcd.payments.validation.payment.PaymentValidator;
import com.sctrcd.payments.validation.payment.RuleBasedPaymentValidator;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link FxPaymentValidator}.
 * 
 * @author Stephen Masters
 */
public class RuleBasedPaymentValidatorTest {

    PaymentValidator validator = new RuleBasedPaymentValidator();

    @Test
    public final void shouldAcceptValidIbans() {
        Payment payment = new Payment();
        payment.setIban("LU36 0029 1524 6005 0000");
        FxPaymentValidationResult result = validator.validatePayment(payment);

        ValidationTestHelper.assertNoRejectionsForAttribute(result.getAnnotations(), PaymentAttribute.iban);

        // A payment could be rejected for reasons other than the IBAN, so in
        // the future, this assertion could break for a valid reason. At that
        // point, it may be most appropriate to remove calls to this assertion.
        // Alternatively the tests could be set up to configure a fully valid
        // payment *except* for the IBAN.
        assertTrue(result.isValid());
    }

    @Test
    public final void shouldRejectInvalidIbans() {
        Payment payment = new Payment();
        payment.setIban("ES95 0217 0100 17");
        FxPaymentValidationResult result = validator.validatePayment(payment);

        assertFalse(result.isValid());
        ValidationTestHelper.assertAtLeastOneRejectionForAttribute(result.getAnnotations(), PaymentAttribute.iban);
    }

    @Test
    public final void shouldAcceptValidUkIbans() {
        Payment payment = new Payment();
        payment.setIban("GB29 NWBK 6016 1331 9268 19");
        FxPaymentValidationResult result = validator.validatePayment(payment);

        ValidationTestHelper.assertNoRejectionsForAttribute(result.getAnnotations(), PaymentAttribute.iban);

        // A payment could be rejected for reasons other than the IBAN, so in
        // the future, this assertion could break for a valid reason. At that
        // point, it may be most appropriate to remove calls to this assertion.
        // Alternatively the tests could be set up to configure a fully valid
        // payment *except* for the IBAN.
        assertTrue(result.isValid());
    }
    
    @Test
    public final void shouldAcceptValidBics() {
        Payment payment = new Payment();
        payment.setBic("HLFXESMM");
        FxPaymentValidationResult result = validator.validatePayment(payment);

        ValidationTestHelper.assertNoRejectionsForAttribute(result.getAnnotations(), PaymentAttribute.bic);

        // A payment could be rejected for reasons other than the IBAN, so in
        // the future, this assertion could break for a valid reason. At that
        // point, it may be most appropriate to remove calls to this assertion.
        // Alternatively the tests could be set up to configure a fully valid
        // payment *except* for the IBAN.
        assertTrue(result.isValid());
    }
    
    @Test
    public final void shouldRejectInvalidBics() {
        Payment payment = new Payment();
        payment.setBic("HLFXESM");
        FxPaymentValidationResult result = validator.validatePayment(payment);

        assertFalse(result.isValid());
        
        ValidationTestHelper.assertAtLeastOneRejectionForAttribute(result.getAnnotations(), PaymentAttribute.bic);
    }
    
}
