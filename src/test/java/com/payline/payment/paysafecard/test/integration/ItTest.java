package com.payline.payment.paysafecard.test.integration;

import com.payline.payment.paysafecard.services.ConfigurationServiceImpl;
import com.payline.payment.paysafecard.services.PaymentServiceImpl;
import com.payline.payment.paysafecard.services.PaymentWithRedirectionServiceImpl;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.payment.*;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.integration.AbstractPaymentIntegration;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.io.Serializable;
import java.math.BigInteger;
import java.util.*;
import java.util.concurrent.TimeUnit;

public class ItTest extends AbstractPaymentIntegration {
    private static String kycLevel = null;
    private static String minAge = null;
    private static String countryRestriction = null;
    private static String authorisation = "cHNjX1I3T1NQNmp2dUpZUmpKNUpIekdxdXVLbTlmOFBMSFo=";
    private static final String PAYMENT_TOKEN = "10000009016901270";    // big token but not unlimited

    private ConfigurationServiceImpl configurationService = new ConfigurationServiceImpl();
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl paymentWithRedirectionService = new PaymentWithRedirectionServiceImpl();

    @Override
    protected Map<String, ContractProperty> generateParameterContract() {
        Map<String, ContractProperty> propertyHashMap = new HashMap<>();
        propertyHashMap.put(PaySafeCardConstants.KYCLEVEL_KEY, new ContractProperty(kycLevel));
        propertyHashMap.put(PaySafeCardConstants.MINAGE_KEY, new ContractProperty(minAge));
        propertyHashMap.put(PaySafeCardConstants.COUNTRYRESTRICTION_KEY, new ContractProperty(countryRestriction));
        propertyHashMap.put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, new ContractProperty(authorisation));

        return propertyHashMap;
    }

    @Override
    protected Map<String, Serializable> generatePaymentFormData() {
        return null;
    }

    @Override
    protected String payOnPartnerWebsite(String partnerUrl) {
        // Start browser
        WebDriver driver = new ChromeDriver();
        driver.manage().timeouts().implicitlyWait(10, TimeUnit.SECONDS);
        try {

            // Go to partner's website
            driver.get(partnerUrl);
            driver.findElement(By.id("classicPin-addPinField")).sendKeys(PAYMENT_TOKEN);

            // accept CGU
            driver.findElement(By.xpath("/html/body/section[1]/section[1]/div[1]/div/div/article/div[1]/div[4]/div[1]/div/div[1]/label")).click();

            // validate payment
            driver.findElement(By.xpath("//*[@id='payBtn']")).click();

            // Wait for redirection to success or cancel url
            WebDriverWait wait = new WebDriverWait(driver, 30);
            wait.until(ExpectedConditions.or(ExpectedConditions.urlToBe(SUCCESS_URL), ExpectedConditions.urlToBe(CANCEL_URL)));
            return driver.getCurrentUrl();
        } finally {
            driver.quit();
        }

    }

    @Override
    protected String cancelOnPartnerWebsite(String s) {
        return null;
    }


    @Test
    public void fullPaymentTest() {
        PaymentRequest request = createDefaultPaymentRequest();
        this.fullRedirectionPayment(request, paymentService, paymentWithRedirectionService);
    }


    //


    public static String createTransactionId() {
        return "transactionID" + Calendar.getInstance().getTimeInMillis();
    }

    public static Map<Buyer.AddressType, Buyer.Address> createAddresses(Buyer.Address address) {
        Map<Buyer.AddressType, Buyer.Address> addresses = new HashMap<>();
        addresses.put(Buyer.AddressType.DELIVERY, address);
        addresses.put(Buyer.AddressType.BILLING, address);

        return addresses;
    }

    public static Map<Buyer.AddressType, Buyer.Address> createDefaultAddresses() {
        Buyer.Address address = createDefaultAddress();
        return createAddresses(address);
    }

    public static Amount createAmount(String currency) {
        return new Amount(BigInteger.ONE, Currency.getInstance(currency));
    }

    public static Order createOrder(String transactionID) {
        return Order.OrderBuilder.anOrder().withReference(transactionID).build();
    }

    public static Order createOrder(String transactionID, Amount amount) {
        return Order.OrderBuilder.anOrder().withReference(transactionID).withAmount(amount).build();
    }

    public static Buyer.FullName createFullName() {
        return new Buyer.FullName("foo", "bar", Buyer.Civility.UNKNOWN);
    }

    public static Map<Buyer.PhoneNumberType, String> createDefaultPhoneNumbers() {
        Map<Buyer.PhoneNumberType, String> phoneNumbers = new HashMap<>();
        phoneNumbers.put(Buyer.PhoneNumberType.BILLING, "0606060606");

        return phoneNumbers;
    }

    public static Buyer.Address createAddress(String street, String city, String zip) {
        return Buyer.Address.AddressBuilder.anAddress()
                .withStreet1(street)
                .withCity(city)
                .withZipCode(zip)
                .withCountry("country")
                .build();
    }

    public static Buyer.Address createDefaultAddress() {
        return createAddress("a street", "a city", "a zip");
    }

    public static Buyer createBuyer(Map<Buyer.PhoneNumberType, String> phoneNumbers, Map<Buyer.AddressType, Buyer.Address> addresses, Buyer.FullName fullName) {
        return Buyer.BuyerBuilder.aBuyer()
                .withEmail("foo@bar.baz")
                .withPhoneNumbers(phoneNumbers)
                .withAddresses(addresses)
                .withFullName(fullName)
                .withCustomerIdentifier("dumbCustomerId")
                .build();
    }

    public static Buyer createDefaultBuyer() {
        return createBuyer(createDefaultPhoneNumbers(), createDefaultAddresses(), createFullName());
    }

    public static ContractConfiguration createContractConfiguration() {
        final ContractConfiguration contractConfiguration = new ContractConfiguration("", new HashMap<>());
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.KYCLEVEL_KEY, new ContractProperty(kycLevel));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.COUNTRYRESTRICTION_KEY, new ContractProperty(countryRestriction));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.MINAGE_KEY, new ContractProperty(minAge));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, new ContractProperty(authorisation));

        return contractConfiguration;
    }

    @Override
    public PaymentRequest createDefaultPaymentRequest() {

        final Amount amount = createAmount("EUR");
        final ContractConfiguration contractConfiguration = createContractConfiguration();

        final PaylineEnvironment paylineEnvironment = new PaylineEnvironment(NOTIFICATION_URL, SUCCESS_URL, CANCEL_URL, true);
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
                .withBuyer(buyer)
                .build();
    }

}
