package com.payline.payment.paysafecard.test.bean;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import org.junit.Assert;
import org.junit.Test;

public class PaySafeRequestTest {


    @Test
    public void createAmount() {
        Assert.assertEquals("0.00", PaySafePaymentRequest.createAmount(0));
        Assert.assertEquals("0.01", PaySafePaymentRequest.createAmount(1));
        Assert.assertEquals("1.00", PaySafePaymentRequest.createAmount(100));
        Assert.assertEquals("10.00", PaySafePaymentRequest.createAmount(1000));
        Assert.assertEquals("100.00", PaySafePaymentRequest.createAmount(10000));
    }
}
