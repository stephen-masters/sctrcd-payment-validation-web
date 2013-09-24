package com.sctrcd.drools.util;

import static org.junit.Assert.*;

import java.util.Map;

import org.drools.builder.ResourceType;
import org.drools.event.rule.ObjectInsertedEvent;
import org.drools.runtime.KnowledgeRuntime;
import org.drools.runtime.rule.FactHandle;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.sctrcd.drools.util.DroolsResource;
import com.sctrcd.drools.util.KnowledgeEnvironment;
import com.sctrcd.drools.util.ResourcePathType;
import com.sctrcd.drools.util.TrackingAgendaEventListener;
import com.sctrcd.drools.util.TrackingWorkingMemoryEventListener;
import com.sctrcd.drools.util.testfacts.Customer;
import com.sctrcd.drools.util.testfacts.Product;

import static org.mockito.Mockito.*;
import static org.mockito.MockitoAnnotations.initMocks;


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
		new DroolsResource("sctrcd/drools/util/TrackingWorkingMemoryEventListenerTest.drl", 
                ResourcePathType.CLASSPATH, 
                ResourceType.DRL)
        });

    @Mock private ObjectInsertedEvent objectInsertedEvent;
    @Mock private KnowledgeRuntime knowledgeRuntime;

    @Before
    public void setUp() {
        initMocks(this);

        when(this.objectInsertedEvent.getKnowledgeRuntime()).thenReturn(this.knowledgeRuntime);
        when(this.objectInsertedEvent.getObject()).thenReturn(new String("Mock object."));
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

        assertEquals("There should have been 10 updates, as the count was increments from 20 to 10.", 10, productListener.getUpdates().size());
        assertEquals("There should only be one customer update.", 1, customerListener.getUpdates().size());

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

        assertEquals("There should be 11 updates in total.", 11, everythingListener.getUpdates().size());
        assertEquals("There should have been 10 updates, as the count was increments from 20 to 10.", 10, productListener.getUpdates().size());

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
