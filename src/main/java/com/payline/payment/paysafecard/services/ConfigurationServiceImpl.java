package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.*;
import com.payline.payment.paysafecard.utils.i18n.I18nService;
import com.payline.payment.paysafecard.utils.properties.constants.ConfigurationConstants;
import com.payline.payment.paysafecard.utils.properties.service.ReleaseProperties;
import com.payline.pmapi.bean.configuration.ReleaseInformation;
import com.payline.pmapi.bean.configuration.parameter.AbstractParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.InputParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.ListBoxParameter;
import com.payline.pmapi.bean.configuration.parameter.impl.PasswordParameter;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.service.ConfigurationService;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

import static com.payline.payment.paysafecard.utils.PaySafeCardConstants.*;
import static com.payline.payment.paysafecard.utils.properties.constants.ConfigurationConstants.PAYMENT_METHOD_NAME;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final Logger LOGGER = LogManager.getLogger(ConfigurationServiceImpl.class);

    private final I18nService i18n;

    public ConfigurationServiceImpl() {
        i18n = I18nService.getInstance();
    }

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // Merchant name
        final InputParameter merchantName = new InputParameter();
        merchantName.setKey(PaySafeCardConstants.MERCHANT_NAME_KEY);
        merchantName.setLabel(i18n.getMessage(MERCHANT_NAME_LABEL, locale));
        merchantName.setRequired(true);

        parameters.add(merchantName);

        // Mid
        final InputParameter merchantId = new InputParameter();
        merchantId.setKey(PaySafeCardConstants.MERCHANT_ID_KEY);
        merchantId.setLabel(i18n.getMessage(MERCHANT_ID_LABEL, locale));
        merchantId.setRequired(true);

        parameters.add(merchantId);

        // authorisation key
        final PasswordParameter authorisationKey = new PasswordParameter();
        authorisationKey.setKey(PaySafeCardConstants.AUTHORISATIONKEY_KEY);
        authorisationKey.setLabel(i18n.getMessage(AUTHORISATIONKEY_LABEL, locale));
        authorisationKey.setDescription(i18n.getMessage(AUTHORISATIONKEY_DESCRIPTION, locale));
        authorisationKey.setRequired(true);

        parameters.add(authorisationKey);

        //settlement key
        final PasswordParameter settlementKey = new PasswordParameter();
        settlementKey.setKey(PaySafeCardConstants.SETTLEMENT_KEY);
        settlementKey.setLabel(i18n.getMessage(SETTLEMENT_LABEL, locale));
        settlementKey.setRequired(false);

        parameters.add(settlementKey);

        // age limit
        final InputParameter minAge = new InputParameter();
        minAge.setKey(PaySafeCardConstants.MINAGE_KEY);
        minAge.setLabel(i18n.getMessage(MINAGE_LABEL, locale));
        minAge.setRequired(false);

        parameters.add(minAge);

        // kyc level
        Map<String, String> kycLevelMap = new HashMap<>();
        kycLevelMap.put(PaySafeCardConstants.KYCLEVEL_SIMPLE_KEY, i18n.getMessage(KYCLEVEL_SIMPLE_VAL, locale));
        kycLevelMap.put(PaySafeCardConstants.KYCLEVEL_FULL_KEY, i18n.getMessage(KYCLEVEL_FULL_VAL, locale));

        final ListBoxParameter kycLevel = new ListBoxParameter();
        kycLevel.setKey(PaySafeCardConstants.KYCLEVEL_KEY);
        kycLevel.setLabel(i18n.getMessage(KYCLEVEL_LABEL, locale));
        kycLevel.setList(kycLevelMap);
        kycLevel.setRequired(false);

        parameters.add(kycLevel);

        // country restriction
        final InputParameter countryRestriction = new InputParameter();
        countryRestriction.setKey(PaySafeCardConstants.COUNTRYRESTRICTION_KEY);
        countryRestriction.setLabel(i18n.getMessage(COUNTRYRESTRICTION_LABEL, locale));
        countryRestriction.setDescription(i18n.getMessage(COUNTRYRESTRICTION_DESCRIPTION, locale));
        countryRestriction.setRequired(false);

        parameters.add(countryRestriction);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        Map<String, String> errors = new HashMap<>();
        Locale locale = contractParametersCheckRequest.getLocale();

        // verify configuration fields
        String minAge = contractParametersCheckRequest.getContractConfiguration().getProperty(PaySafeCardConstants.MINAGE_KEY).getValue();
        String countryRestriction = contractParametersCheckRequest.getContractConfiguration().getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY).getValue();

        // verify fields
        try {
            DataChecker.verifyMinAge(minAge);
        } catch (BadFieldException e) {
            errors.put(e.getField(), i18n.getMessage(e.getMessage(), locale));
        }

        try {
            DataChecker.verifyCountryRestriction(countryRestriction);
        } catch (BadFieldException e) {
            errors.put(e.getField(), i18n.getMessage(e.getMessage(), locale));
        }

        // if there is some errors, stop the process and return them
        if (errors.size() > 0) {
            return errors;
        }

        try {
            // create a CheckRequest
            PaySafePaymentRequest checkRequest = new PaySafePaymentRequest(contractParametersCheckRequest);

            // do the request
            Boolean isSandbox = contractParametersCheckRequest.getEnvironment().isSandbox();
            final PaySafeHttpClient httpClient = getHttpClientInstance(contractParametersCheckRequest);
            PaySafePaymentResponse response = httpClient.initiate(checkRequest, isSandbox);

            // check response object
            if (response.getCode() != null) {
                findErrors(response, errors);
            }

        } catch (IOException | URISyntaxException e) {
            LOGGER.error("unable to check the connection", e);
            errors.put(ContractParametersCheckRequest.GENERIC_ERROR, e.getMessage());
        }catch ( InvalidRequestException e) {
            LOGGER.info("unable to check the connection", e.getMessage());
            errors.put(ContractParametersCheckRequest.GENERIC_ERROR, e.getMessage());
        }

        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        LocalDate date = LocalDate.parse(ReleaseProperties.INSTANCE.get(ConfigurationConstants.RELEASE_DATE),
                DateTimeFormatter.ofPattern(ConfigurationConstants.RELEASE_DATE_FORMAT));
        return ReleaseInformation.ReleaseBuilder.aRelease()
                .withDate(date)
                .withVersion(ReleaseProperties.INSTANCE.get(ConfigurationConstants.RELEASE_VERSION))
                .build();
    }

    @Override
    public String getName(Locale locale) {
        return i18n.getMessage(PAYMENT_METHOD_NAME, locale);
    }

    public void findErrors(PaySafePaymentResponse message, Map<String, String> errors) {
        if (message.getCode() != null) {
            switch (message.getCode()) {
                case "invalid_api_key":
                    // bad authorisation key in header
                    errors.put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, message.getMessage());
                    break;
                case "invalid_request_parameter":
                    // bad parameter, check field "param" to find it
                    if ("kyc_level".equals(message.getParam())) {
                        errors.put(PaySafeCardConstants.KYCLEVEL_KEY, message.getMessage());
                    } else if ("min_age".equals(message.getParam())) {
                        errors.put(PaySafeCardConstants.MINAGE_KEY, message.getMessage());
                    }
                    break;
                case "invalid_restriction":
                    // bad country restriction value
                    errors.put(PaySafeCardConstants.COUNTRYRESTRICTION_KEY, message.getMessage());
                    break;
                default:
                    errors.put(ContractParametersCheckRequest.GENERIC_ERROR, message.getMessage());
            }
        }
    }

    protected PaySafeHttpClient getHttpClientInstance(final ContractParametersCheckRequest contractParametersCheckRequest) {
        return PaySafeHttpClient.getInstance(contractParametersCheckRequest.getPartnerConfiguration());
    }
}
