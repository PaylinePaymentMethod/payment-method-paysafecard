package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;

public class Customer {
    private String id;
    @SerializedName("min_age")
    private String minAge;
    @SerializedName("kyc_level")
    private String kycLevel;
    @SerializedName("country_restriction")
    private String countryRestriction;

    // field used for refund
    private String email;

    public Customer(String id, String minAge, String kycLevel, String countryRestriction) {
        this.id = id;
        this.minAge = minAge;
        // set to null if kyc_level is "SIMPLE"
        this.kycLevel = PaySafeCardConstants.KYCLEVEL_SIMPLE.equals(kycLevel) ? null : kycLevel;
        this.countryRestriction = countryRestriction;
    }

    public Customer(String id, String email) {
        this.id = id;
        this.email = email;
    }
}