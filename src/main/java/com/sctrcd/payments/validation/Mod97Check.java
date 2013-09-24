package com.sctrcd.payments.validation;

import java.math.BigInteger;

/**
 * This IBAN validator does no more than implement a MOD-97 check. The following
 * notes describe the MOD-97 check.
 * <p>
 * 1. The First 4 digits of the IBAN (Country Code and Check Digit) are moved to
 * the end of the IBAN.
 * </p>
 * <p>
 * i.e. KY12 0123 4567 8901 2345 gives 0123 4567 8901 2345 KY12
 * </p>
 * <p>
 * 2. The letters are converted to numbers per the table shown below.
 * </p>
 * 
 * <pre>
 * A 10  B 11  C 12  D 13  E 14  F 15  G 16  H 17  I 18  J 19  K 20
 * L 21  M 22  N 23  O 24  P 25  Q 26  R 27  S 28  T 29  U 30  V 31
 * W 32  X 33  Y 34  Z 35
 * </pre>
 * <p>
 * i.e. 0123 4567 8901 2345 KY12 gives 0123456789012345203412
 * </p>
 * <p>
 * 3. Divide the resulting number by 97. If the remainder is 1, then the IBAN is
 * valid.
 * </p>
 * 
 * @author Stephen Masters
 */
public class Mod97Check {
	
	/**
	 * Default constructor is private to prevent instantiation.
	 */
	private Mod97Check() {
	}

    /**
	 * Performs the ISO mod-97 check based on the assumption that the IBAN arg
	 * has extraneous characters removed and all letters are upper-case.
	 */
    public static boolean isValid(String iban) {
		if (iban == null)
			return false;
        // If the checksum divided by 97 leaves a remainder of 1,
        // the IBAN is valid.
        String cleanIban = IbanUtil.sanitize(iban);
    	// Shift first four characters to the end of the array.
    	String shuffledIban = shuffle(cleanIban);
    	// Convert to a numeric form.
    	BigInteger checkSum = numerize(shuffledIban);
    	System.out.println(checkSum);
        // If the checksum divided by 97 leaves a remainder of 1,
        // the IBAN is valid.
        return new BigInteger(checkSum.toString()).remainder(new BigInteger("97"))
                .equals(BigInteger.ONE);
    }

	/**
	 * Convert the IBAN to a numeric form by replacing each letter with a number
	 * as defined in the following table.
	 * <pre>
	 * A 10  B 11  C 12  D 13  E 14  F 15  G 16  H 17  I 18  J 19  K 20
	 * L 21  M 22  N 23  O 24  P 25  Q 26  R 27  S 28  T 29  U 30  V 31
	 * W 32  X 33  Y 34  Z 35
	 * </pre>
	 * i.e.
	 * <pre>
	 *   12345 => 12345
	 *   123C5 => 123125
	 * </pre>
	 */
    public static BigInteger numerize(String iban) {
        StringBuilder sb = new StringBuilder();
        for (char c : iban.toCharArray()) {
        	sb.append(Character.getNumericValue(c));
        }
        return new BigInteger(sb.toString());
    }
    
    /**
     * Shift the first 4 characters in the IBAN to the end. i.e.
     * <pre>
     *   KY12 0123 4567 8901 2345 => 0123 4567 8901 2345 KY12
     * </pre>
     */
    public static String shuffle(String iban) {
    	String shuffledIban = (iban + iban.substring(0, 4));
    	shuffledIban = shuffledIban.substring(4, shuffledIban.length());
    	return shuffledIban;
    }

}