package com.payline.payment.paysafecard.bean;

import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.pmapi.bean.refund.request.RefundRequest;

public class PaySafeRefundRequest extends PaySafePaymentRequest {
    private boolean capture;

    public PaySafeRefundRequest(RefundRequest request) throws InvalidRequestException {
        super(request);
        this.capture = false;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }
}
