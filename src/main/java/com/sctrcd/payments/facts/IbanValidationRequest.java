package com.sctrcd.payments.facts;

public class IbanValidationRequest {

    private String iban;

    public IbanValidationRequest() {
        super();
    }
    
    public IbanValidationRequest(String iban) {
        super();
        this.setIban(iban);
    }

    public String getIban() {
        return iban;
    }

    /**
     * Strips whitespace out of the IBAN before setting the field.
     * Whitespace is not relevant to validation.
     */
    public void setIban(String iban) {
        this.iban = iban.replace(" ", "");
    }
    
}
