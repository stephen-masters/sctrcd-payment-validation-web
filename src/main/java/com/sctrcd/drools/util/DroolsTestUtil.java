package com.sctrcd.drools.util;

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.lang.reflect.InvocationTargetException;

import com.sctrcd.beans.BeanPropertyFilter;


public class DroolsTestUtil {

    protected static final FactFinder factFinder = new FactFinder();

    /**
     * This class provides static methods, so we prevent instantiation.
     */
    private DroolsTestUtil() {
    }

    /**
     * The test will fail if any of the named rules could not be found in the
     * list of activations.
     * 
     * @param ruleNames
     *            A list of names of rules to look for.
     */
    public static void assertRuleFired(KnowledgeEnvironment kenv, String... ruleNames) {
        for (String ruleName : ruleNames) {
            assertTrue("Rule [" + ruleName + "] should have fired.", 
                    DroolsUtil.ruleFired(
                            kenv.findActivations(), 
                            ruleName));
        }
    }

    /**
     * The test will fail if any of the named rules fired.
     * 
     * @param ruleNames A list of names of rules to look for.
     */
    public static void assertRuleNotFired(KnowledgeEnvironment kenv, String... ruleNames) {
        for (String ruleName : ruleNames) {
            assertFalse("Rule [" + ruleName + "] should not have fired.", 
                    DroolsUtil.ruleFired(
                            kenv.findActivations(), 
                            ruleName));
        }
    }

    /**
     * A more complex assertion that a fact of the expected class with specified
     * properties is in working memory.
     * 
     * @param factName
     *            The simple name of the class of the fact we're looking for.
     * @param expectedProperties
     *            A sequence of expected property name/value pairs.
     * @throws IllegalAccessException
     * @throws InvocationTargetException
     * @throws NoSuchMethodException
     */
    public static void assertFactIsInWorkingMemory(KnowledgeEnvironment kenv, final String factClass, BeanPropertyFilter... expectedProperties) {
        assertTrue(factFinder.findFacts(
                kenv, 
                factClass, 
                expectedProperties).size() > 0);
    }

    public static void assertFactNotInWorkingMemory(KnowledgeEnvironment kenv, final String factClass, BeanPropertyFilter... expectedProperties) {
        assertTrue(factFinder.findFacts(
                kenv, 
                factClass, 
                expectedProperties).size() == 0);
    }

}
