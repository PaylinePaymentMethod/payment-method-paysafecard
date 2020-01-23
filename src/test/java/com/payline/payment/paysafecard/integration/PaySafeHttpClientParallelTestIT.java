package com.payline.payment.paysafecard.utils;

import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.logging.log4j.Level;
import org.apache.logging.log4j.LogManager;
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
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Captor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;
import org.mockito.runners.MockitoJUnitRunner;

import java.time.Duration;
import java.util.HashMap;

@RunWith(MockitoJUnitRunner.class)
public class PaySafeHttpClientParallelTestIT {

    private static HashMap<String, String> partnerConfigurationMap;

    @Mock
    private Appender appender;

    @Captor
    private ArgumentCaptor<LogEvent> captor;

    private LoggerConfig loggerConfig;



    @BeforeClass
    public static void beforeClass() {
        partnerConfigurationMap = new HashMap<>();
        partnerConfigurationMap.put(PaySafeHttpClient.KEY_CONNECT_TIMEOUT,"2000");
        partnerConfigurationMap.put(PaySafeHttpClient.CONNECTION_REQUEST_TIMEOUT,"3000");
        partnerConfigurationMap.put(PaySafeHttpClient.READ_SOCKET_TIMEOUT,"4000");
    }

    @Before
    public void init() throws Exception {

        MockitoAnnotations.initMocks(this);
        Mockito.when(appender.getName()).thenReturn("MockAppender");
        Mockito.when(appender.isStarted()).thenReturn(true);
        Mockito.when(appender.isStopped()).thenReturn(false);

        final LoggerContext ctx = (LoggerContext) LogManager.getContext(false);
        final Configuration config = ctx.getConfiguration();
        loggerConfig = config.getLoggerConfig("com.payline.payment.paysafecard.utils.PaySafeHttpClient");
        loggerConfig.addAppender(appender, Level.INFO, null);
    }

    @Test
    public void testInitOnly1Time() throws Exception {
        for (int i = 0; i < 50; i++) {
            launchGetInstance();
        }
        Thread.sleep(5000);
        // On capture la log
        Mockito.verify(appender, Mockito.atLeastOnce()).append(captor.capture());
        // On vérifie le nombre attendu
        Assert.assertThat(captor.getAllValues().size(), CoreMatchers.is(1));
        // On vérifie les messages formatés et leur niveau de log
        Assert.assertThat(captor.getAllValues().get(0).getMessage().getFormattedMessage(),
                CoreMatchers.is("Initialisation du service HTTP Client"));
        Assert.assertThat(captor.getAllValues().get(0).getLevel(), CoreMatchers.is(Level.INFO));
    }

    private void launchGetInstance() {
        new Thread(() -> {
            final HashMap<String, String> partnerConfigurationMap = new HashMap<>();
            partnerConfigurationMap.put(PaySafeHttpClient.KEY_CONNECT_TIMEOUT,"2000");
            partnerConfigurationMap.put(PaySafeHttpClient.CONNECTION_REQUEST_TIMEOUT,"3000");
            partnerConfigurationMap.put(PaySafeHttpClient.READ_SOCKET_TIMEOUT,"4000");
            PaySafeHttpClient.getInstance(new PartnerConfiguration(partnerConfigurationMap, new HashMap<>()));
        }).start();
    }

    @After
    public void tearDown() {
        loggerConfig.removeAppender("MockAppender");
    }

}
