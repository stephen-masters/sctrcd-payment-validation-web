package com.sctrcd.payments.validation;

import com.sctrcd.payments.facts.Payment;

public interface PaymentValidator {
	
	public FxPaymentValidationResult validatePayment(Payment payment);
    
}
