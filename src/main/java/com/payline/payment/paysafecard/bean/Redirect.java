package com.payline.payment.paysafecard.bean;

import com.payline.pmapi.bean.payment.PaylineEnvironment;

public class Redirect {
    private String success_url;
    private String failure_url;
    private String auth_url;

    public Redirect(){

    }

    public Redirect(PaylineEnvironment environment) {
        this.success_url = environment.getRedirectionReturnURL();
        this.failure_url = environment.getRedirectionCancelURL();
    }

    public String getSuccess_url() {
        return success_url;
    }

    public void setSuccess_url(String success_url) {
        this.success_url = success_url;
    }

    public String getFailure_url() {
        return failure_url;
    }

    public void setFailure_url(String failure_url) {
        this.failure_url = failure_url;
    }

    public String getAuth_url() {
        return auth_url;
    }

    public void setAuth_url(String auth_url) {
        this.auth_url = auth_url;
    }
}
