package com.payline.payment.paysafecard.bean;

import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.configuration.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

public class PaySafePaymentRequest extends PaySafeRequest {
    private final String type = "PAYSAFECARD";
    private String amount;
    private String currency;
    private Redirect redirect;
    private String notification_url;
    private Customer customer;
    private String submerchant_id;
    private String shop_id;

    public PaySafePaymentRequest(ContractParametersCheckRequest request) {
        super(request.getContractConfiguration());
        this.amount = "0.01";
        this.currency = "EUR";
        this.redirect = new Redirect(request.getPaylineEnvironment());
        this.notification_url = request.getPaylineEnvironment().getNotificationURL();

        ContractConfiguration configuration = request.getContractConfiguration();
        this.customer = new Customer("dumbId",
                configuration.getProperty(PaySafeCardConstants.MINAGE_KEY).getValue(),
                configuration.getProperty(PaySafeCardConstants.KYCLEVEL_KEY).getValue(),
                configuration.getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY).getValue());
    }

    public PaySafePaymentRequest(PaymentRequest request) {
        super(request.getContractConfiguration());

        this.amount = createAmount(request.getAmount().getAmountInSmallestUnit().intValue());
        this.currency = request.getAmount().getCurrency().getCurrencyCode();

        this.redirect = new Redirect(request.getPaylineEnvironment());
        this.notification_url = request.getPaylineEnvironment().getNotificationURL();

        this.redirect = new Redirect(request.getPaylineEnvironment());
        this.notification_url = request.getPaylineEnvironment().getNotificationURL();

        ContractConfiguration configuration = request.getContractConfiguration();
        this.customer = new Customer("dumbId",
                configuration.getProperty(PaySafeCardConstants.MINAGE_KEY).getValue(),
                configuration.getProperty(PaySafeCardConstants.KYCLEVEL_KEY).getValue(),
                configuration.getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY).getValue());
    }

    private String createAmount(int amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - 2, ".");
        return sb.toString();
    }

}
