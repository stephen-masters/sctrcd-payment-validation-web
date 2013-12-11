package com.sctrcd.payments.validation.iban;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.conf.EventProcessingOption;
import org.drools.definition.rule.Query;
import org.drools.runtime.ExecutionResults;
import org.drools.runtime.StatefulKnowledgeSession;
import org.drools.runtime.StatelessKnowledgeSession;
import org.drools.runtime.rule.QueryResults;
import org.drools.runtime.rule.QueryResultsRow;
import org.junit.Before;
import org.junit.Test;

import com.sctrcd.drools.util.DroolsResource;
import com.sctrcd.drools.util.DroolsUtil;
import com.sctrcd.drools.util.ResourcePathType;
import com.sctrcd.drools.util.TrackingAgendaEventListener;
import com.sctrcd.drools.util.TrackingWorkingMemoryEventListener;
import com.sctrcd.payments.enums.CountryEnum;
import com.sctrcd.payments.facts.BicValidationRequest;
import com.sctrcd.payments.facts.Country;
import com.sctrcd.payments.facts.IbanValidationRequest;
import com.sctrcd.payments.facts.PaymentValidationAnnotation;
import com.sctrcd.payments.validation.iban.IbanValidationResult;

public class IbanValidationRulesTest {

    private KnowledgeBase kbase;
    StatelessKnowledgeSession ksession;
    StatefulKnowledgeSession statefulSession;
    
    public final List<Country> countries = new ArrayList<Country>();
    
    public IbanValidationRulesTest() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[]{ 
                        new DroolsResource("rules/payments/validation/Validation.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL),
                        new DroolsResource("rules/payments/validation/IbanRules.drl", 
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
        shouldValidateWithStatefulSession("ES050 217009945", "IBAN failed the Mod-97 checksum test.");
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
    
    void ibanShouldValidateAsExpected(String iban, String... expectedRules) {
        
        TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
        TrackingWorkingMemoryEventListener workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
        ksession.addEventListener(agendaEventListener);
        ksession.addEventListener(workingMemoryEventListener);
        
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(CommandFactory.newInsert(new IbanValidationRequest(iban), "request"));
        cmds.add(CommandFactory.newQuery("annotations", "annotations"));

        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));
        
        QueryResults queryResults = ( QueryResults ) results.getValue( "annotations" );
        System.out.println("Found [" + queryResults.size() + "]");
        
        List<PaymentValidationAnnotation> annotations = new ArrayList<>();
        for (QueryResultsRow row : queryResults) {
            annotations.add((PaymentValidationAnnotation) row.get("annotation"));
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
    
    void shouldValidateWithStatefulSession(String iban, String... expectedRules) {
        statefulSession = kbase.newStatefulKnowledgeSession();
        
        TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
        TrackingWorkingMemoryEventListener workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
        
        statefulSession.addEventListener(agendaEventListener);
        statefulSession.addEventListener(workingMemoryEventListener);
        
        statefulSession.setGlobal("countryList", countries);
        
        statefulSession.insert(new IbanValidationRequest(iban));
        
        statefulSession.fireAllRules();
        
        QueryResults queryResults = statefulSession.getQueryResults("annotations");
        
        System.out.println("Found [" + queryResults.size() + "]");
        
        List<PaymentValidationAnnotation> annotations = new ArrayList<>();
        for (QueryResultsRow row : queryResults) {
            annotations.add((PaymentValidationAnnotation) row.get("annotation"));
        }
        
        assertFalse("There should not be any annotations if there is no expected message.", 
                (expectedRules == null || expectedRules.length == 0) && annotations.size() > 0);
    
        System.out.println(agendaEventListener.activationsToString());
        
        for (String ruleName : expectedRules) {
            assertTrue("Rule [" + ruleName + "] should have fired.", agendaEventListener.isRuleFired(ruleName));
        }
        
        statefulSession.removeEventListener(agendaEventListener);
        statefulSession.removeEventListener(workingMemoryEventListener);
        statefulSession.dispose();
    }

}
