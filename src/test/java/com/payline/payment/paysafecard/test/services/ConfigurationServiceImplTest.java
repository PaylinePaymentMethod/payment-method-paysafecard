package com.payline.payment.paysafecard.test.services;

import com.payline.payment.paysafecard.services.ConfigurationServiceImpl;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.configuration.AbstractParameter;
import com.payline.pmapi.bean.configuration.ContractParametersCheckRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.Map;


@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceImplTest {
    private String goodKycLevel = "FULL";
    private String goodMinAge = "18";
    private String goodCountryRestriction = "EU";
    private String goodAuthorisation = "cHNjX1I3T1NQNmp2dUpZUmpKNUpIekdxdXVLbTlmOFBMSFo=";

    private Locale locale = Locale.FRENCH;
    @InjectMocks private ConfigurationServiceImpl service = new ConfigurationServiceImpl();
    @Mock private PaySafeHttpClient httpClient;

    private final String goodPaySafeResponseMessage = "{\n" +
            "    \"object\": \"PAYMENT\",\n" +
            "    \"id\": \"pay_9743636706_A0HVHJaamc8EOz1FJVwUUBKgnK8KTCI9_EUR\",\n" +
            "    \"created\": 1534259261554,\n" +
            "    \"updated\": 1534259261554,\n" +
            "    \"amount\": 0.01,\n" +
            "    \"currency\": \"EUR\",\n" +
            "    \"status\": \"INITIATED\",\n" +
            "    \"type\": \"PAYSAFECARD\",\n" +
            "    \"redirect\": {\n" +
            "        \"success_url\": \"https://www.google.com\",\n" +
            "        \"failure_url\": \"https://translate.google.fr\",\n" +
            "        \"auth_url\": \"https://customer.test.at.paysafecard.com/psccustomer/GetCustomerPanelServlet?mid=9743636706&mtid=pay_9743636706_A0HVHJaamc8EOz1FJVwUUBKgnK8KTCI9_EUR&amount=0.01&currency=EUR\"\n" +
            "    },\n" +
            "    \"customer\": {\n" +
            "        \"id\": \"toto\"\n" +
            "    },\n" +
            "    \"notification_url\": \"https://www.paysafecard.com/notification/pay_9743636706_A0HVHJaamc8EOz1FJVwUUBKgnK8KTCI9_EUR\",\n" +
            "    \"submerchant_id\": \"1\"\n" +
            "}";

    @Test
    public void getParameters(){
        List<AbstractParameter> parameters = service.getParameters(locale);
        Assert.assertEquals(4,parameters.size());
    }

    @Test
    public void checkGood() throws IOException {
        //when(httpClient.doPost(anyString(), anyString(), any(PaySafePaymentRequest.class))).thenReturn(goodPaySafeResponseMessage);


        Map<String, String> errors;
        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest(goodKycLevel, goodMinAge, goodCountryRestriction, goodAuthorisation);
        errors = service.check(request);

        Assert.assertEquals(0, errors.size());
    }
}
