package com.sctrcd.payments.validation.iban;

import static org.junit.jupiter.api.Assertions.*;

import org.junit.jupiter.api.Test;

public class IbanUtilTest {

    @Test
    public void shouldSanitizeIban() {

        assertEquals("ANIBAN12340", IbanUtil.sanitize("an iban12340"));
        assertEquals("ANIBAN12340", IbanUtil.sanitize("an-ib-an-12340"));
        assertEquals("ANIBAN12340", IbanUtil.sanitize("an??-ib@-%a*n1^234(0!"));
    }
}
