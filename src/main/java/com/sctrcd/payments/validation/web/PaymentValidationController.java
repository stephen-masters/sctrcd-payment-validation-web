package com.sctrcd.payments.validation.web;

import org.springframework.web.servlet.ModelAndView;

import com.sctrcd.payments.facts.Payment;
import com.sctrcd.payments.validation.FxPaymentValidationResult;
import com.sctrcd.payments.validation.IbanValidationResult;

public interface PaymentValidationController {

    ModelAndView validator();

	IbanValidationResult validateIban(String iban);
	
	FxPaymentValidationResult validatePayment(Payment payment);

}
