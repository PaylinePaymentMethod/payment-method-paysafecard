package com.payline.payment.paysafecard.test.services;

import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.services.PaymentWithRedirectionServiceImpl;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.PaymentResponseSuccess;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentWithRedirectionServiceImplTest {
    @InjectMocks
    private PaymentWithRedirectionServiceImpl service = new PaymentWithRedirectionServiceImpl();

    @Mock
    private PaySafeHttpClient httpClient;

    private static final String AUTHORIZED_RESPONSE = "{" +
            "    'object': 'PAYMENT'," +
            "    'id': 'pay_9743636706_1ScndUxQPp6jvvSR6XXsAMvjr9QQVstK_EUR'," +
            "    'created': 1533804605318," +
            "    'updated': 1533804628278," +
            "    'amount': 2," +
            "    'currency': 'EUR'," +
            "    'status': 'AUTHORIZED'," +
            "    'type': 'PAYSAFECARD'," +
            "    'redirect': {" +
            "        'success_url': 'https://www.google.com'," +
            "        'failure_url': 'https://translate.google.fr'," +
            "        'auth_url': 'https://customer.test.at.paysafecard.com/psccustomer/GetCustomerPanelServlet?mid=9743636706&mtid=pay_9743636706_1ScndUxQPp6jvvSR6XXsAMvjr9QQVstK_EUR&amount=2.00&currency=EUR'" +
            "    } ," +
            "    'customer': { 'id': 'toto', 'ip': '77.136.40.225' }," +
            "    'notification_url': 'https://www.paysafecard.com/notification/pay_9743636706_1ScndUxQPp6jvvSR6XXsAMvjr9QQVstK_EUR'," +
            "    'submerchant_id': '1'," +
            "    'card_details': [" +
            "        { 'serial': '10000009094601270', 'type': '00028', 'country': 'DE', 'currency': 'EUR', 'amount': 2 }" +
            "    ]" +
            "}";

    private static final String SUCCESS_RESPONSE = "{" +
            "    'object': 'PAYMENT'," +
            "    'id': 'pay_9743636706_HbB79q97NUDfMCDsPXywXsILHYnj1eph_EUR'," +
            "    'created': 1533732577755," +
            "    'updated': 1533732588564," +
            "    'amount': 2," +
            "    'currency': 'EUR'," +
            "    'account': '1'," +
            "    'status': 'SUCCESS'," +
            "    'type': 'PAYSAFECARD'," +
            "    'redirect': {" +
            "        'success_url': 'https://www.paysafecard.com/success/pay_9743636706_HbB79q97NUDfMCDsPXywXsILHYnj1eph_EUR'," +
            "        'failure_url': 'https://www.paysafecard.com/failure/pay_9743636706_HbB79q97NUDfMCDsPXywXsILHYnj1eph_EUR'" +
            "    }," +
            "    'customer': { 'id': 'merchantclientid5HzDvoZSodKDJ7X7VQKrtestAutomation', 'ip': '77.154.225.117' }," +
            "    'notification_url': 'https://www.paysafecard.com/notification/pay_9743636706_HbB79q97NUDfMCDsPXywXsILHYnj1eph_EUR'," +
            "    'submerchant_id': '1'," +
            "    'card_details': [" +
            "        { 'serial': '10000009094601270', 'type': '00028', 'country': 'DE', 'currency': 'EUR', 'amount': 2 }" +
            "    ]" +
            "}";


    @Test
    public void finalizeRedirectionPayment() throws IOException {
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(AUTHORIZED_RESPONSE));
        when(httpClient.capture(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(SUCCESS_RESPONSE));

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);

        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assert.assertEquals("0", responseSuccess.getStatusCode());
    }

    @Test
    public void finalizeWithRetrievePaymentDataError() throws IOException {
        String json = "{ 'code': 'this is a code', 'number': foo}";
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(AUTHORIZED_RESPONSE));
        when(httpClient.capture(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());

    }

    @Test
    public void finalizeWithCaptureError() throws IOException {
        String json = "{ 'code': 'this is a code', 'number': foo}";
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());

    }

    @Test
    public void finalizeWithHttpException() throws IOException {
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenThrow(IOException.class);

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertEquals(FailureCause.INTERNAL_ERROR, responseFailure.getFailureCause());

    }


}
