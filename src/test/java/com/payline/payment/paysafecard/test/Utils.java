package com.payline.payment.paysafecard.test;

import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.configuration.ContractParametersCheckRequest;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.ContractProperty;
import com.payline.pmapi.bean.payment.PaylineEnvironment;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class Utils {
    private static Locale locale = Locale.FRENCH;


    public static ContractParametersCheckRequest createContractParametersCheckRequest(String kycLevel, String minAge, String countryRestriction, String authorisation) {
        Map<String, String> accountInfo = new HashMap<>();
        accountInfo.put(PaySafeCardConstants.KYCLEVEL_KEY, kycLevel);
        accountInfo.put(PaySafeCardConstants.MINAGE_KEY, minAge);
        accountInfo.put(PaySafeCardConstants.COUNTRYRESTRICTION_KEY, countryRestriction);
        accountInfo.put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, authorisation);

        ContractConfiguration configuration = createContractConfiguration(kycLevel, minAge, countryRestriction, authorisation);
        PaylineEnvironment environment = new PaylineEnvironment("http://notificationURL.com", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);
        PartnerConfiguration partnerConfiguration = new PartnerConfiguration(new HashMap<>());

        return ContractParametersCheckRequest.CheckRequestBuilder.aCheckRequest()
                .withAccountInfo(accountInfo)
                .withLocale(locale)
                .withContractConfiguration(configuration)
                .withPaylineEnvironment(environment)
                .withPartnerConfiguration(partnerConfiguration)
                .build();

    }


    public static ContractConfiguration createContractConfiguration(String kycLevel, String minAge, String countryRestriction, String authorisation) {
        final ContractConfiguration contractConfiguration = new ContractConfiguration("", new HashMap<>());
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.KYCLEVEL_KEY, new ContractProperty(kycLevel));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.MINAGE_KEY, new ContractProperty(minAge));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.COUNTRYRESTRICTION_KEY, new ContractProperty(countryRestriction));
        contractConfiguration.getContractProperties().put(PaySafeCardConstants.AUTHORISATIONKEY_KEY, new ContractProperty(authorisation));

        return contractConfiguration;
    }


}
