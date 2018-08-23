package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.SerializedName;

public class Customer {
    private String id;
    @SerializedName("min_age")
    private String minAge;
    @SerializedName("kyc_level")
    private String kycLevel;
    @SerializedName("country_restriction")
    private String countryRestriction;

    public Customer(String id, String minAge, String kycLevel, String countryRestriction) {
        this.id = id;
        this.minAge = minAge;
        this.kycLevel = kycLevel;
        this.countryRestriction = countryRestriction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMinAge() {
        return minAge;
    }

    public void setMinAge(String minAge) {
        this.minAge = minAge;
    }

    public String getKycLevel() {
        return kycLevel;
    }

    public void setKycLevel(String kycLevel) {
        this.kycLevel = kycLevel;
    }

    public String getCountryRestriction() {
        return countryRestriction;
    }

    public void setCountryRestriction(String countryRestriction) {
        this.countryRestriction = countryRestriction;
    }
}