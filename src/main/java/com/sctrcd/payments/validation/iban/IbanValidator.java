package com.sctrcd.payments.validation.iban;

public interface IbanValidator {

    public IbanValidationResult validateIban(String iban);
    
}
