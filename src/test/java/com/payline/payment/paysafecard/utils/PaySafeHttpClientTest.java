package com.payline.payment.paysafecard.utils;

import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.Configurable;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.junit.Assert;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.HashMap;


@RunWith(MockitoJUnitRunner.class)
public class PaySafeHttpClientTest {

    private static HashMap<String, String> partnerConfigurationMap;

    private static PaySafeHttpClient client;

    private static RequestConfig requestConfig;

    @BeforeClass
    public static void beforeClass() {
        partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(PaySafeHttpClient.KEY_CONNECT_TIMEOUT,"2000");
        partnerConfigurationMap.put(PaySafeHttpClient.CONNECTION_REQUEST_TIMEOUT,"3000");
        partnerConfigurationMap.put(PaySafeHttpClient.READ_SOCKET_TIMEOUT,"4000");

        requestConfig = RequestConfig.custom()
                .setConnectTimeout(2000)
                .setConnectionRequestTimeout(3000)
                .setSocketTimeout(4000).build();

        client = PaySafeHttpClient.getInstance(new PartnerConfiguration(partnerConfigurationMap, new HashMap<>()));

    }

    @Test
    public void getHost(){
        Assert.assertNotNull(client.getHost(true));
        Assert.assertNotNull(client.getHost(false));
    }

    @Test
    public void createUrl(){
        client = PaySafeHttpClient.getInstance(new PartnerConfiguration(partnerConfigurationMap, new HashMap<>()));
        String path1 = "foo";
        String path2 = "bar";
        Assert.assertEquals("/foo/bar/", client.createPath( path1, path2));
        Assert.assertEquals("/", client.createPath());
    }

    @Test
    public void testWithNoPoolMaxSize() throws Exception {
        getHttpClient("30000", "30000", "3", null, "400000");
    }

    @Test
    public void testWithEmptyPoolMaxSize() throws Exception {
        getHttpClient("30000", "30000", "3", "", "400000");
    }


    @Test
    public void testWithNoPoolValidation() throws Exception{
        getHttpClient("30000", "30000", "3", "360000", null);
    }

    @Test
    public void testWithEmptyPoolValidation() throws Exception {
        getHttpClient(null, null,null,null,"");
    }

    @Test
    public void testWithAllOptions() throws Exception {
        getHttpClient("30000", "30000", "3", "360000", "400000");
    }

    @Test
    public void testWithEmptyOptions() throws Exception {
        getHttpClient("", "", "", "", "");
    }


    private void getHttpClient(final String connectionTimeToLive, final String evictIdleConnectionTimeout, final String keepAliveDuration,
                               final String poolMaxSize, final String poolValidate) throws IOException {
        HashMap<String, String> partnerMapTest = getParametersMap(connectionTimeToLive, evictIdleConnectionTimeout, keepAliveDuration,
                poolMaxSize, poolValidate);
        final PartnerConfiguration partnerConfiguration = new PartnerConfiguration(partnerMapTest, new HashMap<>());
        HttpClientBuilder builder = client.getHttpClientBuilder(partnerConfiguration, requestConfig);
        Assert.assertNotNull(builder);
        try (CloseableHttpClient httpClient = builder.build()){
            RequestConfig requestConfig = ((Configurable) httpClient).getConfig();
            Assert.assertEquals(4000, requestConfig.getSocketTimeout());
            Assert.assertEquals(3000, requestConfig.getConnectionRequestTimeout());
            Assert.assertEquals(2000, requestConfig.getConnectTimeout());
        }

    }


    private HashMap<String, String> getParametersMap(final String connectionTimeToLive, final String evictIdleConnectionTimeout, final String keepAliveDuration, final String poolMaxSize, final String poolValidate) {
        HashMap<String, String> partnerMapTest = new HashMap<>(partnerConfigurationMap);
        partnerMapTest.put(PaySafeHttpClient.CONNECTION_TIME_TO_LIVE, connectionTimeToLive);
        partnerMapTest.put(PaySafeHttpClient.EVICT_IDLE_CONNECTION_TIMEOUT, evictIdleConnectionTimeout);
        partnerMapTest.put(PaySafeHttpClient.KEEP_ALIVE_DURATION, keepAliveDuration);
        partnerMapTest.put(PaySafeHttpClient.POOL_MAX_SIZE_PER_ROUTE, poolMaxSize);
        partnerMapTest.put(PaySafeHttpClient.POOL_VALIDATE_CONN_AFTER_INACTIVITY, poolValidate);
        return partnerMapTest;
    }
}
