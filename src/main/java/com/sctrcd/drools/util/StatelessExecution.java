package com.sctrcd.drools.util;

import java.util.HashMap;
import java.util.Map;

import org.drools.KnowledgeBase;
import org.drools.definition.rule.Query;
import org.drools.runtime.StatefulKnowledgeSession;

public class StatelessExecution {

    private KnowledgeBase kbase;
    private StatefulKnowledgeSession ksession;
    private Map<String, Query> queries = new HashMap<String, Query>();

    public StatelessExecution(KnowledgeBase kbase) {
        this.kbase = kbase;
    }
    
    public StatelessExecution withQuery(String packageName, String queryName, String resultName) {
        Query query = this.kbase.getQuery(packageName, queryName);
        if (query == null) { 
            throw new UnknownQueryException(packageName, queryName);
        }
        if (queries.containsKey(resultName)) {
            Query existingQuery = queries.get(resultName);
            throw new DuplicateQueryResultNameException(packageName, queryName, resultName, existingQuery);
        }
        queries.put(resultName, query);
        return this;
    }

    public StatelessExecutionResults execute() {
        ksession = kbase.newStatefulKnowledgeSession();
        ksession.fireAllRules();
        StatelessExecutionResults results = new StatelessExecutionResults();
        return results;
    }
    
}
