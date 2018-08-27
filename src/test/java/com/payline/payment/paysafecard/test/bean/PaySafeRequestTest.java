package com.payline.payment.paysafecard.test.bean;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.configuration.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.PaylineEnvironment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import org.junit.Assert;
import org.junit.Test;

import java.math.BigInteger;
import java.util.Currency;

public class PaySafeRequestTest {

    @Test
    public void createPaySafeRequestFromContractParam() throws InvalidRequestException {
        ContractParametersCheckRequest checkRequest = Utils.createContractParametersCheckRequest(null, null, null, Utils.AUTHORISATION_VAL);
        PaySafePaymentRequest request = new PaySafePaymentRequest(checkRequest);
        Assert.assertNotNull(request);
    }

    @Test
    public void createPaySafeRequestFromPaymentRequest() throws InvalidRequestException {
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().build();
        PaySafePaymentRequest request = new PaySafePaymentRequest(paymentRequest);
        Assert.assertNotNull(request);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutAmount() throws InvalidRequestException {
        Amount amount = new Amount(null, Currency.getInstance("EUR"));
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withAmount(amount).build();
        new PaySafePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutCurrency() throws InvalidRequestException {
        Amount amount = new Amount(BigInteger.ONE, null);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withAmount(amount).build();
        new PaySafePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutCustomerId() throws InvalidRequestException {
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withBuyer(null).build();
        new PaySafePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutUrls() throws InvalidRequestException {
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withPaylineEnvironment(null).build();
        new PaySafePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithNullSuccessUrl() throws InvalidRequestException {
        PaylineEnvironment environment = new PaylineEnvironment(Utils.NOTIFICATION_URL, null, Utils.FAILURE_URL, true);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withPaylineEnvironment(environment).build();
        new PaySafePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithNullFailureUrl() throws InvalidRequestException {
        PaylineEnvironment environment = new PaylineEnvironment(Utils.NOTIFICATION_URL, Utils.SUCCESS_URL, null, true);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withPaylineEnvironment(environment).build();
        new PaySafePaymentRequest(paymentRequest);
    }

    @Test(expected = InvalidRequestException.class)
    public void createPaySafeRequestWithoutAuthorisationKey() throws InvalidRequestException {
        ContractConfiguration configuration = Utils.createContractConfiguration(null, null, null, null);
        PaymentRequest paymentRequest = Utils.createCompletePaymentBuilder().withContractConfiguration(configuration).build();
        new PaySafePaymentRequest(paymentRequest);
    }

    @Test
    public void createAmount() {
        Assert.assertEquals("0.00", PaySafePaymentRequest.createAmount(0));
        Assert.assertEquals("0.01", PaySafePaymentRequest.createAmount(1));
        Assert.assertEquals("1.00", PaySafePaymentRequest.createAmount(100));
        Assert.assertEquals("10.00", PaySafePaymentRequest.createAmount(1000));
        Assert.assertEquals("100.00", PaySafePaymentRequest.createAmount(10000));
    }
}
