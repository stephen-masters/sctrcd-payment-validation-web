package com.sctrcd.drools.util;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

import com.sctrcd.drools.util.testfacts.Customer;
import com.sctrcd.drools.util.testfacts.Product;
import java.util.Map;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.kie.api.event.rule.ObjectInsertedEvent;
import org.kie.api.io.ResourceType;
import org.kie.api.runtime.KieRuntime;
import org.kie.api.runtime.rule.FactHandle;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * A very simple test to confirm that the
 * {@link TrackingWorkingMemoryEventListener} is responding to insertions and
 * retractions.
 *
 * @author Stephen Masters
 */
public class TrackingWorkingMemoryEventListenerTest {

    private static Logger log = LoggerFactory.getLogger(TrackingWorkingMemoryEventListenerTest.class);

    private KnowledgeEnvironment kenv = new KnowledgeEnvironment(new DroolsResource[] {
        new DroolsResource(
                "sctrcd/drools/util/TrackingWorkingMemoryEventListenerTest.drl",
                ResourcePathType.CLASSPATH,
                ResourceType.DRL)
    });

    @Mock
    private ObjectInsertedEvent objectInsertedEvent;

    @Mock
    private KieRuntime knowledgeRuntime;

    private AutoCloseable mocks;

    @BeforeEach
    public void setUp() {
        mocks = MockitoAnnotations.openMocks(this);

        when(this.objectInsertedEvent.getKieRuntime()).thenReturn(this.knowledgeRuntime);
        when(this.objectInsertedEvent.getObject()).thenReturn("Mock object.");
    }

    @AfterEach
    public void closeMocks() throws Exception {
        mocks.close();
    }

    @Test
    public void shouldTrackEvents() {
        TrackingWorkingMemoryEventListener listener = new TrackingWorkingMemoryEventListener();

        int insertionCountBeforeInsertion = listener.getInsertions().size();
        int retractionCountBeforeInsertion = listener.getRetractions().size();
        int updateCountBeforeInsertion = listener.getUpdates().size();

        listener.objectInserted(objectInsertedEvent);

        int insertionCountAfterInsertion = listener.getInsertions().size();
        int retractionCountAfterInsertion = listener.getRetractions().size();
        int updateCountAfterInsertion = listener.getUpdates().size();

        assertEquals(insertionCountBeforeInsertion + 1, insertionCountAfterInsertion);
        assertEquals(retractionCountBeforeInsertion, retractionCountAfterInsertion);
        assertEquals(updateCountBeforeInsertion, updateCountAfterInsertion);
    }

    @Test
    public void shouldTrackFilteredUpdates() {

        TrackingAgendaEventListener agendaListener = new TrackingAgendaEventListener();
        TrackingWorkingMemoryEventListener workingMemoryListener = new TrackingWorkingMemoryEventListener();

        kenv.addEventListener(agendaListener);
        kenv.addEventListener(workingMemoryListener);

        FactHandle productHandle = kenv.insert(new Product("Book", 20));
        FactHandle customerHandle = kenv.insert(new Customer("Jimbo"));

        TrackingWorkingMemoryEventListener productListener = new TrackingWorkingMemoryEventListener(productHandle);
        kenv.addEventListener(productListener);
        TrackingWorkingMemoryEventListener customerListener = new TrackingWorkingMemoryEventListener(customerHandle);
        kenv.addEventListener(customerListener);

        kenv.fireAllRules();

        assertEquals(
                10,
                productListener.getUpdates().size(),
                "There should have been 10 updates, as the count was increments from 20 to 10.");
        assertEquals(1, customerListener.getUpdates().size(), "There should only be one customer update.");

        // Print the product updates...
        StringBuilder sb = new StringBuilder("The following changes to product were tracked:\n");
        for (Map<String, Object> objectProperties : productListener.getFactChanges()) {
            for (String k : objectProperties.keySet()) {
                sb.append(k + "=\"" + objectProperties.get(k) + "\" ");
            }
            sb.append("\n");
        }
        log.info(sb.toString());
    }

    @Test
    public void shouldTrackClassFilteredUpdates() {
        TrackingWorkingMemoryEventListener everythingListener = new TrackingWorkingMemoryEventListener();
        TrackingWorkingMemoryEventListener productListener = new TrackingWorkingMemoryEventListener(Product.class);
        kenv.addEventListener(everythingListener);
        kenv.addEventListener(productListener);

        kenv.insert(new Product("Book", 20));
        kenv.insert(new Customer("Jimbo"));

        kenv.fireAllRules();

        assertEquals(11, everythingListener.getUpdates().size(), "There should be 11 updates in total.");
        assertEquals(
                10,
                productListener.getUpdates().size(),
                "There should have been 10 updates, as the count was increments from 20 to 10.");

        // Print the product updates...
        StringBuilder sb = new StringBuilder("The following changes to product were tracked:\n");
        for (Map<String, Object> objectProperties : productListener.getFactChanges()) {
            for (String k : objectProperties.keySet()) {
                sb.append(k + "=\"" + objectProperties.get(k) + "\" ");
            }
            sb.append("\n");
        }
        log.info(sb.toString());
    }
}
