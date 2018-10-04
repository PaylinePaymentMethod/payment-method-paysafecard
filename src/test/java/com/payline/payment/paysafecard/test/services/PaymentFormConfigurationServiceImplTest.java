package com.payline.payment.paysafecard.test.services;


import com.payline.payment.paysafecard.services.PaymentFormConfigurationServiceImpl;
import com.payline.payment.paysafecard.test.Utils;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.PaylineEnvironment;
import com.payline.pmapi.bean.paymentform.bean.PaymentFormLogo;
import com.payline.pmapi.bean.paymentform.request.PaymentFormConfigurationRequest;
import com.payline.pmapi.bean.paymentform.request.PaymentFormLogoRequest;
import com.payline.pmapi.bean.paymentform.response.configuration.PaymentFormConfigurationResponse;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseProvided;
import com.payline.pmapi.bean.paymentform.response.configuration.impl.PaymentFormConfigurationResponseSpecific;
import com.payline.pmapi.bean.paymentform.response.logo.PaymentFormLogoResponse;
import com.payline.pmapi.bean.paymentform.response.logo.impl.PaymentFormLogoResponseFile;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.runners.MockitoJUnitRunner;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.HashMap;
import java.util.Locale;

import static com.payline.payment.paysafecard.test.Utils.createDefaultContractConfiguration;
import static com.payline.payment.paysafecard.test.Utils.createDefaultPaymentFormConfigurationRequest;
import static org.mockito.Mockito.mock;


@RunWith(MockitoJUnitRunner.class)
public class PaymentFormConfigurationServiceImplTest {

    @InjectMocks
    private PaymentFormConfigurationServiceImpl service;

    @Test
    public void testGetPaymentFormConfiguration() {
        // when: getPaymentFormConfiguration is called
         PaymentFormConfigurationResponse response = service.getPaymentFormConfiguration(createDefaultPaymentFormConfigurationRequest());

        // then: returned object is an instance of PaymentFormConfigurationResponseSpecific
        Assert.assertTrue(response instanceof PaymentFormConfigurationResponseSpecific);
    }

    @Test
    public void testGetLogo() throws IOException {
        // when: getLogo is called
        String paymentMethodIdentifier= "PaySafeCard" ;
        PaymentFormLogo paymentFormLogo = service.getLogo(paymentMethodIdentifier,Locale.getDefault());

        // then: returned elements are not null
        Assert.assertNotNull(paymentFormLogo);
        Assert.assertNotNull(paymentFormLogo.getFile());
        Assert.assertNotNull(paymentFormLogo.getContentType());
    }

    @Test
    public void testGetPaymentFormLogo() throws IOException {
        // given: the logo image read from resources
        String filename = "paysafecard.png";
        InputStream input = PaymentFormConfigurationServiceImpl.class.getClassLoader().getResourceAsStream(filename);
        BufferedImage image = ImageIO.read(input);
        String guessedContentType = Files.probeContentType(new File(filename).toPath());
        PaylineEnvironment environment = new PaylineEnvironment("http://notificationURL.com", "http://redirectionURL.com", "http://redirectionCancelURL.com", true);
        ContractConfiguration contractConfiguration = createDefaultContractConfiguration();
        PartnerConfiguration partnerConfiguration = new PartnerConfiguration(new HashMap<>(),new HashMap<>());
        // when: getPaymentFormLogo is called
        PaymentFormLogoRequest request = PaymentFormLogoRequest.PaymentFormLogoRequestBuilder.aPaymentFormLogoRequest()
                .withLocale(Locale.getDefault())
                .withPaylineEnvironment(environment)
                .withContractConfiguration(contractConfiguration)
                .withPartnerConfiguration(partnerConfiguration)
                .build();
        PaymentFormLogoResponse paymentFormLogoResponse = service.getPaymentFormLogo(request);

        // then: returned elements match the image file data
        Assert.assertTrue(paymentFormLogoResponse instanceof PaymentFormLogoResponseFile);
        PaymentFormLogoResponseFile casted = (PaymentFormLogoResponseFile) paymentFormLogoResponse;
        Assert.assertEquals(image.getHeight(), casted.getHeight());
        Assert.assertEquals(image.getWidth(), casted.getWidth());
        Assert.assertNotNull(casted.getTitle());
        Assert.assertNotNull(casted.getAlt());
    }

}
