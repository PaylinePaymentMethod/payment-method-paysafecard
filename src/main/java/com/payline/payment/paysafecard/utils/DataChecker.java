package com.payline.payment.paysafecard.utils;

import org.apache.commons.lang.StringUtils;

import java.util.Arrays;
import java.util.Locale;

public class DataChecker {

    /**
     * verify minAge is a number between 1 and 99
     * @param minAge
     * @throws BadFieldException
     */
    public static void verifyMinAge(String minAge) throws BadFieldException {
        if (minAge != null) {
            if (!StringUtils.isNumeric(minAge)) {
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
        if (countryRestriction != null) {
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

}
