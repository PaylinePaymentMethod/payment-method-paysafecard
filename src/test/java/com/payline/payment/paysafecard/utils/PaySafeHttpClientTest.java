package com.payline.payment.paysafecard.utils;

import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.logging.log4j.core.Appender;
import org.apache.logging.log4j.core.LogEvent;
import org.apache.logging.log4j.core.LoggerContext;
import org.apache.logging.log4j.core.config.Configuration;
import org.apache.logging.log4j.core.config.LoggerConfig;
import org.hamcrest.CoreMatchers;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.platform.commons.logging.LoggerFactory;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.Spy;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.HashMap;
import java.util.Map;

@RunWith(MockitoJUnitRunner.class)
public class PaySafeHttpClientTest {

    private static HashMap<String, String> partnerConfigurationMap;

    private PaySafeHttpClient client;

    @BeforeClass
    public static void beforeClass() {
        partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(PaySafeHttpClient.KEY_CONNECT_TIMEOUT,"2000");
        partnerConfigurationMap.put(PaySafeHttpClient.CONNECTION_REQUEST_TIMEOUT,"3000");
        partnerConfigurationMap.put(PaySafeHttpClient.READ_SOCKET_TIMEOUT,"4000");
    }

    @Test
    public void getHost(){
        client = PaySafeHttpClient.getInstance(new PartnerConfiguration(partnerConfigurationMap, new HashMap<>()));
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

}
