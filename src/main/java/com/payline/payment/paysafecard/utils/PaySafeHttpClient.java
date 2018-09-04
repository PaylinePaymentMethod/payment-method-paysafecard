package com.payline.payment.paysafecard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.bean.PaySafeRequest;
import okhttp3.*;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class PaySafeHttpClient {
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String AUTHENTICATION_KEY = "Authorization";
    private static final String CONTENT_TYPE = "application/json";
    private OkHttpClient client;
    private Gson parser;


    public PaySafeHttpClient() {
        this.parser = new GsonBuilder().create();
        this.client = new OkHttpClient.Builder().build();
    }

    public String getHost(boolean isSandbox) {
        return isSandbox ? PaySafeCardConstants.SANDBOX_URL : PaySafeCardConstants.PRODUCTION_URL;
    }

    public HttpUrl createUrl(String host, String... path) {
        HttpUrl.Builder builder = new HttpUrl.Builder()
                .scheme("https")
                .host(host);

        if (path != null && path.length > 0) {
            for (String aPath : path) {
                builder.addPathSegment(aPath);
            }
        }

        return builder.build();
    }

    private Map<String, String> createHeaders(String authentication) {
        Map<String, String> headers = new HashMap<>();
        headers.put(CONTENT_TYPE_KEY, CONTENT_TYPE);
        headers.put(AUTHENTICATION_KEY, authentication);

        return headers;
    }

    private Request.Builder createRequestBuilder(HttpUrl url, Map<String, String> headers) {
        Request.Builder requestBuilder = new Request.Builder().url(url);
        for (Map.Entry<String, String> entry : headers.entrySet()) {
            requestBuilder.addHeader(entry.getKey(), entry.getValue());
        }

        return requestBuilder;
    }

    private Request createRequest(HttpUrl url, Map<String, String> headers) {
        return createRequestBuilder(url, headers).build();
    }

    private Request createRequest(HttpUrl url, Map<String, String> headers, RequestBody body) {
        return createRequestBuilder(url, headers).post(body).build();
    }

    private Response doGet(HttpUrl url, Map<String, String> headers) throws IOException {
        Request request = createRequest(url, headers);

        return client.newCall(request).execute();
    }

    private Response doPost(HttpUrl url, Map<String, String> headers, RequestBody body) throws IOException {
        Request request = createRequest(url, headers, body);

        // do the request
        return client.newCall(request).execute();
    }


    public PaySafePaymentResponse initiate(PaySafeRequest request, boolean isSandbox) throws IOException {
        String host = getHost(isSandbox);
        HttpUrl url = createUrl(host, "v1", PaySafeCardConstants.PATH);

        String jsonBody = parser.toJson(request);
        RequestBody body = RequestBody.create(MediaType.parse(CONTENT_TYPE), jsonBody);

        Map<String, String> headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        Response response = doPost(url, headers, body);

        // create object from PaySafeCard response
        return parser.fromJson(response.body().string(), PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse retrievePaymentData(PaySafeCaptureRequest request, boolean isSandbox) throws IOException {
        String host = getHost(isSandbox);
        HttpUrl url = createUrl(host, PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId());
        Map<String, String> headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        Response response = doGet(url, headers);
        return parser.fromJson(response.body().string(), PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse capture(PaySafeCaptureRequest request, boolean isSandbox) throws IOException {
        String host = getHost(isSandbox);
        HttpUrl url = createUrl(host, PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId(), PaySafeCardConstants.PATH_CAPTURE);

        RequestBody body = RequestBody.create(null, "");

        Map<String, String> headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        Response response = doPost(url, headers, body);

        // create object from PaySafeCard response
        return parser.fromJson(response.body().string(), PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse refund(PaySafePaymentRequest request, boolean isSandbox) throws IOException {
        String host = getHost(isSandbox);
        HttpUrl url = createUrl(host, PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId(), PaySafeCardConstants.PATH_REFUND);

        String jsonBody = parser.toJson(request);
        RequestBody body = RequestBody.create(MediaType.parse(CONTENT_TYPE), jsonBody);

        Map<String, String> headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        Response response = doPost(url, headers, body);

        // create object from PaySafeCard response
        return parser.fromJson(response.body().string(), PaySafePaymentResponse.class);
    }

}
