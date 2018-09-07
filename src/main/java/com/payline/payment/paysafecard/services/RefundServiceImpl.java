package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.payment.paysafecard.utils.PaySafeErrorHandler;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.refund.request.RefundRequest;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseSuccess;
import com.payline.pmapi.service.RefundService;

import java.io.IOException;

public class RefundServiceImpl implements RefundService {
    PaySafeHttpClient client;

    public RefundServiceImpl() {
        this.client = new PaySafeHttpClient();
    }

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
        try {
            boolean isSandbox = refundRequest.getPaylineEnvironment().isSandbox();
            PaySafePaymentRequest request = createRequest(refundRequest);

            PaySafePaymentResponse response = client.refund(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findRefundError(response, transactionId);
            } else if (!PaySafeCardConstants.STATUS_REFUND_SUCCESS.equals(response.getStatus())) {
                return PaySafeErrorHandler.getRefundResponseFailure("unknown", FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            updateRequest(request);
            response = client.refund(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findRefundError(response, transactionId);
            } else if (!PaySafeCardConstants.STATUS_REFUND_SUCCESS.equals(response.getStatus())) {
                return PaySafeErrorHandler.getRefundResponseFailure("unknown", FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            // refund Success
            return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                    .withStatusCode("0")
                    .withTransactionId(String.valueOf(transactionId))
                    .build();


        } catch (InvalidRequestException | IOException e) {
            return PaySafeErrorHandler.getRefundResponseFailure(e.getMessage(), FailureCause.CANCEL, transactionId);
        }
    }

    public PaySafePaymentRequest createRequest(RefundRequest refundRequest) throws InvalidRequestException {
        return new PaySafePaymentRequest(refundRequest);
    }

    public void updateRequest(PaySafePaymentRequest request){
        request.setCapture(true);
    }


    @Override
    public boolean canMultiple() {
        return false;
    }

    @Override
    public boolean canPartial() {
        return false;
    }
}
