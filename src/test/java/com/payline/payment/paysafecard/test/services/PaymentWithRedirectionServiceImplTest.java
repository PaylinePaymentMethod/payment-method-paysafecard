package com.payline.payment.paysafecard.test.services;

import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.services.PaymentWithRedirectionServiceImpl;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.PaymentResponseSuccess;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class PaymentWithRedirectionServiceImplTest {
    @InjectMocks
    private PaymentWithRedirectionServiceImpl service = spy(new PaymentWithRedirectionServiceImpl());

    @Mock
    private PaySafeHttpClient httpClient;

    @Before
    public void init() throws InvalidRequestException {
        PaySafeCaptureRequest captureRequest = new PaySafeCaptureRequest("dumbId", Utils.createContractConfiguration(null, null, null, Utils.AUTHORISATION_VAL));
        doReturn(captureRequest).when(service).createRequest(any(RedirectionPaymentRequest.class));
    }

    @Test
    public void finalizeRedirectionPayment() throws IOException {
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createAuthorizedPaySafeResponse());
        when(httpClient.capture(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createSuccessPaySafeResponse());

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);

        PaymentResponseSuccess responseSuccess = (PaymentResponseSuccess) response;
        Assert.assertEquals("0", responseSuccess.getStatusCode());
    }

    @Test
    public void finalizeWithRetrievePaymentDataError() throws IOException {
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createBadPaySafeResponse());

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    public void finalizeWithRetrievePaymentWrongStatus() throws IOException {
        String json = "{" +
                "    'object': 'PAYMENT'," +
                "    'id': 'pay_9743636706_C4xKjolAPk439xoFnbvhZ9ckUq2aBCT4_EUR'," +
                "    'created': 1534498236365," +
                "    'updated': 1534498236365," +
                "    'amount': 0.01," +
                "    'currency': 'EUR'," +
                "    'status': 'FOO'," +
                "    'type': 'PAYSAFECARD'," +
                "    'customer': {" +
                "        'id': 'foo'" +
                "    }," +
                "    'submerchant_id': '1'" +
                "}";
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertNotNull(responseFailure);
    }

    @Test
    public void finalizeWithCaptureError() throws IOException {
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createAuthorizedPaySafeResponse());
        when(httpClient.capture(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createBadPaySafeResponse());

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    public void finalizeWithCaptureWrongStatus() throws IOException {
        String json = "{" +
                "    'object': 'PAYMENT'," +
                "    'id': 'pay_9743636706_C4xKjolAPk439xoFnbvhZ9ckUq2aBCT4_EUR'," +
                "    'created': 1534498236365," +
                "    'updated': 1534498236365," +
                "    'amount': 0.01," +
                "    'currency': 'EUR'," +
                "    'status': 'FOO'," +
                "    'type': 'PAYSAFECARD'," +
                "    'customer': {" +
                "        'id': 'foo'" +
                "    }," +
                "    'submerchant_id': '1'" +
                "}";
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createAuthorizedPaySafeResponse());
        when(httpClient.capture(any(PaySafeCaptureRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertNotNull(responseFailure);
    }

    @Test
    public void finalizeWithHttpException() throws IOException {
        RedirectionPaymentRequest redirectionPaymentRequest = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        when(httpClient.retrievePaymentData(any(PaySafeCaptureRequest.class), anyBoolean())).thenThrow(IOException.class);

        PaymentResponse response = service.finalizeRedirectionPayment(redirectionPaymentRequest);
        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;
        Assert.assertEquals(FailureCause.INTERNAL_ERROR, responseFailure.getFailureCause());

    }

    @Test
    public void handleSessionExpired() {
        PaymentResponse response = service.handleSessionExpired(null);
        Assert.assertNotNull(response);
    }

}