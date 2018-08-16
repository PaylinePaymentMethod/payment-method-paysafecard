package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.SerializedName;

import java.net.URL;

public class PaySafePaymentResponse {
    // fields used in payment response
    private String object;
    private String id;
    private String created;
    private String updated;
    private String amount;
    private String currency;
    private String status;
    private String type;
    private Redirect redirect;
    private Customer customer;
    @SerializedName("notificationUrl") private String notificationUrl;
    @SerializedName("subMerchantId") private String subMerchantId;

    // fields used in error response
    private String code;
    private String message;
    private String number;
    private String param;

    public String getObject() {
        return object;
    }

    public String getId() {
        return id;
    }

    public String getCreated() {
        return created;
    }

    public String getUpdated() {
        return updated;
    }

    public String getAmount() {
        return amount;
    }

    public String getCurrency() {
        return currency;
    }

    public String getStatus() {
        return status;
    }

    public String getType() {
        return type;
    }

    public Redirect getRedirect() {
        return redirect;
    }

    public Customer getCustomer() {
        return customer;
    }

    public String getNotificationUrl() {
        return notificationUrl;
    }

    public String getSubMerchantId() {
        return subMerchantId;
    }

    public String getCode() {
        return code;
    }

    public String getMessage() {
        return message;
    }

    public String getNumber() {
        return number;
    }

    public String getParam() {
        return param;
    }

    @Override
    public String toString() {
        return "PaySafePaymentResponse{" +
                "object='" + object + '\'' +
                ", id='" + id + '\'' +
                ", created='" + created + '\'' +
                ", updated='" + updated + '\'' +
                ", amount='" + amount + '\'' +
                ", currency='" + currency + '\'' +
                ", status='" + status + '\'' +
                ", type='" + type + '\'' +
                ", redirect=" + redirect +
                ", customer=" + customer +
                ", notificationUrl='" + notificationUrl + '\'' +
                ", subMerchantId='" + subMerchantId + '\'' +
                ", code='" + code + '\'' +
                ", message='" + message + '\'' +
                ", number='" + number + '\'' +
                ", param='" + param + '\'' +
                '}';
    }

    public String getRedirectURL() {
        return this.getRedirect().getAuth_url();
    }
}
