package com.payline.payment.paysafecard.bean;

import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.Request;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

public abstract class PaySafeRequest {
    private String authentHeader;

    // todo throw error
    public PaySafeRequest(ContractConfiguration configuration) {
        this.authentHeader = configuration.getProperty(PaySafeCardConstants.AUTHORISATIONKEY_KEY).getValue();
    }

    public String getAuthentHeader() {
        return "Basic "+ authentHeader;
    }
}
