package com.payline.payment.paysafecard.bean;

public class Customer {
    private String id;
    private String min_age;
    private String kyc_level;
    private String country_restriction;


    public Customer(String id, String minAge, String kycLevel, String countryRestriction) {
        this.id = id;
        this.min_age = minAge;
        this.kyc_level = kycLevel;
        this.country_restriction = countryRestriction;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMin_age() {
        return min_age;
    }

    public void setMin_age(String min_age) {
        this.min_age = min_age;
    }

    public String getKyc_level() {
        return kyc_level;
    }

    public void setKyc_level(String kyc_level) {
        this.kyc_level = kyc_level;
    }

    public String getCountry_restriction() {
        return country_restriction;
    }

    public void setCountry_restriction(String country_restriction) {
        this.country_restriction = country_restriction;
    }
}