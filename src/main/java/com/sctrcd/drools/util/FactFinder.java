package com.sctrcd.drools.util;

import com.sctrcd.beans.BeanMatcher;
import com.sctrcd.beans.BeanPropertyFilter;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import org.kie.api.runtime.KieSession;
import org.kie.api.runtime.ObjectFilter;
import org.kie.api.runtime.rule.FactHandle;

public class FactFinder {

    BeanMatcher beanMatcher = new BeanMatcher();

    /**
     * An assertion that a fact of the expected class with specified properties
     * is in working memory.
     *
     * @param kenv
     *            A {@link KnowledgeEnvironment} containing a
     *            {@link KieSession} in which we are looking for the fact.
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Collection<Object> findFacts(
            final KnowledgeEnvironment kenv, final String factClass, final BeanPropertyFilter... expectedProperties) {
        return findFacts(kenv.getKnowledgeSession(), factClass, expectedProperties);
    }

    /**
     * An assertion that a fact of the expected class with specified properties
     * is in working memory.
     *
     * @param session
     *            A {@link KieSession} in which we are looking for the
     *            fact.
     * @param factClass
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public Collection<Object> findFacts(
            final KieSession session, final String factClass, final BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().getSimpleName().equals(factClass);
            }
        };

        Collection<? extends FactHandle> factHandles = session.getFactHandles(filter);
        Collection<Object> facts = new ArrayList<Object>();
        for (FactHandle handle : factHandles) {
            Object fact = session.getObject(handle);
            if (beanMatcher.matches(fact, expectedProperties)) {
                facts.add(fact);
            }
        }
        return facts;
    }

    public Collection<Object> findFacts(
            final KieSession session, final Class<Object> factClass, final BeanPropertyFilter... expectedProperties) {

        ObjectFilter filter = new ObjectFilter() {
            @Override
            public boolean accept(Object object) {
                return object.getClass().equals(factClass);
            }
        };

        Collection<? extends FactHandle> factHandles = session.getFactHandles(filter);
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
