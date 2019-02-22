package com.payline.payment.paysafecard.utils;

public class PaySafeCardConstants {
    public static final String KYCLEVEL_SIMPLE = "SIMPLE";
    public static final String KYCLEVEL_FULL = "FULL";
    public static final String KYCLEVEL_KEY = "KYC_LEVEL";
    public static final String MINAGE_KEY = "MIN_AGE";
    public static final String COUNTRYRESTRICTION_KEY = "COUNTRY_RESTRICTION";
    public static final String AUTHORISATIONKEY_KEY = "AUTHORISATION";

    public static final String MERCHANT_NAME_KEY = "MERCHANT_NAME";
    public static final String MERCHANT_ID_KEY = "MERCHANT_ID";

    public static final String SCHEME = "https";
    public static final String SANDBOX_URL = "apitest.paysafecard.com";
    public static final String PRODUCTION_URL = "api.paysafecard.com";
    public static final String PATH = "payments";
    public static final String PATH_VERSION = "v1";
    public static final String PATH_CAPTURE = "capture";
    public static final String PATH_REFUND = "refunds";
    public static final String PSC_ID = "psc_id";

    public static final String STATUS_AUTHORIZED = "AUTHORIZED";
    public static final String STATUS_SUCCESS = "SUCCESS";
    public static final String STATUS_REFUND_SUCCESS = "VALIDATION_SUCCESSFUL";
    public static final String STATUS_CANCELED_MERCHANT = "CANCELED_MERCHANT";
    public static final String STATUS_CANCELED_CUSTOMER = "CANCELED_CUSTOMER";
    public static final String STATUS_EXPIRED = "EXPIRED";

    public static final String SETTLEMENT_KEY = "SETTLEMENT_KEY";
    public static final String RELEASE_DATE_FORMAT = "dd/MM/yyyy";
    public static final String RELEASE_PROPERTIES = "release.properties";
    public static final String RELEASE_VERSION = "release.version";
    public static final String RELEASE_DATE = "release.date";

    public static final String DEFAULT_EMAIL = "no.email.provided@client.unlogged";

    private PaySafeCardConstants() {
        //ras
    }
}
