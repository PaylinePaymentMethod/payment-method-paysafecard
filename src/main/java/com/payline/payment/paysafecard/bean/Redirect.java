package com.payline.payment.paysafecard.bean;

import com.payline.payment.paysafecard.utils.InvalidRequestException;
import com.payline.pmapi.bean.payment.PaylineEnvironment;

public class Redirect {
    private String success_url;
    private String failure_url;
    private String auth_url;

    Redirect(PaylineEnvironment environment) throws InvalidRequestException {
        if (environment.getRedirectionReturnURL() == null){
            throw new InvalidRequestException("PaySafeRequest must have a success url when created");
        }
        if (environment.getRedirectionReturnURL() == null){
            throw new InvalidRequestException("PaySafeRequest must have a failure url when created");
        }
        this.success_url = environment.getRedirectionReturnURL();
        this.failure_url = environment.getRedirectionCancelURL();
    }

    public String getSuccess_url() {
        return success_url;
    }

    public String getFailure_url() {
        return failure_url;
    }


    public String getAuth_url() {
        return auth_url;
    }
}
