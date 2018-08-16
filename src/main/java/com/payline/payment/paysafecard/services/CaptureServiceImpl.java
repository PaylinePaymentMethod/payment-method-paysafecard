package com.payline.payment.paysafecard.services;

import com.payline.pmapi.bean.capture.request.CaptureRequest;
import com.payline.pmapi.bean.capture.response.CaptureResponse;
import com.payline.pmapi.service.CaptureService;

public class CaptureServiceImpl implements CaptureService {
    @Override
    public CaptureResponse captureRequest(CaptureRequest captureRequest) {
        return null;
    }

    @Override
    public boolean canMultiple() {
        return false;
    }

    @Override
    public boolean canPartial() {
        return false;
    }
}
