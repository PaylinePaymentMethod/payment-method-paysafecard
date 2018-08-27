package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.configuration.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.PaylineEnvironment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;

public class PaySafePaymentRequest extends PaySafeRequest {
    private final String type = "PAYSAFECARD";
    private String amount;
    private String currency;
    private Redirect redirect;
    @SerializedName("notification_url")
    private String notificationUrl;
    private Customer customer;
    @SerializedName("submerchant_id")
    private String submerchantId;
    private String shop_id;


    public PaySafePaymentRequest(ContractParametersCheckRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.amount = "0.01";
        this.currency = "EUR";

        setUrls(request.getPaylineEnvironment());

        ContractConfiguration configuration = request.getContractConfiguration();
        this.customer = new Customer("dumbId",
                configuration.getProperty(PaySafeCardConstants.MINAGE_KEY).getValue(),
                configuration.getProperty(PaySafeCardConstants.KYCLEVEL_KEY).getValue(),
                configuration.getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY).getValue());
    }

    public PaySafePaymentRequest(PaymentRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());

        Amount requestAmount = request.getAmount();
        if (requestAmount == null || requestAmount.getAmountInSmallestUnit() == null){
            throw new InvalidRequestException("PaySafeRequest must have an amount when created");
        } else {
            this.amount = createAmount(requestAmount.getAmountInSmallestUnit().intValue());
        }

        if (requestAmount.getCurrency() == null){
            throw new InvalidRequestException("PaySafeRequest must have a currency when created");
        } else {
            this.currency = requestAmount.getCurrency().getCurrencyCode();
        }

        setUrls(request.getPaylineEnvironment());

        if (request.getBuyer() == null || request.getBuyer().getCustomerIdentifier() == null) {
            throw new InvalidRequestException("PaySafeRequest must have a customerId key when created");
        } else {
            ContractConfiguration configuration = request.getContractConfiguration();

            // get non mandatory object
            String minAge = configuration.getProperty(PaySafeCardConstants.MINAGE_KEY) != null ? configuration.getProperty(PaySafeCardConstants.MINAGE_KEY).getValue() : null;
            String kycLevel = configuration.getProperty(PaySafeCardConstants.KYCLEVEL_KEY) != null ? configuration.getProperty(PaySafeCardConstants.KYCLEVEL_KEY).getValue() : null;
            String countryRestriction = configuration.getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY) != null ? configuration.getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY).getValue() : null;

            this.customer = new Customer(request.getBuyer().getCustomerIdentifier(), minAge, kycLevel, countryRestriction);
        }
    }

    private void setUrls(PaylineEnvironment environment) throws InvalidRequestException {
        if (environment == null) {
            throw new InvalidRequestException("PaySafeRequest must have a redirect key when created");
        } else {
            this.redirect = new Redirect(environment);
            this.notificationUrl = environment.getNotificationURL();
        }
    }

    public static String createAmount(int amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - 2, ".");
        return sb.toString();
    }

}
