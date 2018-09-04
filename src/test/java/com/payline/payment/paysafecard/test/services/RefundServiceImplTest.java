package com.payline.payment.paysafecard.test.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.services.RefundServiceImpl;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
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
import static org.mockito.Mockito.*;

@RunWith(MockitoJUnitRunner.class)
public class RefundServiceImplTest {
    @InjectMocks
    private RefundServiceImpl service = spy(new RefundServiceImpl());

    @Mock
    private PaySafeHttpClient httpClient;

    private RefundRequest request;

    @Before
    public void init() throws InvalidRequestException {
        PaySafePaymentRequest paymentRequest = null;
        doReturn(paymentRequest).when(service).createRequest(any(RefundRequest.class));
        request = Mockito.mock(RefundRequest.class, Mockito.RETURNS_DEEP_STUBS);

    }

    @Test
    public void refundRequest() throws IOException {
        String json = "{" +
                "  'object': 'refund'," +
                "  'id': 'ref_1000000007_2838fhsd6dashsdkfsd_EUR'," +
                "  'created': 1430137532383, 'updated': 1430137532383," +
                "  'currency': 'EUR', 'amount': '10.00'," +
                "  'customer': { 'id': 'merchantclientid5HzDvoZSodKDJ7X7VQKrtestAutomation', 'email': 'valid@email.com' }," +
                "  'status': 'SUCCESSFULEUpdated'" +
                "}";
        when(httpClient.refund(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        RefundResponse response = service.refundRequest(request);

        Assert.assertNotNull(response);
        RefundResponseSuccess responseSuccess = (RefundResponseSuccess) response;
        Assert.assertNotNull(responseSuccess);

    }

    @Test
    public void refundRequestError() throws IOException {
        String json = "{" +
                "    'code': 'merchant_refund_missing_transaction'," +
                "    'message': 'No original Transaction found'," +
                "    'number': 3184" +
                "}";
        when(httpClient.refund(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        RefundResponse response = service.refundRequest(request);

        Assert.assertNotNull(response);
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assert.assertNotNull(responseFailure);
    }

    @Test
    public void refundWithWrongStatus() throws IOException {
        String json = "{" +
                "  'object': 'refund'," +
                "  'id': 'ref_1000000007_2838fhsd6dashsdkfsd_EUR'," +
                "  'created': 1430137532383, 'updated': 1430137532383," +
                "  'currency': 'EUR', 'amount': '10.00'," +
                "  'customer': { 'id': 'merchantclientid5HzDvoZSodKDJ7X7VQKrtestAutomation', 'email': 'valid@email.com' }," +
                "  'status': 'DUMBSTATUS'" +
                "}";
        when(httpClient.refund(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        RefundResponse response = service.refundRequest(request);

        Assert.assertNotNull(response);
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assert.assertNotNull(responseFailure);
    }


    @Test
    public void refundWithException() throws IOException {
        when(httpClient.refund(any(PaySafePaymentRequest.class), anyBoolean())).thenThrow(IOException.class);

        RefundResponse response = service.refundRequest(request);

        Assert.assertNotNull(response);
        RefundResponseFailure responseFailure = (RefundResponseFailure) response;
        Assert.assertNotNull(responseFailure);
    }



    @Test
    public void canMultiple() {
        Assert.assertNotNull(service.canMultiple());
    }

    @Test
    public void canPartial() {
        Assert.assertNotNull(service.canPartial());
    }
}
