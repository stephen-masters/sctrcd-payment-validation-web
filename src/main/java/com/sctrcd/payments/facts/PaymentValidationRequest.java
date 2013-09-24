package com.sctrcd.payments.facts;

public class PaymentValidationRequest extends ValidationRequest {

    private Payment payment;

    public PaymentValidationRequest() {
        super();
    }
    
    public PaymentValidationRequest(Payment payment) {
        super();
        this.payment = payment;
    }
    
    public Payment getPayment() {
        return payment;
    }

    public void setPayment(Payment payment) {
        this.payment = payment;
    }

}
