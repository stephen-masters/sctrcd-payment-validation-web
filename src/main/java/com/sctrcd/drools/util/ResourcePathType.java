package com.sctrcd.drools.util;

/**
 * A subset of 'path-based' resource types that can be defined by a String for
 * adding to a Drools resource builder.
 * <p>
 * There used to be a URL type as well, for loading packages from Guvnor over
 * its REST API. Drools no longer reads resources from URLs, and Guvnor is long
 * gone, so it was removed.
 * </p>
 *
 * @author Stephen Masters
 */
public enum ResourcePathType {
    CLASSPATH,
    FILE;
}
