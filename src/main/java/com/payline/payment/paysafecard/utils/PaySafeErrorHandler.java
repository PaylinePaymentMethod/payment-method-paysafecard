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
        if (response.getCode() == null) {
            cause = FailureCause.PARTNER_UNKNOWN_ERROR;
            // unknown error
        } else {
            switch (response.getCode().toUpperCase()) {
                case "MERCHANT_REFUND_CLIENT_ID_NOT_MATCHING":
                case "NO_UNLOAD_MERCHANT_CONFIGURED":
                case "MERCHANT_REFUND_MISSING_TRANSACTION":
                case "MERCHANT_REFUND_CUSTOMER_CREDENTIALS_MISSING":
                case "DUPLICATE_ORDER_REQUEST":
                case "FACEVALUE_FORMAT_ERROR":
                case "MISSING_PARAMETER":
                case "INVALID_CURRENCY":
                case "CUSTOMER_NOT_FOUND":
                case "INVALID_PARAMETER":
                case "DUPLICATE_PAYOUT_REQUEST":
                case "PAYOUT_ID_COLLISION":
                case "CUSTOMER_DETAILS_MISMATCHD":
                case "INVALID_AMOUNT":
                    // Invalid amount.
                    cause = FailureCause.INVALID_DATA;
                    break;
                case "CUSTOMER_LIMIT_EXCEEDED":
                case "KYC_INVALID_FOR_PAYOUT_CUSTOMER":
                case "TOPUP_LIMIT_EXCEEDED":
                case "PAYOUT_AMOUNT_BELOW_MINIMUM":
                case "MERCHANT_NOT_ALLOWED_FOR_PAYOUT":
                case "MERCHANT_REFUND_EXCEEDS_ORIGINAL_TRANSACTION":
                case "CUSTOMER_YEARLY_PAYOUT_LIMIT_REACHED":
                case "MAX_AMOUNT_OF_PAYOUT_MERCHANTS_REACHED":
                    // There is already the maximum number of pay-out merchant clients assigned to this account.
                    cause = FailureCause.REFUSED;
                    break;
                case "PRODUCT_NOT_AVAILABLE":
                case "MERCHANT_REFUND_ORIGINAL_TRANSACTION_INVALID_STATE":
                case "CUSTOMER_INACTIVE":
                    // Customer not active.
                    cause = FailureCause.PAYMENT_PARTNER_ERROR;
                    break;
                case "PAYOUT_BLOCKED":
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
