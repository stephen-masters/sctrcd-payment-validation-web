package com.sctrcd.payments.validation.iban;

public class IbanUtil {

    /**
	 * People have a tendency to provide IBANs as groups of 4 chars with spaces
	 * in between and sometimes with hyphens. This method strips all those
	 * invalid chars out.
	 * <p>
	 * It's worth noting that this does make the check a little bit lenient.
	 * However, from past experience, banking customers do seem to provide IBANs
	 * in a variety of formats: upper-case, lower-case, space-separated,
	 * hyphen-separated, etc. So it's best to be a bit lenient, and to correct
	 * the IBAN for them. However if the IBAN does require adjusting, you should
	 * warn the user.
	 * </p>
	 * <p>
	 * There are other checks and the MOD-97 checksum to be validated, so it's
	 * unlikely that this will be correcting some complete nonsense into a valid
	 * IBAN.
	 * </p>
	 */
    public static String sanitize(String iban) {
    	return iban.toUpperCase().replaceAll("[^A-Z0-9]", "");
    }
	
}
