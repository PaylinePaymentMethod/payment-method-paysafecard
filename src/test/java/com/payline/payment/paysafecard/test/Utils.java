package com.payline.payment.paysafecard.test;

import com.google.gson.Gson;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

import java.math.BigInteger;
import java.util.*;

public class Utils {
    private static Locale locale = Locale.FRENCH;
    public static final String SUCCESS_URL = "https://succesurl.com/";
    public static final String FAILURE_URL = "http://cancelurl.com/";
    public static final String NOTIFICATION_URL = "http://notificationurl.com/";
    public static final String AUTH_URL = "http://authenticationurl.com/";

    public static final String AUTHORISATION_VAL = "psc_R7OSP6jvuJYRjJ5JHzGquuKm9f8PLHZ";
//    public static final String AUTHORISATION_VAL = "cHNjX1I3T1NQNmp2dUpZUmpKNUpIekdxdXVLbTlmOFBMSFo=";
    public static final String PAYMENT_TOKEN = "10000009016901270";    // big token but not unlimited


    private static final Gson parser = new Gson();

    public static PaySafePaymentResponse createInitiatedPaySafeResponse() {
        String json = "{" +
                "    'object': 'PAYMENT'," +
                "    'id': 'pay_9743636706_C4xKjolAPk439xoFnbvhZ9ckUq2aBCT4_EUR'," +
                "    'created': 1534498236365," +
                "    'updated': 1534498236365," +
                "    'amount': 0.01," +
                "    'currency': 'EUR'," +
                "    'status': 'INITIATED'," +
                "    'type': 'PAYSAFECARD'," +
                "    'redirect': {" +
                "        'success_url': '" + SUCCESS_URL + "'," +
                "        'failure_url': '" + FAILURE_URL + "'," +
                "        'auth_url': '" + AUTH_URL + "'" +
                "    }," +
                "    'customer': {" +
                "        'id': 'toto'" +
                "    }," +
                "    'notification_url': '" + NOTIFICATION_URL + "'," +
                "    'submerchant_id': '1'" +
                "}";
        return createPaySafeResponse(json);
    }

    public static PaySafePaymentResponse createAuthorizedPaySafeResponse() {
        String json = "{" +
                "    'object': 'PAYMENT'," +
                "    'id': 'pay_9743636706_C4xKjolAPk439xoFnbvhZ9ckUq2aBCT4_EUR'," +
                "    'created': 1534498236365," +
                "    'updated': 1534498236365," +
                "    'amount': 0.01," +
                "    'currency': 'EUR'," +
                "    'status': 'AUTHORIZED'," +
                "    'type': 'PAYSAFECARD'," +
                "    'redirect': {" +
                "        'success_url': '" + SUCCESS_URL + "'," +
                "        'failure_url': '" + FAILURE_URL + "'," +
                "        'auth_url': '" + AUTH_URL + "'" +
                "    }," +
                "    'customer': {" +
                "        'id': 'toto'" +
                "    }," +
                "    'notification_url': '" + NOTIFICATION_URL + "'," +
                "    'submerchant_id': '1'," +
                "    'card_details': [" +
                "        { 'serial': '10000009094601270', 'type': '00028', 'country': 'DE', 'currency': 'EUR', 'amount': 2 }" +
                "    ]" +
                "}";
        return createPaySafeResponse(json);
    }

    public static PaySafePaymentResponse createSuccessPaySafeResponse() {
        String json = "{" +
                "    'object': 'PAYMENT'," +
                "    'id': 'pay_9743636706_C4xKjolAPk439xoFnbvhZ9ckUq2aBCT4_EUR'," +
                "    'created': 1534498236365," +
                "    'updated': 1534498236365," +
                "    'amount': 0.01," +
                "    'currency': 'EUR'," +
                "    'status': 'SUCCESS'," +
                "    'type': 'PAYSAFECARD'," +
                "    'redirect': {" +
                "        'success_url': '" + SUCCESS_URL + "'," +
                "        'failure_url': '" + FAILURE_URL + "'," +
                "        'auth_url': '" + AUTH_URL + "'" +
                "    }," +
                "    'customer': {" +
                "        'id': 'toto'" +
                "    }," +
                "    'notification_url': '" + NOTIFICATION_URL + "'," +
                "    'submerchant_id': '1'," +
                "    'card_details': [" +
                "        { 'serial': '10000009094601270', 'type': '00028', 'country': 'DE', 'currency': 'EUR', 'amount': 2 }" +
                "    ]" +
                "}";
        return createPaySafeResponse(json);
    }

    public static PaySafePaymentResponse createBadPaySafeResponse() {
        String json = "{" +
                "    'code': 'invalid_request_parameter'," +
                "    'message': 'must be greater than or equal to 1'," +
                "    'number': 10028," +
                "    'param': 'min_age'" +
                "}";
        return createPaySafeResponse(json);
    }

    public static PaySafePaymentResponse createPaySafeResponse(String json) {
        return parser.fromJson(json, PaySafePaymentResponse.class);
    }

    public static ContractParametersCheckRequest createContractParametersCheckRequest(String kycLevel, String minAge, String countryRestriction, String authorisation) {
        Map<String, String> accountInfo = new HashMap<>();
        accountInfo.put(PaySafeCardConstants.KYCLEVEL_KEY, kycLevel);
        accountInfo.put(PaySafeCardConstants.MINAGE_KEY, minAge);
        accountInfo.put(PaySafeCardConstants.COUNTRYRESTRICTION_KEY, countryRestriction);
        accountInfo.put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, authorisation);

        ContractConfiguration configuration = createContractConfiguration(kycLevel, minAge, countryRestriction, authorisation);
        PaylineEnvironment environment = new PaylineEnvironment("http://notificationURL.com", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);
        PartnerConfiguration partnerConfiguration = new PartnerConfiguration(new HashMap<>());

        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(accountInfo)
                .withLocale(locale)
                .withContractConfiguration(configuration)
                .withPaylineEnvironment(environment)
                .withPartnerConfiguration(partnerConfiguration)
                .build();

    }

    public static PaymentRequest.Builder createCompletePaymentBuilder() {
        final Amount amount = createAmount("EUR");
        final ContractConfiguration contractConfiguration = createDefaultContractConfiguration();

        final PaylineEnvironment paylineEnvironment = new PaylineEnvironment(NOTIFICATION_URL, SUCCESS_URL, FAILURE_URL, true);
        final String transactionID = createTransactionId();
        final Order order = createOrder(transactionID);
        final String softDescriptor = "softDescriptor";
        final Locale locale = new Locale("FR");

        Buyer buyer = createDefaultBuyer();

        return PaymentRequest.builder()
                .withAmount(amount)
                .withBrowser(new Browser("", Locale.FRANCE))
                .withContractConfiguration(contractConfiguration)
                .withPaylineEnvironment(paylineEnvironment)
                .withOrder(order)
                .withLocale(locale)
                .withTransactionId(transactionID)
                .withSoftDescriptor(softDescriptor)
                .withBuyer(buyer);
    }

    private static String createTransactionId() {
        return "transactionID" + Calendar.getInstance().getTimeInMillis();
    }

    private static Map<Buyer.AddressType, Buyer.Address> createAddresses(Buyer.Address address) {
        Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.DELIVERY, address);
        addresses.put(Buyer.AddressType.BILLING, address);

        return addresses;
    }

    private static Map<Buyer.AddressType, Buyer.Address> createDefaultAddresses() {
        Buyer.Address address = createDefaultAddress();
        return createAddresses(address);
    }

    private static Amount createAmount(String currency) {
        return new Amount(BigInteger.TEN, Currency.getInstance(currency));
    }

    private static Order createOrder(String transactionID) {
        return Order.OrderBuilder.anOrder().withReference(transactionID).build();
    }

    public static Order createOrder(String transactionID, Amount amount) {
        return Order.OrderBuilder.anOrder().withReference(transactionID).withAmount(amount).build();
    }

    private static Buyer.FullName createFullName() {
        return new Buyer.FullName("foo", "bar", Buyer.Civility.UNKNOWN);
    }

    private static Map<Buyer.PhoneNumberType, String> createDefaultPhoneNumbers() {
        Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, "0606060606");

        return phoneNumbers;
    }

    public static ContractConfiguration createContractConfiguration(String kycLevel, String minAge, String countryRestriction, String authorisation) {
        final ContractConfiguration contractConfiguration = new ContractConfiguration("", new HashMap<>());
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.KYCLEVEL_KEY, new ContractProperty(kycLevel));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.MINAGE_KEY, new ContractProperty(minAge));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.COUNTRYRESTRICTION_KEY, new ContractProperty(countryRestriction));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, new ContractProperty(authorisation));

        return contractConfiguration;
    }

    public static ContractConfiguration createDefaultContractConfiguration() {
        final ContractConfiguration contractConfiguration = new ContractConfiguration("", new HashMap<>());
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, new ContractProperty(AUTHORISATION_VAL));

        return contractConfiguration;
    }

    private static Buyer.Address createAddress(String street, String city, String zip) {
        return Buyer.Address.AddressBuilder.anAddress()
                .withStreet1(street)
                .withCity(city)
                .withZipCode(zip)
                .withCountry("country")
                .build();
    }

    private static Buyer.Address createDefaultAddress() {
        return createAddress("a street", "a city", "a zip");
    }

    private static Buyer createBuyer(Map<Buyer.PhoneNumberType, String> phoneNumbers, Map<Buyer.AddressType, Buyer.Address> addresses, Buyer.FullName fullName) {
        return Buyer.BuyerBuilder.aBuyer()
                .withCustomerIdentifier("customerId")
                .withEmail("foo@bar.baz")
                .withPhoneNumbers(phoneNumbers)
                .withAddresses(addresses)
                .withFullName(fullName)
                .build();
    }

    private static Buyer createDefaultBuyer() {
        return createBuyer(createDefaultPhoneNumbers(), createDefaultAddresses(), createFullName());
    }

}
