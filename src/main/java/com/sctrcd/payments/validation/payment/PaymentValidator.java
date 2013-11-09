package com.sctrcd.payments.validation.payment;

import com.sctrcd.payments.facts.Payment;

public interface PaymentValidator {
	
	public FxPaymentValidationResult validatePayment(Payment payment);
    
}
