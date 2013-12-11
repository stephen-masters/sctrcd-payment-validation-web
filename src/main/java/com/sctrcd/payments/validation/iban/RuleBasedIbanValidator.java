package com.sctrcd.payments.validation.iban;

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
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import com.sctrcd.drools.util.DroolsResource;
import com.sctrcd.drools.util.DroolsUtil;
import com.sctrcd.drools.util.ResourcePathType;
import com.sctrcd.drools.util.TrackingAgendaEventListener;
import com.sctrcd.drools.util.TrackingWorkingMemoryEventListener;
import com.sctrcd.payments.enums.CountryEnum;
import com.sctrcd.payments.facts.Country;
import com.sctrcd.payments.facts.IbanValidationRequest;
import com.sctrcd.payments.facts.PaymentValidationAnnotation;

/**
 * 
 * @author Stephen Masters
 */
@Service("ruleBasedIbanValidator")
public class RuleBasedIbanValidator implements IbanValidator {

    private static Logger log = LoggerFactory.getLogger(RuleBasedIbanValidator.class);
    
    private KnowledgeBase kbase;
    
    public final List<Country> countries = new ArrayList<Country>();
    
    public RuleBasedIbanValidator() {
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
        
	@Override
	public IbanValidationResult validateIban(String iban) {
	    log.debug("Validating IBAN : " + iban + "\n");
	    
	    StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
	    
	    ksession.setGlobal("countryList", countries);
	    
	    TrackingAgendaEventListener agendaEventListener = 
	            new TrackingAgendaEventListener();
	    TrackingWorkingMemoryEventListener workingMemoryEventListener = 
	            new TrackingWorkingMemoryEventListener();
	    ksession.addEventListener(agendaEventListener);
	    ksession.addEventListener(workingMemoryEventListener);
	    
        List<Command> cmds = new ArrayList<Command>();
        cmds.add(CommandFactory.newInsert(new IbanValidationRequest(iban), "request"));
        cmds.add(CommandFactory.newQuery("annotations", "annotations"));
        cmds.add(CommandFactory.newQuery("rejected", "rejected"));
        
        ExecutionResults results = ksession.execute(CommandFactory.newBatchExecution(cmds));
	    
	    IbanValidationResult result = new IbanValidationResult();
		result.setIban(iban);
		
		QueryResults rejected = ( QueryResults ) results.getValue( "rejected" );
		result.setValid(rejected.size() > 0);
		
		QueryResults queryResults = ( QueryResults ) results.getValue( "annotations" );
		List<PaymentValidationAnnotation> annotations = new ArrayList<>();
        for (QueryResultsRow row : queryResults) {
            annotations.add((PaymentValidationAnnotation) row.get("annotation"));
        }
		result.addAnnotations(annotations);
		
		ksession.removeEventListener(agendaEventListener);
		ksession.removeEventListener(workingMemoryEventListener);
		
		log.debug("Validation complete for IBAN : " + iban + "\n");
		
		return result;
	}
	
}