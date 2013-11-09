package com.sctrcd.payments.validation.bic;

import java.util.Collection;

import com.sctrcd.payments.facts.PaymentValidationAnnotation;
import com.sctrcd.payments.validation.ValidationResult;

/**
 * Encapsulates an IBAN and the result of validating it.
 * 
 * @author Stephen Masters
 */
public class BicValidationResult extends ValidationResult {

    private String bic;

    public BicValidationResult() {
        super();
    }
    
    public BicValidationResult(String bic) {
        super();
        this.bic = bic;
    }
    
    public BicValidationResult(String bic, boolean isValid) {
        this(bic);
        this.setValid(isValid);
    }

    public BicValidationResult(String bic, boolean isValid, PaymentValidationAnnotation annotation) {
        this(bic, isValid);
        this.addAnnotation(annotation);
    }
    
    public BicValidationResult(String bic, boolean isValid, Collection<PaymentValidationAnnotation> annotations) {
        this(bic, isValid);
        this.addAnnotations(annotations);
    }

    public String getBic() {
        return bic;
    }

    public void setBic(String bic) {
        this.bic = bic;
    }
    
}
