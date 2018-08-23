package com.payline.payment.paysafecard.bean;

public class CardDetail {
    private String serial;
    private String type;
    private String country;
    private String currency;
    private String amount;

    public String getSerial() {
        return serial;
    }

    public void setSerial(String serial) {
        this.serial = serial;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public String getCurrency() {
        return currency;
    }

    public void setCurrency(String currency) {
        this.currency = currency;
    }

    public String getAmount() {
        return amount;
    }

    public void setAmount(String amount) {
        this.amount = amount;
    }

    @Override
    public String toString() {
        return "CardDetail{" +
                "serial='" + serial + '\'' +
                ", type='" + type + '\'' +
                ", country='" + country + '\'' +
                ", currency='" + currency + '\'' +
                ", amount='" + amount + '\'' +
                '}';
    }
}
