package com.payline.payment.paysafecard.bean;

import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;

public class PaySafeCaptureRequest extends PaySafeRequest {
    private String paymentId;

    public PaySafeCaptureRequest(RedirectionPaymentRequest request) {
        super(request.getContractConfiguration());
        this.paymentId = request.getRedirectionContext().toString();
    }

    public PaySafeCaptureRequest(String paymentId, ContractConfiguration configuration){
        super(configuration);
        this.paymentId = paymentId;
    }

    public String getPaymentId() {
        return paymentId;
    }
}
