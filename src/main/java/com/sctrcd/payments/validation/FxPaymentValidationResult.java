package com.sctrcd.payments.validation;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

import com.sctrcd.payments.facts.ValidationAnnotation;


public class FxPaymentValidationResult {

    private boolean isValid = true;
    private List<ValidationAnnotation> annotations = new ArrayList<ValidationAnnotation>();

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

    public List<ValidationAnnotation> getAnnotations() {
        return Collections.unmodifiableList(annotations);
    }

    public void setAnnotations(List<ValidationAnnotation> annotations) {
        this.annotations = new ArrayList<ValidationAnnotation>(annotations);
        for (ValidationAnnotation annotation : annotations) {
            rejectIfAnnotationRejects(annotation);
        }
    }

    public void addAnnotation(ValidationAnnotation annotation) {
        this.annotations.add(annotation);
        rejectIfAnnotationRejects(annotation);
    }
    
    public void addAnnotations(Collection<ValidationAnnotation> annotations) {
        this.annotations.addAll(annotations);
        rejectIfAnnotationRejects(annotations);
    }

    /**
     * For every annotation added to the result, 
     * @param annotation
     */
    private void rejectIfAnnotationRejects(ValidationAnnotation annotation) {
        if (!annotation.isValid()) {
            this.isValid = false;
        }
    }
    
    /**
     * For every annotation added to the result, 
     * @param annotation
     */
    private void rejectIfAnnotationRejects(Collection<ValidationAnnotation> annotations) {
        for (ValidationAnnotation ann : annotations) {
            rejectIfAnnotationRejects(ann);
        }
    }

}
