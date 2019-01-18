package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.bean.PaySafeRefundRequest;
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
    private static final Logger LOGGER = LogManager.getLogger(RefundServiceImpl.class);

    private PaySafeHttpClient httpClient = PaySafeHttpClient.getInstance();

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
        try {
            boolean isSandbox = refundRequest.getEnvironment().isSandbox();
            PaySafeRefundRequest request = createRequest(refundRequest);

            PaySafePaymentResponse response = httpClient.refund(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findRefundError(response, transactionId);
            } else if (!PaySafeCardConstants.STATUS_REFUND_SUCCESS.equals(response.getStatus())) {
                return PaySafeErrorHandler.getRefundResponseFailure(FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            updateRequest(request);
            response = httpClient.refund(request, isSandbox);

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
            LOGGER.error("unable to refund the payment", e);
            return PaySafeErrorHandler.getRefundResponseFailure(FailureCause.CANCEL, transactionId);
        }
    }

    public PaySafeRefundRequest createRequest(RefundRequest refundRequest) throws InvalidRequestException {
        return new PaySafeRefundRequest(refundRequest);
    }

    public void updateRequest(PaySafeRefundRequest request) {
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
