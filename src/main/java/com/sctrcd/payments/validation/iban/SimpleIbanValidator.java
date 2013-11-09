package com.sctrcd.payments.validation.iban;

import java.util.HashMap;
import java.util.Map;

import org.springframework.stereotype.Service;

import com.sctrcd.payments.enums.CountryEnum;
import com.sctrcd.payments.facts.PaymentValidationAnnotation;
import com.sctrcd.payments.facts.AnnotationLevel;

/**
 * This IBAN validator does no more than check that the country code is for a
 * valid country and that the IBAN passes a MOD-97 check.
 * 
 * @see{Mod97IbanValidator for details of the MOD-97 check.
 * 
 * @author Stephen Masters
 */
@Service("simpleIbanValidator")
public class SimpleIbanValidator implements IbanValidator {

    /**
     * A map containing the countries permitted.
     */
    private final Map<String, CountryEnum> countryMap;

    /**
     * Default constructor sets up the permitted countries based on all those
     * defined in the {@link CountryEnum} enum.
     */
    public SimpleIbanValidator() {
        countryMap = new HashMap<String, CountryEnum>();
        for (CountryEnum c : CountryEnum.values()) {
            countryMap.put(c.isoCode, c);
        }
    }

    /**
     * Validate an IBAN provided as a <code>String</code>.
     * Check that the country code is for a valid country and that the IBAN
     * passes a MOD-97 check.
     * 
     * @see{Mod97IbanValidator for details of the MOD-97 check.
     */
    public IbanValidationResult validateIban(String iban) {
        IbanValidationResult result = new IbanValidationResult(iban, true);
        
        if (iban == null) {
            result.addAnnotation(new PaymentValidationAnnotation("The IBAN  was not defined.", AnnotationLevel.REJECT, "The IBAN  was not defined."));
        }
        if (countryMap.get(iban.substring(0, 2)) == null) {
            // It's not a known country.
            result.addAnnotation(new PaymentValidationAnnotation("The country code on the IBAN is not valid.", AnnotationLevel.REJECT, "The country code on the IBAN is not valid."));
        }
        // If the checksum divided by 97 leaves a remainder of 1,
        // the IBAN is valid.
        if (!IbanMod97Check.isValid(IbanUtil.sanitize(iban))) {
            result.addAnnotation(new PaymentValidationAnnotation("Failed Mod-97 check", AnnotationLevel.REJECT, "The IBAN is not valid."));
        }
        
        return result;
    }

}