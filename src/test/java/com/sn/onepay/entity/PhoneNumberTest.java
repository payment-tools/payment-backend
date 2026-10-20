package com.sn.onepay.entity;

import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PhoneNumberTest {

    @ParameterizedTest
    @ValueSource(strings = {"771234567", " 77 123 45 67 ", "+221771234567", "+221 77 123 45 67"})
    void phoneNumber_isStoredWithoutPrefixNorSpaces(String phoneNumber) {
        Client client = new Client();
        Cashier cashier = new Cashier();
        EnterpriseProfile enterpriseProfile = new EnterpriseProfile();
        SalesProfile salesProfile = new SalesProfile();

        client.setPhoneNumber(phoneNumber);
        cashier.setPhoneNumber(phoneNumber);
        enterpriseProfile.setPhoneNumber(phoneNumber);
        salesProfile.setPhoneNumber(phoneNumber);

        assertEquals("771234567", client.getPhoneNumber());
        assertEquals("771234567", cashier.getPhoneNumber());
        assertEquals("771234567", enterpriseProfile.getPhoneNumber());
        assertEquals("771234567", salesProfile.getPhoneNumber());
    }
}
