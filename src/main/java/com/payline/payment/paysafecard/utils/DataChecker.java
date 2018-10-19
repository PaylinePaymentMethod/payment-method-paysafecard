package com.payline.payment.paysafecard.utils;


import java.util.Arrays;
import java.util.Locale;

public class DataChecker {

    /**
     * verify minAge is a number between 1 and 99
     * @param minAge
     * @throws BadFieldException
     */
    public static void verifyMinAge(String minAge) throws BadFieldException {
        if (!isEmpty(minAge)) {
            if (!isNumeric(minAge)) {
                throw new BadFieldException(PaySafeCardConstants.MINAGE_KEY, "contract.errors.minAgeNotNumeric");
            }
            else if (Integer.parseInt(minAge) < 1 || Integer.parseInt(minAge) > 99) {
                throw new BadFieldException(PaySafeCardConstants.MINAGE_KEY, "contract.errors.minAgeWrongRange");
            }
        }
    }

    /**
     * verify country restriction is ISO-3166 alpha-2
     * @param countryRestriction
     * @throws BadFieldException
     */
    public static void verifyCountryRestriction(String countryRestriction) throws BadFieldException {
        if (!isEmpty(countryRestriction )) {
            countryRestriction = countryRestriction.toUpperCase();
            if (!isISO3166(countryRestriction)) {
                throw new BadFieldException(PaySafeCardConstants.COUNTRYRESTRICTION_KEY , "contract.errors.countryNotISO");
            }
        }
    }

    /**
     * check if a String respect ISO-3166 rules
     * @param countryCode the code to compare
     * @return true if countryCode is in ISO-3166 list, else return false
     */
    public static boolean isISO3166(String countryCode) {
        return Arrays.asList(Locale.getISOCountries()).contains(countryCode);
    }

    /**
     * check if a string is a number
     * @param str
     * @return
     */
    public static boolean isNumeric(String str) {
        if (str == null) {
            return false;
        } else {
            int sz = str.length();

            for(int i = 0; i < sz; ++i) {
                if (!Character.isDigit(str.charAt(i))) {
                    return false;
                }
            }

            return true;
        }
    }

    /**
     * check if a String is null or empty
     * @param str
     * @return
     */
    public static boolean isEmpty(String str) {
        return str == null || str.length() == 0;
    }

}
