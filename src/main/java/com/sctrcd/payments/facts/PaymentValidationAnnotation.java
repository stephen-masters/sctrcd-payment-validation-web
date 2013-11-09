package com.sctrcd.payments.facts;

public class PaymentValidationAnnotation {

    private final String ruleName;
    private final AnnotationLevel level;
    private final String message;
    
    /**
     * If this annotation relates to a specific attribute of the payment, this
     * field will refer to it.
     */
    private final PaymentAttribute attribute;

    /**
     * Represents a general payment annotation, as opposed to an annotation
     * referring to a specific attribute.
     * 
     * @param ruleName
     * @param level
     * @param message
     */
    public PaymentValidationAnnotation(String ruleName, AnnotationLevel level, String message) {
        this.ruleName = ruleName;
        this.level = level;
        this.message = message;
        this.attribute = null;
    }
    
    /**
     * Represents an annotation of a particular attribute of a payment. i.e. Rejecting the IBAN or BIC.
     * 
     * @param ruleName
     * @param level
     * @param message
     * @param attribute
     */
    public PaymentValidationAnnotation(String ruleName, AnnotationLevel level, String message, PaymentAttribute attribute) {
        this.ruleName = ruleName;
        this.level = level;
        this.message = message;
        this.attribute = attribute;
    }

    public PaymentAttribute getAttribute() {
        return attribute;
    }
    
    public AnnotationLevel getLevel() {
        return level;
    }

    public String getMessage() {
        return message;
    }

    public String getRuleName() {
        return ruleName;
    }
    
    public boolean isValid() {
        return this.level != AnnotationLevel.REJECT;
    }

    public String toString() {
        return "[" + level + "]: " + message;
    }
    
}
