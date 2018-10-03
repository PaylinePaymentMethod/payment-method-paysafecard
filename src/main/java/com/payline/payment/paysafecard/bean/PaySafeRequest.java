package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.Expose;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.payment.ContractConfiguration;

import java.util.Base64;

public abstract class PaySafeRequest {
    @Expose(serialize = false, deserialize = false)
    private String authenticationHeader;

    PaySafeRequest(ContractConfiguration configuration) throws InvalidRequestException {
        if (configuration == null || configuration.getProperty(PaySafeCardConstants.AUTHORISATIONKEY_KEY).getValue() == null){
            throw new InvalidRequestException("PaySafeRequest must have an authorisation key when created");
        } else {
            this.authenticationHeader = "Basic " + encodeToBase64( configuration.getProperty(PaySafeCardConstants.AUTHORISATIONKEY_KEY).getValue());
        }
    }

    public String getAuthenticationHeader() {
        return authenticationHeader;
    }

    public static String encodeToBase64(String toEncode){
        if (toEncode == null) toEncode = "";
        return Base64.getEncoder().encodeToString(toEncode.getBytes());
    }
}
