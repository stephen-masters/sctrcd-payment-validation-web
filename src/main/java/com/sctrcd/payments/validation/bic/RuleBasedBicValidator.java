package com.sctrcd.payments.validation.bic;

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
import org.springframework.stereotype.Service;

import com.sctrcd.payments.enums.CountryEnum;
import com.sctrcd.payments.facts.BicValidationRequest;
import com.sctrcd.payments.facts.Country;
import com.sctrcd.payments.facts.PaymentValidationAnnotation;
import com.sctrcd.regulator.DroolsResource;
import com.sctrcd.regulator.DroolsUtil;
import com.sctrcd.regulator.ResourcePathType;
import com.sctrcd.regulator.listeners.TrackingAgendaEventListener;
import com.sctrcd.regulator.listeners.TrackingWorkingMemoryEventListener;

/**
 * 
 */
@Service("ruleBasedBicValidator")
public class RuleBasedBicValidator implements BicValidator {

    private KnowledgeBase kbase;
    
    public final List<Country> countries = new ArrayList<Country>();
    
    public RuleBasedBicValidator() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[]{
                        new DroolsResource("rules/payments/validation/Validation.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL),
                        new DroolsResource("rules/payments/validation/BicRules.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL)
                }, 
                EventProcessingOption.CLOUD);
        for (CountryEnum c : CountryEnum.values()) {
            countries.add(new Country(c.isoCode, c.name));
        }
    }
        
	@Override
	public BicValidationResult validate(String bic) {
	    StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
	    
	    ksession.setGlobal("countryList", countries);
	    
	    TrackingAgendaEventListener agendaEventListener = 
	            new TrackingAgendaEventListener();
	    TrackingWorkingMemoryEventListener workingMemoryEventListener = 
	            new TrackingWorkingMemoryEventListener();
	    ksession.addEventListener(agendaEventListener);
	    ksession.addEventListener(workingMemoryEventListener);
	    
	    List<Command> cmds = new ArrayList<Command>();
	    cmds.add(CommandFactory.newInsert(new BicValidationRequest(bic), "request"));
	    
	    ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));
		
		BicValidationResult result = new BicValidationResult();
		result.setBic(bic);

		QueryResults queryResults = ( QueryResults ) results.getValue( "annotations" );
        List<PaymentValidationAnnotation> annotations = new ArrayList<>();
        for (QueryResultsRow row : queryResults) {
            annotations.add((PaymentValidationAnnotation) row.get("annotation"));
        }
        result.addAnnotations(annotations);
		
		ksession.removeEventListener(agendaEventListener);
		ksession.removeEventListener(workingMemoryEventListener);
		
		return result;
	}
	
}
