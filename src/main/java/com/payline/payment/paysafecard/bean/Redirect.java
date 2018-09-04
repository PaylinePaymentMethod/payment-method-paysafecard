package com.payline.payment.paysafecard.bean;

import com.google.gson.annotations.SerializedName;
import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.pmapi.bean.payment.PaylineEnvironment;

public class Redirect {
    @SerializedName("success_url")
    private String successUrl;
    @SerializedName("failure_url")
    private String failureUrl;
    @SerializedName("auth_url")
    private String authUrl;

    Redirect(PaylineEnvironment environment) throws InvalidRequestException {
        if (environment.getRedirectionReturnURL() == null) {
            throw new InvalidRequestException("PaySafeRequest must have a success url when created");
        }
        if (environment.getRedirectionCancelURL() == null) {
            throw new InvalidRequestException("PaySafeRequest must have a failure url when created");
        }
        this.successUrl = environment.getRedirectionReturnURL();
        this.failureUrl = environment.getRedirectionCancelURL();
    }

    public String getAuth_url() {
        return authUrl;
    }
}
