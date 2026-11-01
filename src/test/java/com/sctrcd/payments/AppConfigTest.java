package com.sctrcd.payments;

import static org.junit.jupiter.api.Assertions.*;

import com.sctrcd.payments.validation.iban.IbanValidator;
import com.sctrcd.payments.validation.payment.PaymentValidator;
import com.sctrcd.payments.validation.web.PaymentValidationController;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;

/**
 * This test is purely to establish that key beans in the application are
 * injected correctly. It just defines references to beans and asserts that they
 * exist. Some of these are beans which wrap Drools knowledge bases, so they
 * will load those knowledge bases as part of the test. If any contain DRL code
 * which does not compile, then this test will fail.
 *
 * @author Stephen Masters
 */
@SpringBootTest
public class AppConfigTest {

    @Autowired(required = true)
    @Qualifier("ruleBasedPaymentValidator")
    private PaymentValidator paymentValidator;

    @Autowired(required = true)
    @Qualifier("ruleBasedIbanValidator")
    private IbanValidator ibanValidator;

    @Autowired(required = true)
    @Qualifier("paymentValidationController")
    private PaymentValidationController paymentValidationcontroller;

    @Test
    public void shouldInjectBeans() {
        assertNotNull(paymentValidator);
        assertNotNull(ibanValidator);
        assertNotNull(paymentValidationcontroller);
    }
}
