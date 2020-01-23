package com.payline.payment.paysafecard.integration;

import com.payline.payment.paysafecard.Utils;
import com.payline.payment.paysafecard.services.ConfigurationServiceImpl;
import com.payline.payment.paysafecard.services.PaymentServiceImpl;
import com.payline.payment.paysafecard.services.PaymentWithRedirectionServiceImpl;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.PaymentFormContext;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.integration.AbstractPaymentIntegration;
import org.junit.Assert;
import org.junit.Test;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

public class ItTest extends AbstractPaymentIntegration {
    private PaymentServiceImpl paymentService = new PaymentServiceImpl();
    private PaymentWithRedirectionServiceImpl paymentWithRedirectionService = new PaymentWithRedirectionServiceImpl();

    @Override
    protected Map<String, ContractProperty> generateParameterContract() {
        Map<String, ContractProperty> propertyHashMap = new HashMap<>();
        propertyHashMap.put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, new ContractProperty(Utils.AUTHORISATION_VAL));

        return propertyHashMap;
    }

    @Override
    protected PaymentFormContext generatePaymentFormContext() {
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
            driver.findElement(By.id("classicPin-addPinField")).sendKeys(Utils.PAYMENT_TOKEN);

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
    public void checkMinAgeEmpty() {
        ConfigurationServiceImpl service = new ConfigurationServiceImpl();

        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest("SIMPLE", "", "FR", Utils.AUTHORISATION_VAL);
        Map<String, String> errors = service.check(request);

        Assert.assertEquals(0, errors.size());
    }


    @Test
    public void checkCountryRestrictionEmpty() {
        ConfigurationServiceImpl service = new ConfigurationServiceImpl();

        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest("SIMPLE", "18", "",  Utils.AUTHORISATION_VAL);
        Map<String, String> errors = service.check(request);

        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void fullPaymentTest() {
        PaymentRequest request = createDefaultPaymentRequest();
        this.fullRedirectionPayment(request, paymentService, paymentWithRedirectionService);
    }

    @Override
    public PaymentRequest createDefaultPaymentRequest() {
        return Utils.createCompletePaymentBuilder().build();
    }

}
