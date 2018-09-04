package com.payline.payment.paysafecard.utils;

import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.response.PaymentResponseFailure;
import com.payline.pmapi.bean.refund.response.RefundResponse;
import com.payline.pmapi.bean.refund.response.impl.RefundResponseFailure;

public class PaySafeErrorHandler {
    public static PaymentResponseFailure findError(PaySafePaymentResponse response) {
        FailureCause cause;
        if (response.getNumber() == null) {
            cause = FailureCause.PARTNER_UNKNOWN_ERROR;
            // unknown error
        } else {
            switch (response.getNumber()) {
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
        return getPaymentResponseFailure(response.getCode(), cause);
    }

    public static PaymentResponseFailure getPaymentResponseFailure(String errorCode, final FailureCause failureCause) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .withErrorCode(errorCode).build();
    }

    public static RefundResponse findRefundError(PaySafePaymentResponse response, String transactionId) {
        FailureCause cause;
        if (response.getNumber() == null) {
            cause = FailureCause.PARTNER_UNKNOWN_ERROR;
            // unknown error
        } else {
            switch (response.getNumber()) {
                case "3100":
                    // Product not available.
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "3103":
                    // Duplicate order request.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3106":
                    // Invalid facevalue format.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3150":
                    // Missing paramenter.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3151":
                    // Invalid currency.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3161":
                    // Merchant not allowed to perform this Action.
                    cause = FailureCause.REFUSED;
                    break;
                case "3162":
                    // No customer account found by provided credentials.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3163":
                    // Invalid paramater.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3164":
                    // Transaction already exists.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3165":
                    // Invalid amount.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3167":
                    // Customer limit exceeded.
                    cause = FailureCause.REFUSED;
                    break;
                case "3168":
                    // Feature not activated in this country for this kyc Level.
                    cause = FailureCause.REFUSED;
                    break;
                case "3169":
                    // Payout id collides with existing disposition id
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3170":
                    // Top-up limit exceeded.
                    cause = FailureCause.REFUSED;
                    break;
                case "3171":
                    // Payout amount is below minimum payout amount of the merchant.
                    cause = FailureCause.REFUSED;
                    break;
                case "3179":
                    // Merchant refund exceeds original transaction.
                    cause = FailureCause.REFUSED;
                    break;
                case "3180":
                    // Original Transaction of Merchant Refund is in invalid state.
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "3181":
                    // Merchant Client Id not matching with original Payment.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3182":
                    // merchant client Id missing.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3184":
                    // No original Transaction found.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3185":
                    // my paysafecard account not found on original transaction and no additional credentials provided.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3193":
                    // Customer not active.
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "3194":
                    // Customer yearly payout limit exceeded.
                    cause = FailureCause.REFUSED;
                    break;
                case "3195":
                    // Customer details from request don't match with database.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "3198":
                    // There is already the maximum number of pay-out merchant clients assigned to this account.
                    cause = FailureCause.REFUSED;
                    break;
                case "3199":
                    // Payout blocked due to security reasons.
                    cause = FailureCause.FRAUD_DETECTED;
                    break;
                default:
                    cause = FailureCause.PARTNER_UNKNOWN_ERROR;
                    break;
            }
        }
        return getRefundResponseFailure(response.getCode(), cause, transactionId);
    }

    public static RefundResponseFailure getRefundResponseFailure(String errorCode, final FailureCause failureCause, String transactionId) {
        return RefundResponseFailure.RefundResponseFailureBuilder.aRefundResponseFailure()
                .withErrorCode(errorCode)
                .withFailureCause(failureCause)
                .withTransactionId(transactionId)
                .build();
    }
}
