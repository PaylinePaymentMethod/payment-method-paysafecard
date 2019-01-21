package com.payline.payment.paysafecard.test.utils;

import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import org.junit.Assert;
import org.junit.Test;

public class PaySafeHttpClientTest {
    private PaySafeHttpClient client = PaySafeHttpClient.getInstance();

    @Test
    public void getHost(){
        Assert.assertNotNull(client.getHost(true));
        Assert.assertNotNull(client.getHost(false));
    }

    @Test
    public void createUrl(){
        String path1 = "foo";
        String path2 = "bar";
        Assert.assertEquals("/foo/bar/", client.createPath( path1, path2));
        Assert.assertEquals("/", client.createPath());
    }
}
