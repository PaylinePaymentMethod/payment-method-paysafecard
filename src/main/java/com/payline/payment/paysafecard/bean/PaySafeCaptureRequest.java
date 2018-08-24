package com.payline.payment.paysafecard.bean;

import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;

public class PaySafeCaptureRequest extends PaySafeRequest {
    private String paymentId;

    public PaySafeCaptureRequest(RedirectionPaymentRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        if (request.getRedirectionContext() == null){
            throw new InvalidRequestException("PaySafeRequest must have a paymentId key when created");
        } else {
            this.paymentId = request.getRedirectionContext().toString();
        }
    }

    public String getPaymentId() {
        return paymentId;
    }
}
