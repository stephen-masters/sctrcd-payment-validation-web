package com.sctrcd.payments.validation;

public interface IbanValidator {

    public IbanValidationResult validateIban(String iban);
    
}
