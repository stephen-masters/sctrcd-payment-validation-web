package com.sctrcd.payments.validation.web;

import static org.junit.Assert.*;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.support.AnnotationConfigContextLoader;
import org.springframework.web.servlet.ModelAndView;

import com.sctrcd.payments.PaymentAppConfig;
import com.sctrcd.payments.facts.Payment;
import com.sctrcd.payments.validation.FxPaymentValidationResult;
import com.sctrcd.payments.validation.IbanValidationResult;
import com.sctrcd.payments.validation.RuleBasedPaymentValidatorTest;
import com.sctrcd.payments.validation.web.PaymentValidationController;

@RunWith(SpringJUnit4ClassRunner.class)
@ContextConfiguration(classes = PaymentAppConfig.class, loader = AnnotationConfigContextLoader.class)
public class PaymentValidationControllerTest {

    @Autowired(required = true)
    @Qualifier("paymentValidationController")
    private PaymentValidationController controller;

    @Test
    public void shouldProvideForm() {
        ModelAndView mav = controller.validator();
        assertEquals("iban/validator", mav.getViewName());
    }

    @Test
    public void shouldValidateIban() {
        String iban = "ES23 0217 0099 47";
        IbanValidationResult result = controller.validateIban(iban);
        assertNotNull(result);
        assertEquals("ES23 0217 0099 47", result.getIban());
    }

    /**
     * This test is only for checking that the controller has been injected with
     * an appropriate validator which supports the payment validation. It does
     * not perform full analysis of which rules are activated. That kind of
     * validation is done in {@link RuleBasedPaymentValidatorTest}.
     * <p>
     * Note that because this only tests against a very minimal payment, there
     * is a possibility that it will fail due to changes to the rules which
     * require additional attributes on the payment for verification.
     * </p>
     */
    @Test
    public void shouldValidatePayment() {
        Payment payment = new Payment();
        payment.setIban("ES23 0217 0099 47");
        
        FxPaymentValidationResult result = controller.validatePayment(payment);
        assertNotNull(result);
    }

}
