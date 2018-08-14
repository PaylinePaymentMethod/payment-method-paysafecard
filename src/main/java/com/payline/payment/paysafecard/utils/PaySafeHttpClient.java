package com.payline.payment.paysafecard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.bean.PaySafeRequest;
import okhttp3.*;

import java.io.IOException;

public class PaySafeHttpClient extends AbstractHttpClient {
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String AUTHENTICATION_KEY = "Authorization";
    private static final String CONTENT_TYPE = "application/json";
    private OkHttpClient client;
    private Gson parser;


    public PaySafeHttpClient() {
        this.parser = new GsonBuilder().create();
        this.client = new OkHttpClient.Builder().build();
    }

    public PaySafePaymentResponse doPost(String host, String path, PaySafeRequest request) throws IOException {
        String jsonBody = parser.toJson(request);
        RequestBody body = RequestBody.create(MediaType.parse(CONTENT_TYPE), jsonBody);

        // create url
        HttpUrl url = new HttpUrl.Builder()
                .scheme("https")
                .host(host)
                .addPathSegment("v1")
                .addPathSegment(path)
                .build();

        // create request
        Request httpRequest = new Request.Builder()
                .url(url)
                .post(body)
                .addHeader(CONTENT_TYPE_KEY, CONTENT_TYPE)
                .addHeader(AUTHENTICATION_KEY, request.getAuthentHeader())
                .build();

        // do the request
        Response response = client.newCall(httpRequest).execute();

        // create object from PaySafeCard response
        return parser.fromJson(response.body().string(), PaySafePaymentResponse.class);
    }
}
