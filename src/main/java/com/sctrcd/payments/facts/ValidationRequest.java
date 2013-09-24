package com.sctrcd.payments.facts;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public abstract class ValidationRequest {

    private boolean isValid = true;
    private List<ValidationAnnotation> annotations = new ArrayList<ValidationAnnotation>();

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }
    
    public List<ValidationAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void addAnnotation(ValidationAnnotation annotation) {
        annotations.add(annotation);
    }
    
    public void reject(String message) {
        annotations.add(new ValidationAnnotation(false, message));
    }
    
    public void reject(String message, String attributeName) {
        annotations.add(new ValidationAnnotation(false, message, attributeName));
    }
    
}
