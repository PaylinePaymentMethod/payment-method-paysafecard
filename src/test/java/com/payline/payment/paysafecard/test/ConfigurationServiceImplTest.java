package com.payline.payment.paysafecard.test;

import com.payline.payment.paysafecard.ConfigurationServiceImpl;
import com.payline.pmapi.bean.configuration.ContractParametersCheckRequest;
import org.junit.Test;

public class ConfigurationServiceImplTest {

    private ConfigurationServiceImpl service = new ConfigurationServiceImpl();

    @Test
    public void check(){
        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest("FUaLL", "18", null, "cHNjX1I3T1NQNmp2dUpZUmpKNUpIekdxdXVLbTlmOFBMSFo=");
        service.check(request);
    }
}
