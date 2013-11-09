package com.sctrcd.payments.validation.iban;

public class IbanFormatter {

    /**
	 * For readability, an IBAN tends to be displayed in uppercase with a space
	 * after every fourth character.
	 */
    public static String printFormat(String iban) {
        String eFormat = IbanUtil.sanitize(iban);
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < eFormat.length(); i++) {
            if (i % 4 == 0 && i != 0) {
                sb.append(" ");
            }
            sb.append(eFormat.charAt(i));
        }
        return sb.toString();
    }
	
}
