package com.payline.payment.paysafecard.services;

import com.payline.pmapi.bean.paymentform.bean.form.NoFieldForm;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;

import static com.payline.payment.paysafecard.utils.PaySafeCardConstants.NO_FIELD_DESCRIPTION;
import static com.payline.payment.paysafecard.utils.PaySafeCardConstants.NO_FIELD_TEXT;

public class PaymentFormConfigurationServiceImpl implements DefaultPaymentFormConfigurationService {
    private static final boolean DISPLAY_PAYMENT_BUTTON = true;

    /**
     * Build a new PaymentFormConfigurationResponse
     *
     * @param paymentFormConfigurationRequest
     * @return
     */
    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        NoFieldForm noFieldForm = NoFieldForm.NoFieldFormBuilder.aNoFieldForm()
                .withDisplayButton(DISPLAY_PAYMENT_BUTTON)
                .withButtonText(i18n.getMessage(NO_FIELD_TEXT, paymentFormConfigurationRequest.getLocale()))
                .withDescription(i18n.getMessage(NO_FIELD_DESCRIPTION, paymentFormConfigurationRequest.getLocale()))
                .build();

        return PaymentFormConfigurationResponseSpecific.PaymentFormConfigurationResponseSpecificBuilder.aPaymentFormConfigurationResponseSpecific()
                .withPaymentForm(noFieldForm)
                .build();
    }
}
