package com.sctrcd.payments.validation;

import static org.junit.Assert.*;

import org.junit.Test;



public class IbanUtilTest {

	@Test
	public void shouldSanitizeIban() {
		assertEquals("ANIBAN12340", IbanUtil.sanitize("an iban12340"));
		assertEquals("ANIBAN12340", IbanUtil.sanitize("an-ib-an-12340"));
		assertEquals("ANIBAN12340", IbanUtil.sanitize("an??-ib@-%a*n1^234(0!"));
	}

}
