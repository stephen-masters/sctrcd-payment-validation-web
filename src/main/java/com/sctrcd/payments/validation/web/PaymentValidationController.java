package com.sctrcd.payments.validation.web;

import org.springframework.web.servlet.ModelAndView;

import com.sctrcd.payments.facts.Payment;
import com.sctrcd.payments.validation.bic.BicValidationResult;
import com.sctrcd.payments.validation.iban.IbanValidationResult;
import com.sctrcd.payments.validation.payment.FxPaymentValidationResult;

/**
 * Provides a REST API for validating international payments and their component
 * parts.
 * 
 * @author Stephen Masters
 */
public interface PaymentValidationController {

    ModelAndView validator();

    /**
     * 
     * @param bic
     *            A SWIFT BIC code, as defined by ISO 9362:2009.
     * @return A result object indicating whether the BIC is valid.
     */
    BicValidationResult validateBic(String bic);

    /**
     * 
     * @param iban
     *            An International Bank Account Number (IBAN)
     * @return A result object indicating whether the IBAN is valid.
     */
    IbanValidationResult validateIban(String iban);

    /**
     * 
     * @param payment
     *            An international payment, encapsulating information such as
     *            account details, currencies, foreign exchange rates and
     *            amounts.
     * @return A result object indicating whether the payment in valid and if
     *         not, which attribute of the payment caused the rejection.
     */
    FxPaymentValidationResult validatePayment(Payment payment);

}
