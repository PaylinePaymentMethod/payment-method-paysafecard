package com.payline.payment.paysafecard.utils;

import com.payline.payment.paysafecard.utils.PaySafeErrorHandler;
import org.junit.Test;
import org.junit.jupiter.api.Assertions;

public class PaySafeErrorHandlerTest {

    @Test
    public void truncate(){
        int length = 20;
        String s1 = PaySafeErrorHandler.truncate("short message", length);
        String s2 = PaySafeErrorHandler.truncate("this is a way too long message", length);
        String s3 = PaySafeErrorHandler.truncate("", length);
        String s4 = PaySafeErrorHandler.truncate(null, length);

        Assertions.assertTrue(s1.length() <= length);
        Assertions.assertTrue(s2.length() <= length);
        Assertions.assertTrue(s3.length() <= length);
        Assertions.assertTrue(s4.length() <= length);
    }
}
