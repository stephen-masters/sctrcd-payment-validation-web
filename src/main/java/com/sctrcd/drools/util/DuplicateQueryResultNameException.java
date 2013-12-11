package com.sctrcd.drools.util;

import org.drools.definition.rule.Query;

public class DuplicateQueryResultNameException extends RuntimeException {

    private static final long serialVersionUID = 883612644972106312L;

    /**
     * 
     * @param packageName The package in which this query is found.
     * @param queryName The name of this query.
     * @param resultName The key to the map where the query results will be saved.
     * @param existingQuery The query which is already mapped by the <code>resultName</code>.
     */
    public DuplicateQueryResultNameException(String packageName, String queryName,
            String resultName, Query existingQuery) {
        
        super("Unable to add "
                + "query [" + queryName + "] "
                + "in package [" + packageName + "]. "
                + "Result name already exists for "
                + "query [" + existingQuery.getName() + "] "
                + "in package [" + existingQuery.getPackageName() + "].");
    }

}
