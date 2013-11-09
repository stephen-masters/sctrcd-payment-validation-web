package com.sctrcd.payments.validation.iban;

import org.junit.Test;

import com.sctrcd.payments.validation.ValidationResult;
import com.sctrcd.payments.validation.iban.IbanMod97Check;
import com.sctrcd.payments.validation.iban.IbanValidator;
import com.sctrcd.payments.validation.iban.SimpleIbanValidator;

import static org.junit.Assert.*;

/**
 * Unit tests for the {@link FxPaymentValidator}.
 * 
 * @author Stephen Masters
 */
public class SimpleIbanValidatorTest {

	public static final String[] validIbans = {
       	"BG29FINV915010EUR0IKFF",
        "ES5702170302862100282783",
        "SK3211000000002612890189",
        "ES23 0217 0099 47",
        "LU36 0029 1524 6005 0000",
        "AD12 0001 2030 2003 5910 0100",
        "AT61 1904 3002 3457 3201",
        "BE68 5390 0754 7034",
        "HR12 1001 0051 8630 0016 0",
        "CY17 0020 0128 0000 0012 0052 7600",
        "CZ65 0800 0000 1920 0014 5399",
        "DK50 0040 0440 1162 43",
        "EE38 2200 2210 2014 5685",
        "FI21 1234 5600 0007 85",
        "FR14 2004 1010 0505 0001 3M02 606",
        "DE89 3704 0044 0532 0130 00",
        "GI75 NWBK 0000 0000 7099 453",
        "GR16 0110 1250 0000 0001 2300 695",
        "HU42 1177 3016 1111 1018 0000 0000",
        "IS14 0159 2600 7654 5510 7303 39",
        "IE29 AIBK 9311 5212 3456 78",
        "IT60 X054 2811 1010 0000 0123 456",
        "LV80 BANK 0000 4351 9500 1",
        "LT12 1000 0111 0100 1000",
        "LI21 0881 0000 2324 013A A",
        "LU28 0019 4006 4475 0000",
        "MK07 300 0000000424 25",
        "MT84 MALT 0110 0001 2345 MTLC AST001S",
        "NL91 ABNA 0417 1643 00",
        "NO93 8601 1117 947",
        "PL27 1140 2004 0000 3002 0135 5387",
        "PT50 0002 0123 1234 5678 9015 4",
        "RO49 AAAA 1B31 0075 9384 0000",
        "SK31 1200 0000 1987 4263 7541",
        "SI56 1910 0000 0123 438",
        "ES91 2100 0418 4502 0005 1332",
        "SE35 5000 0000 0549 1000 0003",
        "CH93 0076 2011 6238 5295 7",
        "GB29 NWBK 6016 1331 9268 19",
        "TN59 1420 7207 1007 0712 9648"
	};
	
	public static final String[] invalidIbans = {
        "IT72C012574030100000000789",
        "ES050 217009945",
        "ES150 217002616",
        "ES350217009941",
        "ES95 0217 0100 17",
        "ES950217002613",
        "es100217009945",
        "es190217009941",
        "LU36 0029 1534 6005 0000",
        "TN59 1421 7207 1007 0712 9648"
	};
	
	IbanValidator validator = new SimpleIbanValidator();

    @Test
    public final void shouldAcceptValidIbans() {
        for (String iban : SimpleIbanValidatorTest.validIbans) {
            assertTrue(IbanMod97Check.isValid(iban));
            ValidationResult result = validator.validateIban(iban);
            assertTrue(result.isValid());
        }
    }
    
    @Test
    public final void shouldRejectInvalidIbans() {
        for (String iban : SimpleIbanValidatorTest.invalidIbans) {
            assertFalse(IbanMod97Check.isValid(iban));
            ValidationResult result = validator.validateIban(iban);
            assertFalse(result.isValid());
        }
    }
}
