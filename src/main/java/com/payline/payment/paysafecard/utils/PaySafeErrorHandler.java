package com.payline.payment.paysafecard.utils;

import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.PaymentResponseFailure;

public class PaySafeErrorHandler {
    public static PaymentResponseFailure findError(PaySafePaymentResponse message) {
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
                    // Merchant with Id XX is not active.
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "3007":
                    // Merchant with Id XX is not allowed to perform this debit any more
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

    public static PaymentResponseFailure getPaymentResponseFailure(String errorCode, final FailureCause failureCause) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .withErrorCode(errorCode).build();
    }
}
