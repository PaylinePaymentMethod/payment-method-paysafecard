package com.payline.payment.paysafecard.utils.config;

import com.payline.payment.paysafecard.utils.properties.service.ReleaseProperties;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class ConfigPropertiesTest {

    private String key;


    @Test
    public void getFromKeyKO() {
        key = ReleaseProperties.INSTANCE.get("BadKey");
        Assertions.assertNull(key);

    }

    @Test
    public void getFromKeyOK() {
        key = ReleaseProperties.INSTANCE.get("release.version");
        Assertions.assertNotNull(key);
    }

}
