package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.*;
import com.payline.pmapi.bean.configuration.*;
import com.payline.pmapi.service.ConfigurationService;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class ConfigurationServiceImpl implements ConfigurationService {
    private static final String VERSION = "1.0";
    private static final String RELEASE_DATE = "12/12/2012";

    private PaySafeHttpClient httpClient = new PaySafeHttpClient();


    private final LocalizationService localization;

    public ConfigurationServiceImpl() {
        this.localization = LocalizationImpl.getInstance();
    }

    @Override
    public List<AbstractParameter> getParameters(Locale locale) {
        List<AbstractParameter> parameters = new ArrayList<>();

        // authorisation key
        final InputParameter authorisationKey = new InputParameter();
        authorisationKey.setKey(PaySafeCardConstants.AUTHORISATIONKEY_KEY);
        authorisationKey.setLabel(localization.getSafeLocalizedString("contract.authorisationKey.label", locale));
        authorisationKey.setDescription(localization.getSafeLocalizedString("contract.authorisationKey.description", locale));
        authorisationKey.setRequired(false);

        parameters.add(authorisationKey);

        // kyc level
        Map<String, String> kycLevelMap = new HashMap<>();
        kycLevelMap.put(PaySafeCardConstants.KYCLEVEL_SIMPLE, localization.getSafeLocalizedString("contract.kycLevel.simple", locale));
        kycLevelMap.put(PaySafeCardConstants.KYCLEVEL_FULL, localization.getSafeLocalizedString("contract.kycLevel.full", locale));

        final ListBoxParameter kycLevel = new ListBoxParameter();
        kycLevel.setKey(PaySafeCardConstants.KYCLEVEL_KEY);
        kycLevel.setLabel(localization.getSafeLocalizedString("contract.kycLevel.label", locale));
        kycLevel.setDescription(localization.getSafeLocalizedString("contract.kycLevel.description", locale));
        kycLevel.setList(kycLevelMap);
        kycLevel.setRequired(true);

        parameters.add(kycLevel);

        // age limit
        final InputParameter minAge = new InputParameter();
        minAge.setKey(PaySafeCardConstants.MINAGE_KEY);
        minAge.setLabel(localization.getSafeLocalizedString("contract.minAge.label", locale));
        minAge.setDescription(localization.getSafeLocalizedString("contract.minAge.description", locale));
        minAge.setRequired(false);

        parameters.add(minAge);

        // country restriction
        final InputParameter countryRestriction = new InputParameter();
        countryRestriction.setKey(PaySafeCardConstants.COUNTRYRESTRICTION_KEY);
        countryRestriction.setLabel(localization.getSafeLocalizedString("contract.countryRestriction.label", locale));
        countryRestriction.setLabel(localization.getSafeLocalizedString("contract.countryRestriction.description", locale));
        countryRestriction.setRequired(false);

        parameters.add(countryRestriction);

        return parameters;
    }

    @Override
    public Map<String, String> check(ContractParametersCheckRequest contractParametersCheckRequest) {
        Map<String, String> errors = new HashMap<>();

        try {
            // create a CheckRequest
            PaySafePaymentRequest checkRequest = new PaySafePaymentRequest(contractParametersCheckRequest);

            // do the request
            Boolean isSandbox = contractParametersCheckRequest.getPaylineEnvironment().isSandbox();
            PaySafePaymentResponse response = httpClient.initiate(checkRequest, isSandbox);

            // check response object
            if (response.getCode() != null) {
                findErrors(response, errors);
            }

        } catch (IOException | InvalidRequestException e) {
            errors.put(ContractParametersCheckRequest.GENERIC_ERROR, e.getMessage());
        }

        return errors;
    }

    @Override
    public ReleaseInformation getReleaseInformation() {
        LocalDate date = LocalDate.parse(RELEASE_DATE, DateTimeFormatter.ofPattern("dd/MM/yyyy"));
        return ReleaseInformation.ReleaseBuilder.aRelease().withDate(date).withVersion(VERSION).build();
    }

    @Override
    public String getName(Locale locale) {
        return localization.getSafeLocalizedString("project.name", locale);
    }

    // todo remprendre avec toutes les erreurs possibles + gerer la traduction?
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
}
