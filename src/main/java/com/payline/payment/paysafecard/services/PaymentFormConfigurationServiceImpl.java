package com.payline.payment.paysafecard.services;

import com.payline.payment.paysafecard.utils.LocalizationImpl;
import com.payline.payment.paysafecard.utils.LocalizationService;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponseProvided;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponseFile;
import com.payline.pmapi.service.PaymentFormConfigurationService;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Locale;

public class PaymentFormConfigurationServiceImpl implements PaymentFormConfigurationService {
    private static final String LOGO_CONTENT_TYPE = "image/png";
    private static final int LOGO_HEIGHT = 256;
    private static final int LOGO_WIDTH = 256;

    private LocalizationService localization;

    public PaymentFormConfigurationServiceImpl() {
        localization = LocalizationImpl.getInstance();
    }

    /**
     * Build a new PaymentFormConfigurationResponse
     *
     * @param paymentFormConfigurationRequest
     * @return
     */
    @Override
    public PaymentFormConfigurationResponse getPaymentFormConfiguration(PaymentFormConfigurationRequest paymentFormConfigurationRequest) {
        return PaymentFormConfigurationResponseProvided.PaymentFormConfigurationResponseBuilder.aPaymentFormConfigurationResponse().withContextPaymentForm(new HashMap<>()).build();
    }

    @Override
    public PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                .withContentType(LOGO_CONTENT_TYPE)
                .withHeight(LOGO_HEIGHT)
                .withWidth(LOGO_WIDTH)
                .withTitle(localization.getSafeLocalizedString("project.name", paymentFormLogoRequest.getLocale()))
                .withAlt(localization.getSafeLocalizedString("project.name", paymentFormLogoRequest.getLocale()))
                .build();
    }

    @Override
    public PaymentFormLogo getLogo(Locale locale) throws IOException {
        // Read logo file
        InputStream input = PaymentFormConfigurationServiceImpl.class.getClassLoader().getResourceAsStream("paysafe-logo.png");
        BufferedImage logo = ImageIO.read(input);

        // Recover byte array from image
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        ImageIO.write(logo, "png", baos);

        return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                .withFile(baos.toByteArray())
                .withContentType(LOGO_CONTENT_TYPE)
                .build();
    }
}
