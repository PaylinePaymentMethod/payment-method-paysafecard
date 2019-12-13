package com.payline.payment.paysafecard.bean;

import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;

public class PaySafeCaptureRequest extends PaySafeRequest {
    private String paymentId;

    public PartnerConfiguration getPartnerConfiguration() {
        return partnerConfiguration;
    }

    private PartnerConfiguration partnerConfiguration;

    public PaySafeCaptureRequest(RedirectionPaymentRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.partnerConfiguration = request.getPartnerConfiguration();
        this.paymentId= request.getRequestContext().getRequestData().get(PaySafeCardConstants.PSC_ID);
    }

    public PaySafeCaptureRequest(String paymentId, ContractConfiguration configuration, PartnerConfiguration partnerConfiguration) throws InvalidRequestException {
        super(configuration);
        this.paymentId = paymentId;
        this.partnerConfiguration = partnerConfiguration;
    }

    public PaySafeCaptureRequest(TransactionStatusRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.paymentId = request.getTransactionId();
        this.partnerConfiguration = request.getPartnerConfiguration();
    }

    public String getPaymentId() {
        return paymentId;
    }
}
