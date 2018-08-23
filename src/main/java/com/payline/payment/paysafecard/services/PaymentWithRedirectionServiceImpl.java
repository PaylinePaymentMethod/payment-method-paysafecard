package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.payment.paysafecard.utils.PaySafeHttpClient;
import com.payline.pmapi.bean.common.FailureCause;
import com.payline.pmapi.bean.payment.request.RedirectionPaymentRequest;
import com.payline.pmapi.bean.payment.request.TransactionStatusRequest;
import com.payline.pmapi.bean.payment.response.PaymentResponse;
import com.payline.pmapi.bean.payment.response.PaymentResponseFailure;
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
        PaySafeCaptureRequest request = new PaySafeCaptureRequest(redirectionPaymentRequest);

        // retrieve payment data

        try {
            // todo attendre un MAJ  de la lib POAyline pour decommanter cette ligne
//            Boolean isSandbox = redirectionPaymentRequest.getPaylineEnvironment().isSandbox();
            boolean isSandbox = true;
            PaySafePaymentResponse response = httpClient.retrievePaymentData(request, isSandbox);
            System.out.println(response.toString());


            if (response.getCode() != null) {


            } else {
                // status must be "AUTHORIZED"
                System.out.println("status: " + response.getStatus());

                // capture payment
                response = httpClient.capture(request, isSandbox);

                // status must be success
                System.out.println("status2: " + response.getStatus());
                if (PaySafeCardConstants.STATUS_SUCCESS.equals(response.getStatus())) {
                    System.out.println(response.toString());

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
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }

        // capture the payment

        return null;
    }

    @Override
    public PaymentResponse handleSessionExpired(TransactionStatusRequest transactionStatusRequest) {
        return null;
    }

    private PaymentResponseFailure getPaymentResponseFailure(String errorCode, final FailureCause failureCause) {
        return PaymentResponseFailure.PaymentResponseFailureBuilder.aPaymentResponseFailure()
                .withFailureCause(failureCause)
                .withErrorCode(errorCode).build();
    }
}
