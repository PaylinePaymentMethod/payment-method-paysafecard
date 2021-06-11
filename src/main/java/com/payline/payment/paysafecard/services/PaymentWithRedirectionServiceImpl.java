package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.*;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.Email;
import com.payline.pmapi.bean.payment.response.impl.PaymentResponseSuccess;
import com.payline.pmapi.logger.LogManager;
import com.payline.pmapi.service.PaymentWithRedirectionService;
import org.apache.logging.log4j.Logger;

import java.io.IOException;
import java.net.URISyntaxException;

import static com.payline.payment.paysafecard.utils.PaySafeCardConstants.DEFAULT_EMAIL;
import static com.payline.payment.paysafecard.utils.PaySafeCardConstants.DEFAULT_SUCCESS_STATUS_CODE;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    private static final Logger LOGGER = LogManager.getLogger(PaymentWithRedirectionServiceImpl.class);

    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {
        try {
            PaySafeCaptureRequest request = createRequest(redirectionPaymentRequest);
            boolean isSandbox = redirectionPaymentRequest.getEnvironment().isSandbox();

            // first try
            PaymentResponse response = validatePayment(request, isSandbox);
            if (PaymentResponseSuccess.class.equals(response.getClass())) {
                return response;
            } else {
                // second try
                return validatePayment(request, isSandbox);
            }

        } catch (InvalidRequestException e) {
            LOGGER.info("unable to finalize the payment", e.getMessage());
            return PaySafeErrorHandler.getPaymentResponseFailure(e.getMessage(), FailureCause.INVALID_DATA);
        }
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        try {
            PaySafeCaptureRequest request = createRequest(transactionStatusRequest);
            boolean isSandbox = transactionStatusRequest.getEnvironment().isSandbox();

            return validatePayment(request, isSandbox);
        } catch (InvalidRequestException e) {
            LOGGER.info("unable to handle the session expiration", e.getMessage());
            return PaySafeErrorHandler.getPaymentResponseFailure(e.getMessage(), FailureCause.INVALID_DATA);
        }
    }

    /**
     * Used for test (mocking)
     *
     * @param transactionStatusRequest
     * @return
     * @throws InvalidRequestException
     */
    public PaySafeCaptureRequest createRequest(TransactionStatusRequest transactionStatusRequest) throws InvalidRequestException {
        return new PaySafeCaptureRequest(transactionStatusRequest);
    }

    /**
     * Used for test (mocking)
     *
     * @param redirectionPaymentRequest
     * @return
     * @throws InvalidRequestException
     */
    public PaySafeCaptureRequest createRequest(RedirectionPaymentRequest redirectionPaymentRequest) throws InvalidRequestException {
        return new PaySafeCaptureRequest(redirectionPaymentRequest);
    }

    private PaymentResponse getErrorFromStatus(String status) {
        switch (status) {
            case PaySafeCardConstants.STATUS_CANCELED_CUSTOMER:
                return PaySafeErrorHandler.getPaymentResponseFailure(status, FailureCause.CANCEL);
            case PaySafeCardConstants.STATUS_CANCELED_MERCHANT:
                return PaySafeErrorHandler.getPaymentResponseFailure(status, FailureCause.CANCEL);
            case PaySafeCardConstants.STATUS_EXPIRED:
                return PaySafeErrorHandler.getPaymentResponseFailure(status, FailureCause.SESSION_EXPIRED);
            default:
                return PaySafeErrorHandler.getPaymentResponseFailure(status, FailureCause.PARTNER_UNKNOWN_ERROR);
        }
    }

    private PaymentResponseSuccess createResponseSuccess(PaySafePaymentResponse response) {
        String email = DEFAULT_EMAIL;
        if (!DataChecker.isEmpty(response.getCustomer().getEmail())) {
            email = response.getCustomer().getEmail();
        }

        return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                .withStatusCode(DEFAULT_SUCCESS_STATUS_CODE)
                .withPartnerTransactionId(response.getId())
                .withTransactionDetails(Email.EmailBuilder.anEmail().withEmail(email).build())
                .withTransactionAdditionalData(response.getId())
                .build();
    }

    private PaymentResponse validatePayment(PaySafeCaptureRequest request, boolean isSandbox) {
        try {
            // retrieve payment data
            final PaySafeHttpClient httpClient = getHttpClientInstance(request);
            PaySafePaymentResponse response = httpClient.retrievePaymentData(request, isSandbox);
            if (response.getCode() != null) {
                return PaySafeErrorHandler.findError(response);
            } else {
                // check if the payment has to be captured
                if (PaySafeCardConstants.STATUS_AUTHORIZED.equals(response.getStatus())) {
                    response = httpClient.capture(request, isSandbox);
                }

                if (response.getCode() != null) {
                    return PaySafeErrorHandler.findError(response);
                }
                // check if the payment is well captured
                if (PaySafeCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {
                    return createResponseSuccess(response);
                } else {
                    return getErrorFromStatus(response.getStatus());
                }
            }
        } catch (IOException | URISyntaxException e) {
            LOGGER.error("unable to validate the payment", e);
            return PaySafeErrorHandler.getPaymentResponseFailure(e.getMessage(), FailureCause.COMMUNICATION_ERROR);
        }
    }

    protected PaySafeHttpClient getHttpClientInstance(final PaySafeCaptureRequest request) {
        return PaySafeHttpClient.getInstance(request.getPartnerConfiguration());
    }
}
