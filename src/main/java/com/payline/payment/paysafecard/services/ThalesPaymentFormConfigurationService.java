package com.payline.payment.paysafecard.services;


import com.payline.payment.paysafecard.utils.i18n.I18nService;
import com.payline.payment.paysafecard.utils.properties.service.LogoProperties;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import com.payline.pmapi.service.PaymentFormConfigurationService;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Locale;

import static com.payline.payment.paysafecard.utils.properties.constants.LogoConstants.*;

public interface ThalesPaymentFormConfigurationService extends PaymentFormConfigurationService {
    Logger LOGGER = LogManager.getLogger(ThalesPaymentFormConfigurationService.class);
    I18nService i18n = I18nService.getInstance();

    @Override
    default PaymentFormLogoResponse getPaymentFormLogo(PaymentFormLogoRequest paymentFormLogoRequest) {
        Locale locale = paymentFormLogoRequest.getLocale();

        return PaymentFormLogoResponseFile.PaymentFormLogoResponseFileBuilder.aPaymentFormLogoResponseFile()
                .withHeight(Integer.valueOf(LogoProperties.INSTANCE.get(LOGO_HEIGHT)))
                .withWidth(Integer.valueOf(LogoProperties.INSTANCE.get(LOGO_WIDTH)))
                .withTitle(i18n.getMessage(LogoProperties.INSTANCE.get(LOGO_TITLE), locale))
                .withAlt(i18n.getMessage(LogoProperties.INSTANCE.get(LOGO_ALT), locale))
                .build();
    }

    @Override
    default PaymentFormLogo getLogo(String s, Locale locale) {
        String fileName = LogoProperties.INSTANCE.get(LOGO_FILE_NAME);
        InputStream input = PaymentFormConfigurationServiceImpl.class.getClassLoader().getResourceAsStream(fileName);
        if (input == null) {
            LOGGER.error("Unable to load the logo {}", LOGO_FILE_NAME);
            throw new RuntimeException("Unable to load the logo " + LOGO_FILE_NAME);
        }
        try {
            // Read logo file
            BufferedImage logo = ImageIO.read(input);

            // Recover byte array from image
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ImageIO.write(logo, LogoProperties.INSTANCE.get(LOGO_FORMAT), baos);

            return PaymentFormLogo.PaymentFormLogoBuilder.aPaymentFormLogo()
                    .withFile(baos.toByteArray())
                    .withContentType(LogoProperties.INSTANCE.get(LOGO_CONTENT_TYPE))
                    .build();
        } catch (IOException e) {
            LOGGER.error("Unable to load the logo", e);
            throw new RuntimeException(e);
        }
    }
}
