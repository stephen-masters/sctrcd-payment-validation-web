package com.sctrcd.payments.validation;

/**
 * Encapsulates an IBAN and the result of validating it.
 * 
 * @author Stephen Masters
 */
public class IbanValidationResult extends ValidationResult {

    private String iban;

    public IbanValidationResult() {
        super();
    }
    
    public IbanValidationResult(String iban) {
        super();
        this.iban = iban;
    }
    
    public IbanValidationResult(String iban, boolean isValid) {
        this(iban);
        this.setValid(isValid);
    }

    public String getIban() {
        return iban;
    }

    public void setIban(String iban) {
        this.iban = iban;
    }
    
}
