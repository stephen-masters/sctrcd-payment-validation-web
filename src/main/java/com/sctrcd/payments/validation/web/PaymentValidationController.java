package com.sctrcd.payments.validation.web;

import org.springframework.web.servlet.ModelAndView;

import com.sctrcd.payments.facts.Payment;
import com.sctrcd.payments.validation.bic.BicValidationResult;
import com.sctrcd.payments.validation.iban.IbanValidationResult;
import com.sctrcd.payments.validation.payment.FxPaymentValidationResult;

public interface PaymentValidationController {

    ModelAndView validator();

    BicValidationResult validateBic(String bic);
    
	IbanValidationResult validateIban(String iban);
	
	FxPaymentValidationResult validatePayment(Payment payment);

}
