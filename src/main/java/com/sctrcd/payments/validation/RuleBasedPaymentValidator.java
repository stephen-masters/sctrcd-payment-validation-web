package com.sctrcd.payments.validation;

import java.util.ArrayList;
import java.util.List;

import org.drools.KnowledgeBase;
import org.drools.builder.ResourceType;
import org.drools.conf.EventProcessingOption;
import org.drools.runtime.StatelessKnowledgeSession;
import org.springframework.stereotype.Service;

import com.sctrcd.drools.util.DroolsResource;
import com.sctrcd.drools.util.DroolsUtil;
import com.sctrcd.drools.util.ResourcePathType;
import com.sctrcd.drools.util.TrackingAgendaEventListener;
import com.sctrcd.drools.util.TrackingWorkingMemoryEventListener;
import com.sctrcd.payments.enums.CountryEnum;
import com.sctrcd.payments.facts.Country;
import com.sctrcd.payments.facts.Payment;
import com.sctrcd.payments.facts.PaymentValidationRequest;

/**
 * 
 */
@Service("ruleBasedPaymentValidator")
public class RuleBasedPaymentValidator implements PaymentValidator {

    private KnowledgeBase kbase;
    
    public final List<Country> countries = new ArrayList<Country>();
    
    public RuleBasedPaymentValidator() {
        this.kbase = DroolsUtil.createKnowledgeBase(
                new DroolsResource[]{ 
                        new DroolsResource("rules/payments/validation/iban/IbanRules.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL),
                        new DroolsResource("rules/payments/validation/iban/PaymentRules.drl", 
                                ResourcePathType.CLASSPATH, 
                                ResourceType.DRL)
                }, 
                EventProcessingOption.CLOUD);
        for (CountryEnum c : CountryEnum.values()) {
            countries.add(new Country(c.isoCode, c.name));
        }
    }
    
	@Override
	public FxPaymentValidationResult validatePayment(Payment payment) {
	    StatelessKnowledgeSession ksession = kbase.newStatelessKnowledgeSession();
	    
	    ksession.setGlobal("countryList", countries);
	    
	    TrackingAgendaEventListener agendaEventListener = 
	            new TrackingAgendaEventListener();
	    TrackingWorkingMemoryEventListener workingMemoryEventListener = 
	            new TrackingWorkingMemoryEventListener();
	    ksession.addEventListener(agendaEventListener);
	    ksession.addEventListener(workingMemoryEventListener);
	    
	    PaymentValidationRequest request = new PaymentValidationRequest(payment);
	    request.setPayment(payment);
	    
	    List<Object> facts = new ArrayList<Object>();
	    facts.add(request);
	    
		ksession.execute(facts);
		
		FxPaymentValidationResult result = new FxPaymentValidationResult();
		result.addAnnotations(request.getAnnotations());
		
		ksession.removeEventListener(agendaEventListener);
		ksession.removeEventListener(workingMemoryEventListener);
		
		return result;
	}
	
}
