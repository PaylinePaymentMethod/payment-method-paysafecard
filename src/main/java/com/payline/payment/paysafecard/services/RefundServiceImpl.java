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
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

public class RefundServiceImpl implements RefundService {
    private static final Logger logger = LogManager.getLogger(RefundServiceImpl.class);

    private PaySafeHttpClient client;

    public RefundServiceImpl() {
        this.client = new PaySafeHttpClient();
    }

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
        try {
            boolean isSandbox = refundRequest.getEnvironment().isSandbox();
            PaySafePaymentRequest request = createRequest(refundRequest);

            PaySafePaymentResponse response = client.refund(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findRefundError(response, transactionId);
            } else if (!PaySafeCardConstants.STATUS_REFUND_SUCCESS.equals(response.getStatus())) {
                return PaySafeErrorHandler.getRefundResponseFailure(FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            updateRequest(request);
            response = client.refund(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findRefundError(response, transactionId);
            } else if (!PaySafeCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {
                return PaySafeErrorHandler.getRefundResponseFailure(FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            // refund Success
            return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                    .withStatusCode("0")
                    .withPartnerTransactionId(transactionId)
                    .build();


        } catch (InvalidRequestException | URISyntaxException | IOException e) {
            logger.error("unable to refund the payment:" , e.getMessage(), e);
            return PaySafeErrorHandler.getRefundResponseFailure(FailureCause.CANCEL, transactionId);
        }
    }

    public PaySafePaymentRequest createRequest(RefundRequest refundRequest) throws InvalidRequestException {
        return new PaySafePaymentRequest(refundRequest);
    }

    public void updateRequest(PaySafePaymentRequest request) {
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
