package com.sctrcd.payments.facts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.sctrcd.payments.facts.AnnotationLevel;

public abstract class ValidationRequest {

    private boolean isValid = true;
    private AnnotationLevel mostSevereAnnotation = null;
    private List<PaymentValidationAnnotation> annotations = new ArrayList<PaymentValidationAnnotation>();

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
    
    public AnnotationLevel getMostSevereAnnotation() {
        return mostSevereAnnotation;
    }

    public void setMostSevereAnnotation(AnnotationLevel mostSevereAnnotation) {
        this.mostSevereAnnotation = mostSevereAnnotation;
    }

    public List<PaymentValidationAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void addAnnotation(PaymentValidationAnnotation annotation) {
        annotations.add(annotation);
    }
    
    public void reject(String ruleName, String message) {
        annotations.add(new PaymentValidationAnnotation(ruleName, AnnotationLevel.REJECT, message));
    }
    
    public void reject(String ruleName, String message, PaymentAttribute attribute) {
        annotations.add(new PaymentValidationAnnotation(ruleName, AnnotationLevel.REJECT, message, attribute));
    }
    
}
