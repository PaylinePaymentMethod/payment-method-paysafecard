package com.payline.payment.paysafecard.test.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.services.PaymentServiceImpl;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseRedirect;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class PaymentServiceImplTest {

    @InjectMocks
    private PaymentServiceImpl service = new PaymentServiceImpl();
    @Mock
    private PaySafeHttpClient httpClient;

    @Test
    public void paymentRequest() throws IOException, URISyntaxException {
        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createInitiatedPaySafeResponse());

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseRedirect responseRedirect = (PaymentResponseRedirect) response;

        Assert.assertEquals(Utils.AUTH_URL, responseRedirect.getRedirectionRequest().getUrl().toString());
    }


    @Test
    public void paymentRequestWithError10007() throws IOException, URISyntaxException  {
        String json = "{" +
                "    \"code\": \"general_technical_error\"," +
                "    \"number\": 10007" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithError10008() throws IOException, URISyntaxException  {
        String json = "{" +
                "    \"code\": \"invalid_api_key\"," +
                "    \"number\": 10008" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithError10028() throws IOException, URISyntaxException  {
        String json = "{" +
                "    \"code\": \"invalid_request_parameter\"," +
                "    \"number\": 10028" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithError2001() throws IOException, URISyntaxException  {
        String json = "{" +
                "    code: duplicate_transaction_id," +
                "    number: 2001" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithError2017() throws IOException, URISyntaxException  {
        String json = "{" +
                "    code: duplicate_transaction_id," +
                "    number: 2017" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithError3001() throws IOException, URISyntaxException  {
        String json = "{" +
                "    \"code\": \"duplicate_transaction_id\"," +
                "    \"number\": 3001" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithError3007() throws IOException, URISyntaxException  {
        String json = "{" +
                "    \"code\": \"Merchant with Id dumbId is not allowed to perform this debit any more\"," +
                "    \"number\": 3007" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.PAYMENT_PARTNER_ERROR, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithError3014() throws IOException, URISyntaxException  {
        String json = "{" +
                "    \"code\": \"submerchant_not_found\"," +
                "    \"number\": 3014" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.INVALID_DATA, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithErrorUnknown() throws IOException, URISyntaxException  {
        String json = "{" +
                "    \"code\": \"this is a code\"," +
                "    \"number\": foo" +
                "}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithErrorNull() throws IOException, URISyntaxException  {
        String json = "{\"code\": \"this is a code\"}";

        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createPaySafeResponse(json));

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.PARTNER_UNKNOWN_ERROR, responseFailure.getFailureCause());
    }

    @Test
    public void paymentRequestWithException() throws IOException, URISyntaxException  {
        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenThrow(IOException.class);

        PaymentRequest request = Utils.createCompletePaymentBuilder().build();
        PaymentResponse response = service.paymentRequest(request);

        PaymentResponseFailure responseFailure = (PaymentResponseFailure) response;

        Assert.assertEquals(FailureCause.INTERNAL_ERROR, responseFailure.getFailureCause());
    }
}
