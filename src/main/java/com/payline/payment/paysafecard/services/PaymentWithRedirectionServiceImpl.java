package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeErrorHandler;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
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
            } else {
                // status must be "AUTHORIZED"

                // capture payment
                response = httpClient.capture(request, isSandbox);

                // status must be success
                if (PaySafeCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {

                    // return success response to payline
                    Card card = Card.CardBuilder.aCard()
                            .withPan(response.getFirstCardDetails().getSerial())
                            .withExpirationDate(YearMonth.now())
                            .build();

                    CardPayment cardPayment = CardPayment.CardPaymentBuilder.aCardPayment()
                            .withCard(card)
//                            .withAuthorizationNumber()
                            .build();

                    return PaymentResponseSuccess.PaymentResponseSuccessBuilder.aPaymentResponseSuccess()
                            .withStatusCode("0")
                            .withTransactionIdentifier(response.getId())
                            .withTransactionDetails(cardPayment)
                            .build();
                } else {
                    return PaySafeErrorHandler.findError(response);
                }
            }
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
}
