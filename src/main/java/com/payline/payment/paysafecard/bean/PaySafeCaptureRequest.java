package com.payline.payment.paysafecard.bean;

import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;

public class PaySafeCaptureRequest extends PaySafeRequest {
    private String paymentId;

    public PaySafeCaptureRequest(RedirectionPaymentRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.paymentId= request.getRequestContext().getRequestContext().get(PaySafeCardConstants.PSC_ID);
    }

    public PaySafeCaptureRequest(String paymentId, ContractConfiguration configuration) throws InvalidRequestException {
        super(configuration);
        this.paymentId = paymentId;
    }

    public PaySafeCaptureRequest(TransactionStatusRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.paymentId = request.getTransactionIdentifier();
    }

    public String getPaymentId() {
        return paymentId;
    }
}
