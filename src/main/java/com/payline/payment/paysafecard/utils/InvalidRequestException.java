package com.payline.payment.paysafecard.utils;

public class InvalidRequestException extends  Exception{
    public InvalidRequestException(String s) {
        super(s);
    }
}