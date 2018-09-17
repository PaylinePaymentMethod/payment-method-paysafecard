package com.payline.payment.paysafecard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.bean.PaySafeRequest;
import org.apache.http.Header;
import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

public class PaySafeHttpClient {
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String AUTHENTICATION_KEY = "Authorization";
    private static final String CONTENT_TYPE = "application/json";
    HttpClient client;// = HttpClients.createDefault();
    private Gson parser;


    public PaySafeHttpClient() {
        this.parser = new GsonBuilder().create();

        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(2 * 1000)
                .setConnectionRequestTimeout(3 * 1000)
                .setSocketTimeout(4 * 1000).build();

        final HttpClientBuilder builder = HttpClientBuilder.create();
        builder.useSystemProperties()
                .setDefaultRequestConfig(requestConfig)
                .setDefaultCredentialsProvider(new BasicCredentialsProvider())
                .setSSLSocketFactory(new SSLConnectionSocketFactory(HttpsURLConnection.getDefaultSSLSocketFactory(), SSLConnectionSocketFactory.getDefaultHostnameVerifier()));
        this.client = builder.build();
    }

    public String getHost(boolean isSandbox) {
        return isSandbox ? PaySafeCardConstants.SANDBOX_URL : PaySafeCardConstants.PRODUCTION_URL;
    }

    public String createPath(String... path) {
        String url = "/";

        if (path != null && path.length > 0) {
            for (String aPath : path) {
                url +=   aPath +"/";
            }
        }

        return url;
    }

    //
    private Header[] createHeaders(String authentication) {
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader(CONTENT_TYPE_KEY, CONTENT_TYPE);
        headers[1] = new BasicHeader(AUTHENTICATION_KEY, authentication);
        return headers;
    }


    public HttpResponse doGet(String scheme, String host, String path, Header[] headers) throws IOException, URISyntaxException {

        final URI uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path)
                .build();

        final HttpGet httpGetRequest = new HttpGet(uri);
        httpGetRequest.setHeaders(headers);
        return client.execute(httpGetRequest);
    }

    public HttpResponse doPost(String scheme, String host, String path, Header[] headers, String body) throws IOException, URISyntaxException {

        final URI uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path)
                .build();

        final HttpPost httpPostRequest = new HttpPost(uri);
        httpPostRequest.setHeaders(headers);
        httpPostRequest.setEntity(new StringEntity(body));
        return client.execute(httpPostRequest);
    }


    public PaySafePaymentResponse initiate(PaySafeRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH);
        String jsonBody = parser.toJson(request);
        Header[] headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        HttpResponse response = doPost(PaySafeCardConstants.SCHEME, host, path, headers, jsonBody);
        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");

        // create object from PaySafeCard response
        return parser.fromJson(responseString, PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse retrievePaymentData(PaySafeCaptureRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId());
        Header[] headers = createHeaders(request.getAuthenticationHeader());
        // do the request
        HttpResponse response = doGet(PaySafeCardConstants.SCHEME, host, path, headers);
        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        return parser.fromJson(responseString, PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse capture(PaySafeCaptureRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId(), PaySafeCardConstants.PATH_CAPTURE);

        String body = "";
        Header[] headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        HttpResponse response = doPost(PaySafeCardConstants.SCHEME, host, path, headers, body);

        // create object from PaySafeCard response
        String responseString = EntityUtils.toString(response.getEntity(), "UTF-8");
        return parser.fromJson(responseString, PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse refund(PaySafePaymentRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId(), PaySafeCardConstants.PATH_REFUND);
        String jsonBody = parser.toJson(request);
        Header[] headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        HttpResponse response = doPost(PaySafeCardConstants.SCHEME, host, path, headers, jsonBody);

        // create object from PaySafeCard response
        return parser.fromJson(response.getEntity().toString(), PaySafePaymentResponse.class);
    }

}
