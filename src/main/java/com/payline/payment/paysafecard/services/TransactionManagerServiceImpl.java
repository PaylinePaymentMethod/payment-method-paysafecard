package com.payline.payment.paysafecard.services;

import com.payline.pmapi.service.TransactionManagerService;

import java.util.HashMap;
import java.util.Map;

public class TransactionManagerServiceImpl implements TransactionManagerService {

    public static final String PARTNER_TRANSACTION_ID = "partnerTransactionId";

    @Override
    public Map<String, String> readAdditionalData(final String s, final String s1) {
        final Map<String, String> additionalData = new HashMap<>();
        additionalData.put(PARTNER_TRANSACTION_ID, s);
        return additionalData;
    }
}
