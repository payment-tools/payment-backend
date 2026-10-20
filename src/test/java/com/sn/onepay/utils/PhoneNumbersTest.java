package com.sn.onepay.utils;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class PhoneNumbersTest {

    @ParameterizedTest
    @ValueSource(strings = {"771234567", "701234567", "77 123 45 67", "+221771234567", "+221 77 123 45 67", " 771234567 "})
    void senegalPattern_acceptsSenegaleseNumbers(String phoneNumber) {
        assertTrue(phoneNumber.matches(PhoneNumbers.SENEGAL_PATTERN));
    }

    @ParameterizedTest
    @ValueSource(strings = {"", "   ", "12345", "671234567", "77123456", "7712345678", "+33771234567", "221771234567", "77-123-45-67", "string"})
    void senegalPattern_rejectsOtherValues(String phoneNumber) {
        assertFalse(phoneNumber.matches(PhoneNumbers.SENEGAL_PATTERN));
    }

    @Test
    void normalize_removesPrefixAndSpaces() {
        assertEquals("771234567", PhoneNumbers.normalize("+221 77 123 45 67"));
        assertEquals("771234567", PhoneNumbers.normalize("77 123 45 67"));
        assertEquals("771234567", PhoneNumbers.normalize("771234567"));
    }

    @Test
    void normalize_keepsNull() {
        assertNull(PhoneNumbers.normalize(null));
    }
}
