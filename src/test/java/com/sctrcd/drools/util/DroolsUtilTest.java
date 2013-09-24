package com.sctrcd.drools.util;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;
import static org.mockito.MockitoAnnotations.initMocks;

import java.util.ArrayList;
import java.util.List;

import org.drools.event.rule.ObjectInsertedEvent;
import org.junit.Before;
import org.junit.Test;
import org.mockito.Mock;

import com.sctrcd.drools.util.Activation;
import com.sctrcd.drools.util.DroolsUtil;


/**
 * 
 * @author Stephen Masters
 */
public class DroolsUtilTest {

    @Mock Activation activation_1;
    @Mock Activation activation_2;
    @Mock Activation activation_3;
    
    @Mock ObjectInsertedEvent insertion_1;
    @Mock ObjectInsertedEvent insertion_2;
    @Mock ObjectInsertedEvent insertion_3;
    
    List<Activation> activations;
    List<ObjectInsertedEvent> insertions;
    
    @Before
    public void setUp() {
        initMocks(this);
        
        when(this.activation_1.getRuleName()).thenReturn(new String("His Rule"));
        when(this.activation_2.getRuleName()).thenReturn(new String("121 Her Rule"));
        when(this.activation_3.getRuleName()).thenReturn(new String("54 My Rule"));
        
        activations = new ArrayList<Activation>();
        activations.add(this.activation_1);
        activations.add(this.activation_2);
        activations.add(this.activation_3);
        
        when(this.insertion_1.getObject()).thenReturn(new StubBean("happy", "larry"));
        when(this.insertion_2.getObject()).thenReturn(new StubBean("aunt", "sally"));
        when(this.insertion_3.getObject()).thenReturn(new StubBean("lame", "duck"));
        
        insertions = new ArrayList<ObjectInsertedEvent>();
        insertions.add(insertion_1);
        insertions.add(insertion_2);
        insertions.add(insertion_3);
    }
    
    /**
     * The rule name passed into the isRuleFired method should be matched
     * against the name of each rule in the list of activations. Decision 
     * table rules prefix the rule name with a number, so exact matches 
     * are not appropriate. So we check when the end of the rule name 
     * matches the argument.
     * <p>
     * Note that for additional leniency (rule writers are not expected to 
     * be ultra-strict with naming conventions) the match is case-insensitive. 
     * </p> 
     */
    @Test
    public void shouldIdentifyIfRuleFired() {
        assertFalse(DroolsUtil.ruleFired(activations, "Hello me"));
        assertTrue(DroolsUtil.ruleFired(activations, "His Rule"));
        assertTrue(DroolsUtil.ruleFired(activations, "Her Rule"));
        assertTrue(DroolsUtil.ruleFired(activations, "121 Her Rule"));
        assertTrue(DroolsUtil.ruleFired(activations, "121 Her rule"));
        assertFalse(DroolsUtil.ruleFired(activations, "54"));
    }
    
    @Test
    public void shouldFindFactWithSingleMatchingField() {
        assertNotNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=happy"}));
    }
    
    @Test
    public void shouldFindFactWithMultipleMatchingFields() {
        assertNotNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=lame", "bar=duck"}));
    }
    
    @Test
    public void shouldNotFindFactWithMatchingAndNonMatchingField() {
        assertNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=aunt", "bar=duck"}));
    }
    
    @Test
    public void shouldNotFindFactWithSingleNonMatchingField() {
        assertNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"foo=flippant"}));
    }

    @Test
    public void shouldNotFindFactWithNonExistantField() {
        assertNull(DroolsUtil.findInsertedFact(insertions, "StubBean", new String[] {"fake=larry"}));
    }

    public class StubBean {
        private String foo;
        private String bar;
        public StubBean(String foo, String bar) {
            this.foo = foo; this.bar = bar;
        }
        public String getFoo() { return foo; }
        public String getBar() { return bar; }
    }

}
