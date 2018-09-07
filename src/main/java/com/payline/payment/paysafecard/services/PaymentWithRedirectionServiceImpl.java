package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.payment.paysafecard.utils.PaySafeErrorHandler;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.PaymentResponseSuccess;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.Card;
import com.payline.pmapi.bean.payment.response.buyerpaymentidentifier.impl.CardPayment;
import com.payline.pmapi.service.PaymentWithRedirectionService;

import java.io.IOException;
import java.time.YearMonth;

public class PaymentWithRedirectionServiceImpl implements PaymentWithRedirectionService {
    private PaySafeHttpClient httpClient;

    public PaymentWithRedirectionServiceImpl() {
        httpClient = new PaySafeHttpClient();
    }


    @Override
    public PaymentResponse finalizeRedirectionPayment(RedirectionPaymentRequest redirectionPaymentRequest) {

        try {
            PaySafeCaptureRequest request = createRequest(redirectionPaymentRequest);

            boolean isSandbox = redirectionPaymentRequest.getPaylineEnvironment().isSandbox();


            PaySafePaymentResponse response = httpClient.retrievePaymentData(request, isSandbox);

            if (response.getCode() != null) {
                return PaySafeErrorHandler.findError(response);
            } else if (!PaySafeCardConstants.STATUS_AUTHORIZED.equals(response.getStatus())) {
                return getErrorFromStatus(response.getStatus());
            }

            int i = 0;
            while (i < 2) {
                response = httpClient.capture(request, isSandbox);
                if (response.getCode() != null) {
                    return PaySafeErrorHandler.findError(response);
                } else if (PaySafeCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {
                    // create successResponse object
                    Card card = Card.CardBuilder.aCard()
                            .withPan(response.getFirstCardDetails().getSerial())
                            .withExpirationDate(YearMonth.now())
                            .build();

                    CardPayment cardPayment = CardPayment.CardPaymentBuilder.aCardPayment()
                            .withCard(card)
                            .build();

                    return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withStatusCode("0")
                            .withTransactionIdentifier(response.getId())
                            .withTransactionDetails(cardPayment)
                            .build();
                }

                i++;
            }

            // 2 calls but no response with status = "SUCCESS"
            return getErrorFromStatus(response.getStatus());

        } catch (IOException | InvalidRequestException e) {
            return PaySafeErrorHandler.getPaymentResponseFailure(e.getMessage(), FailureCause.INTERNAL_ERROR);
        }
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        return PaySafeErrorHandler.getPaymentResponseFailure("timeout", FailureCause.SESSION_EXPIRED);
    }

    public PaySafeCaptureRequest createRequest(RedirectionPaymentRequest redirectionPaymentRequest) throws InvalidRequestException {
        return new PaySafeCaptureRequest(redirectionPaymentRequest);
    }

    private PaymentResponse getErrorFromStatus(String status) {
        switch (status) {
            case PaySafeCardConstants.STATUS_CANCELED_CUSTOMER:
                return PaySafeErrorHandler.getPaymentResponseFailure("canceledByCustomer", FailureCause.CANCEL);
            case PaySafeCardConstants.STATUS_CANCELED_MERCHANT:
                return PaySafeErrorHandler.getPaymentResponseFailure("canceledByMerchant", FailureCause.CANCEL);
            case PaySafeCardConstants.STATUS_EXPIRED:
                return PaySafeErrorHandler.getPaymentResponseFailure("sessionExpired", FailureCause.SESSION_EXPIRED);
            default:
                return PaySafeErrorHandler.getPaymentResponseFailure("unknown", FailureCause.PARTNER_UNKNOWN_ERROR);
        }
    }
}
