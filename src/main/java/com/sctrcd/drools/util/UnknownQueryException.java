package com.sctrcd.drools.util;

public class UnknownQueryException extends RuntimeException {

    private static final long serialVersionUID = 2549141322931326165L;

    public UnknownQueryException(String packageName, String queryName) {
        super("Unable to find query [" + queryName + "] in package [" + packageName + "]");
    }

}
