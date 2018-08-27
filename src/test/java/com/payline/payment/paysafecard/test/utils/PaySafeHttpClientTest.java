package com.payline.payment.paysafecard.test.utils;

import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import org.junit.Assert;
import org.junit.Test;

public class PaySafeHttpClientTest {
    private PaySafeHttpClient client = new PaySafeHttpClient();

    @Test
    public void getHost(){
        Assert.assertNotNull(client.getHost(true));
        Assert.assertNotNull(client.getHost(false));
    }

    @Test
    public void createUrl(){
        Assert.assertNotNull(client.createUrl("www.foo.bar", "foo", "bar"));
        Assert.assertNotNull(client.createUrl("www.foo.bar"));
    }
}
