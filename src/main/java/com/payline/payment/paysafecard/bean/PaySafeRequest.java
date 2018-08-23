package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.Expose;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.payment.ContractConfiguration;

public abstract class PaySafeRequest {
    @Expose(serialize = false, deserialize = false)
    private String authenticationHeader;

    // todo throw error si ya un truc null
    public PaySafeRequest(ContractConfiguration configuration) {
        this.authenticationHeader = "Basic " + configuration.getProperty(PaySafeCardConstants.AUTHORISATIONKEY_KEY).getValue();
    }

    public String getAuthenticationHeader() {
        return authenticationHeader;
    }
}
