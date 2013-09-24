package com.sctrcd.payments.facts;

public class ValidationAnnotation {

    private boolean isValid;
    private String message;
    /**
     * If this annotation relates to a specific attribute of the payment, this
     * field will refer to it.
     */
    private String attributeName;

    public ValidationAnnotation(boolean isValid) {
        this.isValid = isValid;
    }

    public ValidationAnnotation(boolean isValid, String message) {
        this(isValid);
        this.message = message;
    }
    
    public ValidationAnnotation(boolean isValid, String message, String attributeName) {
        this(isValid, message);
        this.attributeName = attributeName;
    }

    public boolean isValid() {
        return isValid;
    }

    public void setValid(boolean isValid) {
        this.isValid = isValid;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getAttributeName() {
        return attributeName;
    }

    public void setAttributeName(String attributeName) {
        this.attributeName = attributeName;
    }

    public String toString() {
        return (isValid ? "Valid: " : "Not valid: ") + message;
    }

}
