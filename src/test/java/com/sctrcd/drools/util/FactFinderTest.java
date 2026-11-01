package com.sctrcd.drools.util;

import static org.junit.jupiter.api.Assertions.*;

import com.sctrcd.beans.BeanPropertyFilter;
import com.sctrcd.drools.util.testfacts.Customer;
import com.sctrcd.drools.util.testfacts.Product;
import java.util.Collection;
import org.junit.jupiter.api.Test;
import org.kie.api.io.ResourceType;

public class FactFinderTest {

    private final KnowledgeEnvironment kenv = new KnowledgeEnvironment(new DroolsResource[] {
        new DroolsResource(
                "sctrcd/drools/util/TrackingWorkingMemoryEventListenerTest.drl",
                ResourcePathType.CLASSPATH,
                ResourceType.DRL)
    });

    private final FactFinder finder = new FactFinder();

    @Test
    public void shouldFindFactsBasedOnSimpleName() {
        kenv.insert(new Product("Book", 5), new Product("Pen", 3), new Customer("Jimbo"));

        assertEquals(2, finder.findFacts(kenv, "Product").size());
        assertEquals(1, finder.findFacts(kenv, "Customer").size());
        assertTrue(finder.findFacts(kenv, "Order").isEmpty());
    }

    @Test
    public void shouldFindOnlyFactsWithMatchingProperties() {
        kenv.insert(new Product("Book", 5), new Product("Pen", 3));

        Collection<Object> pens = finder.findFacts(kenv, "Product", new BeanPropertyFilter("name", "Pen"));
        assertEquals(1, pens.size());
        assertEquals("Pen", ((Product) pens.iterator().next()).getName());

        assertTrue(finder.findFacts(kenv, "Product", new BeanPropertyFilter("name", "Ink"))
                .isEmpty());
    }

    @Test
    public void shouldSeeFactsAsTheRulesLeftThem() {
        kenv.insert(new Product("Book", 20), new Customer("Jimbo"));
        kenv.fireAllRules();

        // "Decrement count" takes the count down to 10; "Greet customer" greets.
        assertEquals(
                1,
                finder.findFacts(kenv, "Product", new BeanPropertyFilter("count", 10))
                        .size());
        assertEquals(
                1,
                finder.findFacts(kenv, "Customer", new BeanPropertyFilter("greeted", true))
                        .size());
        DroolsTestUtil.assertRuleFired(kenv, "Decrement count", "Greet customer");
        DroolsTestUtil.assertFactIsInWorkingMemory(kenv, "Customer", new BeanPropertyFilter("name", "Jimbo"));
        DroolsTestUtil.assertFactNotInWorkingMemory(kenv, "Customer", new BeanPropertyFilter("greeted", false));
    }
}
