package com.sctrcd.drools.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class StatelessExecutionResults {

    private Map<String, List<Object>> queryResults = new HashMap<String, List<Object>>();

    public StatelessExecutionResults() {
    }
    
    public void addQueryResults(String queryName, List<Object> facts) {
        this.queryResults.put(queryName, facts);
    }
    
    public List<Object> getQueryResults(String queryName) { 
        return this.queryResults.get(queryName);
    }

}
