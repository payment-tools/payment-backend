package com.sn.onepay.utils;

/*Numéros de téléphone des comptes : format sénégalais, 9 chiffres commençant par 7.
  Indicatif +221 et espaces acceptés en entrée, enregistré sous la forme 771234567*/
public final class PhoneNumbers {

    public static final String SENEGAL_PATTERN = "^\\s*(\\+221\\s*)?7(\\s*\\d){8}\\s*$";
    public static final String FORMAT_MESSAGE = "Numéro de téléphone sénégalais attendu : 9 chiffres commençant par 7 (ex. 771234567 ou +221 77 123 45 67)";

    private PhoneNumbers() {
    }

    public static String normalize(String phoneNumber) {
        if (phoneNumber == null) {
            return null;
        }
        String compact = phoneNumber.replaceAll("\\s", "");
        return compact.startsWith("+221") ? compact.substring(4) : compact;
    }
}
