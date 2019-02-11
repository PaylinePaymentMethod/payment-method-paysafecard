package com.payline.payment.paysafecard.utils;

public class PaySafeCardConstants {
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



    // data used in I18nService
    public static final String I18N_SERVICE_DEFAULT_LOCALE = "en";
    public static final String RESOURCE_BUNDLE_BASE_NAME = "traduction";

    // data used in ConfigurationService
    public static final String MERCHANT_NAME_KEY = "MERCHANT_NAME";
    public static final String MERCHANT_NAME_LABEL = "contract.merchantName.label";
    public static final String MERCHANT_ID_KEY = "MERCHANT_ID";
    public static final String MERCHANT_ID_LABEL = "contract.merchantId.label";
    public static final String AUTHORISATIONKEY_KEY = "AUTHORISATION";
    public static final String AUTHORISATIONKEY_LABEL = "contract.authorisationKey.label";
    public static final String AUTHORISATIONKEY_DESCRIPTION = "contract.authorisationKey.description";
    public static final String SETTLEMENT_KEY = "SETTLEMENT_KEY";
    public static final String SETTLEMENT_LABEL = "contract.settlementKey.label";
    public static final String MINAGE_KEY = "MIN_AGE";
    public static final String MINAGE_LABEL = "contract.minAge.label";
    public static final String KYCLEVEL_SIMPLE_KEY = "SIMPLE";
    public static final String KYCLEVEL_SIMPLE_VAL = "contract.kycLevel.simple";
    public static final String KYCLEVEL_FULL_KEY = "FULL";
    public static final String KYCLEVEL_FULL_VAL = "contract.kycLevel.full";
    public static final String KYCLEVEL_KEY = "KYC_LEVEL";
    public static final String KYCLEVEL_LABEL = "contract.kycLevel.label";
    public static final String COUNTRYRESTRICTION_KEY = "COUNTRY_RESTRICTION";
    public static final String COUNTRYRESTRICTION_LABEL = "contract.countryRestriction.label";
    public static final String COUNTRYRESTRICTION_DESCRIPTION = "contract.countryRestriction.description";

    // data used in PaymentFormConfigurationService
    public static final String NO_FIELD_TEXT = "form.button.paySafeCard.text";
    public static final String NO_FIELD_DESCRIPTION = "form.button.paySafeCard.description";

    // data used in PaymentWithRedirectionService
    public static final String DEFAULT_SUCCESS_STATUS_CODE = "0";


    public static final String DEFAULT_EMAIL = "no.email.provided@client.unlogged";

    private PaySafeCardConstants() {
        //ras
    }
}
