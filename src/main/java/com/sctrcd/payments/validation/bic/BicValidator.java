package com.sctrcd.payments.validation.bic;

public interface BicValidator {

    public BicValidationResult validate(String bic);
    
}
