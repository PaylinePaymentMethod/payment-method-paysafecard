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
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.payline.payment.paysafecard.utils.PaySafeCardConstants.DEFAULT_SUCCESS_STATUS_CODE;

public class RefundServiceImpl implements RefundService {
    private static final Logger LOGGER = LogManager.getLogger(RefundServiceImpl.class);

    private PaySafeHttpClient httpClient;

    @Override
    public RefundResponse refundRequest(RefundRequest refundRequest) {
        String transactionId = refundRequest.getTransactionId();
        try {
            boolean isSandbox = refundRequest.getEnvironment().isSandbox();
            PaySafeRefundRequest request = createRequest(refundRequest);

            httpClient = getHttpClient(refundRequest);
            PaySafePaymentResponse response = httpClient.refund(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findRefundError(response, transactionId);
            } else if (!PaySafeCardConstants.STATUS_REFUND_SUCCESS.equals(response.getStatus())) {
                return PaySafeErrorHandler.getRefundResponseFailure(response.getStatus(),  FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            updateRequest(request);
            response = httpClient.refund(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findRefundError(response, transactionId);
            } else if (!PaySafeCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {
                return PaySafeErrorHandler.getRefundResponseFailure(response.getStatus(), FailureCause.PARTNER_UNKNOWN_ERROR, transactionId);
            }

            // refund Success
            return RefundResponseSuccess.RefundResponseSuccessBuilder.aRefundResponseSuccess()
                    .withStatusCode(DEFAULT_SUCCESS_STATUS_CODE)
                    .withPartnerTransactionId(transactionId)
                    .build();


        } catch (URISyntaxException | IOException e) {
            LOGGER.error("unable to refund the payment", e);
            return PaySafeErrorHandler.getRefundResponseFailure(e.getMessage(), FailureCause.CANCEL, transactionId);
        }catch (InvalidRequestException e) {
            LOGGER.info("unable to refund the payment", e.getMessage());
            return PaySafeErrorHandler.getRefundResponseFailure(e.getMessage(), FailureCause.CANCEL, transactionId);
        }
    }

    public PaySafeHttpClient getHttpClient(final RefundRequest refundRequest) {
        return PaySafeHttpClient.getInstance(refundRequest.getPartnerConfiguration());
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
