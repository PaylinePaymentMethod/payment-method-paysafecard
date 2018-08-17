package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.PaymentResponseFailure;
import com.payline.pmapi.bean.payment.response.PaymentResponseRedirect.PaymentResponseRedirectBuilder;
import com.payline.pmapi.bean.payment.response.PaymentResponseRedirect.RedirectionRequest;
import com.payline.pmapi.service.PaymentService;

import java.io.IOException;
import java.net.URL;

public class PaymentServiceImpl implements PaymentService {
    private PaySafeHttpClient httpClient = new PaySafeHttpClient();

    @Override
    public PaymentResponse paymentRequest(PaymentRequest paymentRequest) {
        // create the PaySAfeCard payment request
        PaySafePaymentRequest request = new PaySafePaymentRequest(paymentRequest);

        Boolean isSandbox = paymentRequest.getPaylineEnvironment().isSandbox();
        String url = isSandbox ? PaySafeCardConstants.SANDBOX_URL : PaySafeCardConstants.PRODUCTION_URL;

        try {
            PaySafePaymentResponse response = httpClient.doPost(url, PaySafeCardConstants.PATH, request);

            // check response object
            if (response.getCode() != null) {
                return findError(response);
            } else {
                // get the url to get
                URL redirectURL = new URL(response.getRedirectURL());
                RedirectionRequest redirectionRequest = new RedirectionRequest(redirectURL);

                return PaymentResponseRedirectBuilder.aPaymentResponseRedirect().withRedirectionRequest(redirectionRequest).build();
            }

        } catch (IOException e) {
            return getPaymentResponseFailure(e.getMessage(), FailureCause.INTERNAL_ERROR);
        }
    }

    private PaymentResponseFailure getPaymentResponseFailure(String errorCode, final FailureCause failureCause) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .withErrorCode(errorCode).build();
    }

    private PaymentResponseFailure findError(PaySafePaymentResponse message) {
        FailureCause cause;
        if (message.getNumber() == null) {
            cause = FailureCause.PARTNER_UNKNOWN_ERROR;
            // unknown error
        } else {
            switch (message.getNumber()) {
                case "10007":
                    // general_technical_error
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "10008":
                    // invalid_api_key
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "10028":
                    // invalid_request_parameter
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "2001":
                    // duplicate_transaction_id
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "2017":
                    // payment_invalid_state
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "3001":
                    // Merchant with Id XXXXXXXXXX is not active.
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "3007":
                    // Merchant with Id XXXXXXXXXX is not allowed to perform this debit any more
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "3014":
                    // submerchant_not_found
                    cause = FailureCause.INVALID_DATA;
                    break;
                default:
                    cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                    break;
            }

        }
        return getPaymentResponseFailure(message.getCode(), cause);
    }
}
