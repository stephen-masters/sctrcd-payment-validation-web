package com.sctrcd.payments.validation;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.runtime.StatelessKnowledgeSession;
import org.junit.Before;
import org.junit.Test;

import com.sctrcd.drools.util.DroolsResource;
import com.sctrcd.drools.util.DroolsUtil;
import com.sctrcd.drools.util.ResourcePathType;
import com.sctrcd.drools.util.TrackingAgendaEventListener;
import com.sctrcd.drools.util.TrackingWorkingMemoryEventListener;
import com.sctrcd.payments.enums.CountryEnum;
import com.sctrcd.payments.facts.Country;
import com.sctrcd.payments.facts.IbanValidationRequest;
import com.sctrcd.payments.facts.ValidationAnnotation;

public class IbanValidationRulesTest {

    private KnowledgeBase kbase;
    StatelessKnowledgeSession ksession;
    
    public final List<Country> countries = new ArrayList<Country>();
    
    public IbanValidationRulesTest() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[]{ 
                        new DroolsResource("rules/payments/validation/iban/IbanRules.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL)
                }, 
                EventProcessingOption.CLOUD);
        for (CountryEnum c : CountryEnum.values()) {
            countries.add(new Country(c.isoCode, c.name));
        }
    }
    
    @Before
    public void setup() {
        ksession = kbase.newStatelessKnowledgeSession();
        ksession.setGlobal("countryList", countries);
    }
    
    @Test
    public void shouldNotAnnotateValidIban() {
        ibanShouldValidateAsExpected("ES5702170302862100282783");
    }

    @Test
    public void shouldRejectForInvalidMod97() {
        ibanShouldValidateAsExpected("ES050 217009945", "IBAN failed the Mod-97 checksum test.");
    }
    
    @Test
    public void shouldValidateUkStructure() {
        // First a valid UK IBAN.
        ibanShouldValidateAsExpected("GB29NWBK60161331926819");
        // If inserted with spaces, we should be able to deal with that.
        ibanShouldValidateAsExpected("GB29 NWBK 6016 1331 9268 19");
        // Check that Mod-97 works correctly for this IBAN.
        ibanShouldValidateAsExpected("GB29 NWBK 6016 1331 9268 20", "IBAN failed the Mod-97 checksum test.");
        // Passes the Mod-97 check, but replaced letter in bank code with
        // number, so that it is GB and does not have correct BBAN structure.
        ibanShouldValidateAsExpected("GB29 NWB0 6016 1331 9268 19", "IBAN is for UK, but doesn't have BBAN structure.");
    }
    
    public void ibanShouldValidateAsExpected(String iban, String... expectedRules) {
        
        TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
        TrackingWorkingMemoryEventListener workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
        ksession.addEventListener(agendaEventListener);
        ksession.addEventListener(workingMemoryEventListener);
        
        IbanValidationRequest request = new IbanValidationRequest(iban);
        
        List<Object> facts = new ArrayList<Object>();
        facts.add(request);
        
        ksession.execute(facts);
        
        IbanValidationResult result = new IbanValidationResult();
        result.addAnnotations(request.getAnnotations());
        
        List<ValidationAnnotation> annotations = result.getAnnotations();        
        for (ValidationAnnotation annotation : annotations) {
            System.out.println(annotation.toString());
        }
        
        assertFalse("There should not be any annotations if there is no expected message.", 
                    (expectedRules == null || expectedRules.length == 0) && annotations.size() > 0);
        
        System.out.println(agendaEventListener.activationsToString());
        
        for (String ruleName : expectedRules) {
            assertTrue("Rule [" + ruleName + "] should have fired.", agendaEventListener.isRuleFired(ruleName));
        }
        
        ksession.removeEventListener(agendaEventListener);
        ksession.removeEventListener(workingMemoryEventListener);
    }

}
