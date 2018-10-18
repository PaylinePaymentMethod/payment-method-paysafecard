package com.payline.payment.paysafecard.test.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.services.ConfigurationServiceImpl;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.io.IOException;
import java.net.URISyntaxException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyBoolean;
import static org.mockito.Mockito.when;


@RunWith(MockitoJUnitRunner.class)
public class ConfigurationServiceImplTest {
    private String goodKycLevel = "FULL";
    private String goodMinAge = "18";
    private String goodCountryRestriction = "FR";
    private String goodAuthorisation = "cHNjX1I3T1NQNmp2dUpZUmpKNUpIekdxdXVLbTlmOFBMSFo=";

    private Locale locale = Locale.FRENCH;
    @InjectMocks
    private ConfigurationServiceImpl service = new ConfigurationServiceImpl();
    @Mock
    private PaySafeHttpClient httpClient;


    @Test
    public void getParameters() {
        List<AbstractParameter> parameters = service.getParameters(locale);
        Assert.assertEquals(7, parameters.size());
    }

    @Test
    public void checkGood() throws IOException, URISyntaxException {
        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createInitiatedPaySafeResponse());

        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest(goodKycLevel, goodMinAge, goodCountryRestriction, goodAuthorisation);
        Map<String, String> errors = service.check(request);

        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void checkBad() throws IOException, URISyntaxException {
        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createBadPaySafeResponse());

        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest(goodKycLevel, goodMinAge, goodCountryRestriction, goodAuthorisation);
        Map<String, String> errors = service.check(request);

        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void checkBadMinAge() throws IOException, URISyntaxException {
        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createBadPaySafeResponse());

        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest(goodKycLevel, "a", goodCountryRestriction, goodAuthorisation);
        Map<String, String> errors = service.check(request);

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey(PaySafeCardConstants.MINAGE_KEY));
    }

    @Test
    public void checkBadCountryRestriction() throws IOException, URISyntaxException {
        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenReturn(Utils.createBadPaySafeResponse());

        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest(goodKycLevel, goodMinAge, "foo", goodAuthorisation);
        Map<String, String> errors = service.check(request);

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey(PaySafeCardConstants.COUNTRYRESTRICTION_KEY));
    }

    @Test
    public void checkException() throws IOException, URISyntaxException {
        when(httpClient.initiate(any(PaySafePaymentRequest.class), anyBoolean())).thenThrow(IOException.class);

        ContractParametersCheckRequest request = Utils.createContractParametersCheckRequest(goodKycLevel, goodMinAge, goodCountryRestriction, goodAuthorisation);
        Map<String, String> errors = service.check(request);

        Assert.assertEquals(1, errors.size());
    }

    @Test
    public void findErrorNoError() {
        Map<String, String> errors = new HashMap<>();
        service.findErrors(Utils.createInitiatedPaySafeResponse(), errors);

        Assert.assertEquals(0, errors.size());
    }

    @Test
    public void findErrorInvalidKey() {
        Map<String, String> errors = new HashMap<>();
        String json = "{" +
                "    'code': 'invalid_api_key'," +
                "    'message': 'Authentication failed'," +
                "    'number': 10008" +
                "}";
        service.findErrors(Utils.createPaySafeResponse(json), errors);

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey(PaySafeCardConstants.AUTHORISATIONKEY_KEY));
    }

    @Test
    public void findErrorInvalidKycLevel() {
        Map<String, String> errors = new HashMap<>();
        String json = "{" +
                "    'code': 'invalid_request_parameter'," +
                "    'message': 'Valid values are: SIMPLE, FULL'," +
                "    'number': 10028," +
                "    'param': 'kyc_level'" +
                "}";
        service.findErrors(Utils.createPaySafeResponse(json), errors);

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey(PaySafeCardConstants.KYCLEVEL_KEY));
    }

    @Test
    public void findErrorInvalidMinAge() {
        Map<String, String> errors = new HashMap<>();
        String json = "{" +
                "    'code': 'invalid_request_parameter'," +
                "    'message': 'must be greater than or equal to 1'," +
                "    'number': 10028," +
                "    'param': 'min_age'" +
                "}";
        service.findErrors(Utils.createPaySafeResponse(json), errors);

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey(PaySafeCardConstants.MINAGE_KEY));
    }

    @Test
    public void findErrorInvalidRestriction() {
        Map<String, String> errors = new HashMap<>();
        String json = "{" +
                "    'code': 'invalid_restriction'," +
                "    'message': 'Could not convert restriction value foo!'," +
                "    'number': 2039" +
                "}";
        service.findErrors(Utils.createPaySafeResponse(json), errors);

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey(PaySafeCardConstants.COUNTRYRESTRICTION_KEY));
    }


    @Test
    public void findErrorUnknownError() {
        Map<String, String> errors = new HashMap<>();
        String json = "{" +
                "    'code': 'dumb error'," +
                "    'message': 'this is a message'," +
                "    'number': 0000" +
                "}";
        service.findErrors(Utils.createPaySafeResponse(json), errors);

        Assert.assertEquals(1, errors.size());
        Assert.assertTrue(errors.containsKey(ContractParametersCheckRequest.GENERIC_ERROR));
    }

    @Test
    public void getName() {
        String name = service.getName(locale);
        Assert.assertNotNull(name);
        Assert.assertNotEquals(0, name.length());
    }
}
