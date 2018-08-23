package com.payline.payment.paysafecard.test.services;

import com.payline.payment.paysafecard.services.PaymentWithRedirectionServiceImpl;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import org.junit.Test;
import org.mockito.Mockito;

public class PaymentWithRedirectionServiceImplTest {
    private PaymentWithRedirectionServiceImpl service = new PaymentWithRedirectionServiceImpl();


    @Test
    public void finalizeRedirectionPayment() {
        RedirectionPaymentRequest a = Mockito.mock(RedirectionPaymentRequest.class, Mockito.RETURNS_DEEP_STUBS);
        service.finalizeRedirectionPayment(a);
    }

}
