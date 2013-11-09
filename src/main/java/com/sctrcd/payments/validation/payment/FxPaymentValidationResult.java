package com.sctrcd.payments.validation.payment;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.sctrcd.payments.facts.PaymentValidationAnnotation;
import com.sctrcd.payments.facts.AnnotationLevel;


public class FxPaymentValidationResult {

    private boolean isValid = true;
    private List<PaymentValidationAnnotation> annotations = new ArrayList<PaymentValidationAnnotation>();

    public FxPaymentValidationResult() {
    }
    
    public FxPaymentValidationResult(boolean isValid) {
        this.isValid = isValid;
    }
    
    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public List<PaymentValidationAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void setAnnotations(List<PaymentValidationAnnotation> annotations) {
        this.annotations = new ArrayList<PaymentValidationAnnotation>(annotations);
        for (PaymentValidationAnnotation annotation : annotations) {
            rejectIfAnnotationRejects(annotation);
        }
    }

    public void addAnnotation(PaymentValidationAnnotation annotation) {
        this.annotations.add(annotation);
        rejectIfAnnotationRejects(annotation);
    }
    
    public void addAnnotations(Collection<PaymentValidationAnnotation> annotations) {
        this.annotations.addAll(annotations);
        rejectIfAnnotationRejects(annotations);
    }

    /**
     * For every annotation added to the result, 
     * @param annotation
     */
    private void rejectIfAnnotationRejects(PaymentValidationAnnotation annotation) {
        if (annotation.getLevel() == AnnotationLevel.REJECT) {
            this.isValid = false;
        }
    }
    
    /**
     * For every annotation added to the result, 
     * @param annotation
     */
    private void rejectIfAnnotationRejects(Collection<PaymentValidationAnnotation> annotations) {
        for (PaymentValidationAnnotation ann : annotations) {
            rejectIfAnnotationRejects(ann);
        }
    }

}
