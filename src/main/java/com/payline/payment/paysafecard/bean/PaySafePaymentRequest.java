package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;
import com.payline.payment.paysafecard.utils.DataChecker;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.payment.paysafecard.utils.PaySafeCardConstants;
import com.payline.pmapi.bean.common.Amount;
import com.payline.pmapi.bean.common.Buyer;
import com.payline.pmapi.bean.configuration.request.ContractParametersCheckRequest;
import com.payline.pmapi.bean.payment.ContractConfiguration;
import com.payline.pmapi.bean.payment.PaylineEnvironment;
import com.payline.pmapi.bean.payment.request.PaymentRequest;
import com.payline.pmapi.bean.refund.request.RefundRequest;


public class PaySafePaymentRequest extends PaySafeRequest {
    private String type = "PAYSAFECARD";
    private String amount;
    private String currency;
    private Redirect redirect;
    @SerializedName("notification_url")
    private String notificationUrl;
    private Customer customer;
    @SerializedName("submerchant_id")
    private String submerchantId;
    @SerializedName("shop_id")
    private String shopId;

    @Expose(serialize = false, deserialize = false)
    private String paymentId;
    private boolean capture;


    public PaySafePaymentRequest(ContractParametersCheckRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.amount = "0.01";
        this.currency = "EUR";

        setCustomer("dumbId", request.getContractConfiguration());
    }

    public PaySafePaymentRequest(PaymentRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());

        setAmount(request.getAmount());
        setCurrency(request.getAmount());
        setUrls(request.getPaylineEnvironment());

        Buyer buyer = request.getBuyer();
        ContractConfiguration configuration = request.getContractConfiguration();
        if (buyer == null || buyer.getCustomerIdentifier() == null) {
            throw new InvalidRequestException("PaySafeRequest must have a customerId key when created");
        } else {
            // get non mandatory object
            setCustomer(buyer.getCustomerIdentifier(), configuration);
        }
    }

    public PaySafePaymentRequest(RefundRequest request) throws InvalidRequestException {
        super(request.getContractConfiguration());
        this.capture = false;

        setAmount(request.getAmount());
        setCurrency(request.getAmount());
        setUrls(request.getPaylineEnvironment());

        Buyer buyer = request.getBuyer();
        if (buyer == null || buyer.getCustomerIdentifier() == null) {
            throw new InvalidRequestException("PaySafeRequest must have a customerId key when created");
        } else {
            this.customer = new Customer(buyer.getCustomerIdentifier(), buyer.getEmail());
        }
    }

    private void setAmount(Amount amount) throws InvalidRequestException {
        if (amount == null || amount.getAmountInSmallestUnit() == null) {
            throw new InvalidRequestException("PaySafeRequest must have an amount when created");
        } else {
            this.amount = createAmount(amount.getAmountInSmallestUnit().intValue());
        }
    }

    private void setCurrency(Amount amount) throws InvalidRequestException {
        if (amount.getCurrency() == null) {
            throw new InvalidRequestException("PaySafeRequest must have a currency when created");
        } else {
            this.currency = amount.getCurrency().getCurrencyCode();
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

    private void setCustomer(String id, ContractConfiguration config) throws InvalidRequestException {
        String minAge = config.getProperty(PaySafeCardConstants.MINAGE_KEY) != null ? config.getProperty(PaySafeCardConstants.MINAGE_KEY).getValue() : null;
        if (DataChecker.isEmpty(minAge)) {
            minAge = null;
        }

        String kycLevel = config.getProperty(PaySafeCardConstants.KYCLEVEL_KEY) != null ? config.getProperty(PaySafeCardConstants.KYCLEVEL_KEY).getValue() : null;
        if (DataChecker.isEmpty(kycLevel)) {
            kycLevel = null;
        }

        String countryRestriction = config.getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY) != null ? config.getProperty(PaySafeCardConstants.COUNTRYRESTRICTION_KEY).getValue() : null;
        if (DataChecker.isEmpty(countryRestriction)) {
            countryRestriction = null;
        }

        // verify fields
        DataChecker.verifyMinAge(minAge);
        DataChecker.verifyCountryRestriction(countryRestriction);
        this.customer = new Customer(id, minAge, kycLevel, countryRestriction);
    }


    /**
     * create a String amount from a int amount
     *
     * @param amount
     * @return a string under the form xx.xx
     */
    public static String createAmount(int amount) {
        StringBuilder sb = new StringBuilder();
        sb.append(amount);

        for (int i = sb.length(); i < 3; i++) {
            sb.insert(0, "0");
        }

        sb.insert(sb.length() - 2, ".");
        return sb.toString();
    }

    public String getPaymentId() {
        return paymentId;
    }

    public void setCapture(boolean capture) {
        this.capture = capture;
    }

}
