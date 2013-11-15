package com.sctrcd.drools.util;

import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;

import org.drools.runtime.ObjectFilter;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.rule.FactHandle;

import com.sctrcd.beans.BeanMatcher;
import com.sctrcd.beans.BeanPropertyFilter;


public class FactFinder {

    BeanMatcher beanMatcher = new BeanMatcher();

    /**
     * An assertion that a fact of the expected class with specified properties
     * is in working memory.
     * 
     * @param kenv
     *            A {@link KnowledgeEnvironment} containing a
     *            {@link KnowledgeSession} in which we are looking for the fact.
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Collection<Object> findFacts(final KnowledgeEnvironment kenv,
            final String factClass,
            final BeanPropertyFilter... expectedProperties) {
        return findFacts(kenv.getKnowledgeSession(), factClass, expectedProperties);
    }

    /**
     * An assertion that a fact of the expected class with specified properties
     * is in working memory.
     * 
     * @param session
     *            A {@link KnowledgeSession} in which we are looking for the
     *            fact.
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Collection<Object> findFacts(final StatefulKnowledgeSession session,
            final String factClass,
            final BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().getSimpleName().equals(factClass);
            }
        };

        Collection<FactHandle> factHandles = session.getFactHandles(filter);
        Collection<Object> facts = new ArrayList<Object>();
        for (FactHandle handle : factHandles) {
            Object fact = session.getObject(handle);
            if (beanMatcher.matches(fact, expectedProperties)) {
                facts.add(fact);
            }
        }
        return facts;
    }
    
    public Collection<Object> findFacts(final StatefulKnowledgeSession session,
            final Class<Object> factClass,
            final BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().equals(factClass);
            }
        };

        Collection<FactHandle> factHandles = session.getFactHandles(filter);
        Collection<Object> facts = new ArrayList<Object>();
        for (FactHandle handle : factHandles) {
            Object fact = session.getObject(handle);
            if (beanMatcher.matches(fact, expectedProperties)) {
                facts.add(fact);
            }
        }
        return facts;
    }

}
