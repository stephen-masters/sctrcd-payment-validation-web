package com.sctrcd.payments.validation.bic;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.command.Command;
import org.drools.command.CommandFactory;
import org.drools.conf.EventProcessingOption;
import org.drools.runtime.ExecutionResults;
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
import com.sctrcd.payments.facts.PaymentValidationAnnotation;

/**
 * A Business Identifier Code (BIC), also known as a BIC or SWIFT-BIC, is a
 * format defined by ISO 9362:2009.
 * 
 * Ref: http://en.wikipedia.org/wiki/ISO_9362
 * 
 * The code is 8 or 11 characters long, made up of:
 * 
 * <pre>
 *     4 letters: The Institution code or Bank code.
 *                i.e. DEUT is Deutsche Bank.
 *     2 letters: The ISO 3166-1 country code.
 *     2 letters or digits: Location code.
 *         Conventions for 2nd character:
 *             0 - Typically a test BIC.
 *             1 - A passive participant in the SWIFT network.
 *             2 - Typically a reverse billing BIC where the recipient pays for the message.
 *     3 letters or digits: Branch code. Optional "XXX" for primary office.
 * </pre>
 * 
 * Where an 8-digit code is given, it may be assumed that it refers to the
 * primary office.
 */
public class BicValidationRulesTest {

    private KnowledgeBase kbase;
    StatelessKnowledgeSession ksession;
    
    public final List<Country> countries = new ArrayList<Country>();
    
    public BicValidationRulesTest() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[]{ 
                        new DroolsResource("rules/payments/validation/BicRules.drl", 
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
    public void shouldNotAnnotateValidBic() {
        bicShouldValidateAsExpected("HLFXESMM");
        bicShouldValidateAsExpected("HLFXESMM123");
    }
    
    @Test
    public void shouldValidateStructure() {
        bicShouldValidateAsExpected("HLFXESM", "BIC doesn't follow ISO 9362 structure.");
        bicShouldValidateAsExpected("1234ESMM123", "BIC doesn't follow ISO 9362 structure.");
    }
    
    @Test
    public void shouldValidateCountry() {
        bicShouldValidateAsExpected("HLFXEXMM", "BIC doesn't contain a valid country ISO code.");
    }
    
    public class Cheese {
        String name;
        public Cheese(String name) {this.name = name;}
    }
    
    public void bicShouldValidateAsExpected(String bic, String... expectedRules) {
        
        TrackingAgendaEventListener agendaEventListener = new TrackingAgendaEventListener();
        TrackingWorkingMemoryEventListener workingMemoryEventListener = new TrackingWorkingMemoryEventListener();
        ksession.addEventListener(agendaEventListener);
        ksession.addEventListener(workingMemoryEventListener);
        
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(CommandFactory.newInsert(new BicValidationRequest(bic), "request"));
        cmds.add(CommandFactory.newQuery("annotations", "annotations"));
        
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));

        QueryResults queryResults = ( QueryResults ) results.getValue( "annotations" );
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

}
