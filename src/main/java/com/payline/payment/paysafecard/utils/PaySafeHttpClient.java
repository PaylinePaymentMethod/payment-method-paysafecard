package com.payline.payment.paysafecard.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.payline.payment.paysafecard.bean.PaySafeCaptureRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentRequest;
import com.payline.payment.paysafecard.bean.PaySafePaymentResponse;
import com.payline.payment.paysafecard.bean.PaySafeRequest;
import com.payline.pmapi.bean.configuration.PartnerConfiguration;
import org.apache.http.Header;
import org.apache.http.client.config.RequestConfig;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpRequestBase;
import org.apache.http.client.utils.URIBuilder;
import org.apache.http.conn.ssl.SSLConnectionSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.BasicCredentialsProvider;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.http.message.BasicHeader;
import org.apache.http.util.EntityUtils;
import com.payline.pmapi.logger.LogManager;
import org.apache.logging.log4j.Logger;

import javax.net.ssl.HttpsURLConnection;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.concurrent.atomic.AtomicBoolean;

public class PaySafeHttpClient {

    public static final String KEY_CONNECT_TIMEOUT = "connect.time.out";
    public static final String CONNECTION_REQUEST_TIMEOUT = "connect.request.time.out";
    public static final String READ_SOCKET_TIMEOUT = "read.time.out";

    private static final Logger LOGGER = LogManager.getLogger(PaySafeHttpClient.class);
    private static final String DEFAULT_CHARSET = "UTF-8";
    private static final String CONTENT_TYPE_KEY = "Content-Type";
    private static final String AUTHENTICATION_KEY = "Authorization";
    private static final String CONTENT_TYPE = "application/json";
    private CloseableHttpClient client;
    private Gson parser;

    private static final AtomicBoolean isInit = new AtomicBoolean(false);

    private static PaySafeHttpClient instance;

    /**
     * @return the singleton instance
     */
    public static PaySafeHttpClient getInstance(final PartnerConfiguration partnerConfiguration) {
        //On initialise le service avec les configurations du partenaire si c'est le premier appel.
        if (!isInit.getAndSet(true)) {
            LOGGER.info("Initialisation du service HTTP Client");
            instance = new PaySafeHttpClient(partnerConfiguration);
        }
        return instance;
    }

    private PaySafeHttpClient(final PartnerConfiguration partnerConfiguration) {
        this.parser = new GsonBuilder().create();
        final RequestConfig requestConfig = RequestConfig.custom()
                .setConnectTimeout(Integer.parseInt(partnerConfiguration.getProperty(KEY_CONNECT_TIMEOUT)))
                .setConnectionRequestTimeout(Integer.parseInt(partnerConfiguration.getProperty(CONNECTION_REQUEST_TIMEOUT)))
                .setSocketTimeout(Integer.parseInt(partnerConfiguration.getProperty(READ_SOCKET_TIMEOUT))).build();

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
        StringBuilder sb = new StringBuilder("/");
        if (path != null && path.length > 0) {
            for (String aPath : path) {
                sb.append(aPath).append("/");
            }
        }

        return sb.toString();
    }

    private Header[] createHeaders(String authentication) {
        Header[] headers = new Header[2];
        headers[0] = new BasicHeader(CONTENT_TYPE_KEY, CONTENT_TYPE);
        headers[1] = new BasicHeader(AUTHENTICATION_KEY, authentication);
        return headers;
    }

    public String doGet(String scheme, String host, String path, Header[] headers) throws IOException, URISyntaxException {

        final URI uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path)
                .build();

        final HttpGet httpGetRequest = new HttpGet(uri);
        httpGetRequest.setHeaders(headers);
        return this.execute(httpGetRequest);
    }

    public String doPost(String scheme, String host, String path, Header[] headers, String body) throws IOException, URISyntaxException {

        final URI uri = new URIBuilder()
                .setScheme(scheme)
                .setHost(host)
                .setPath(path)
                .build();

        final HttpPost httpPostRequest = new HttpPost(uri);
        httpPostRequest.setHeaders(headers);
        httpPostRequest.setEntity(new StringEntity(body));
        return this.execute(httpPostRequest);
    }

    protected String execute(final HttpRequestBase request) throws IOException {
        final long start = System.currentTimeMillis();
        int count = 0;
        String strResp = null;
        while (count < 3) {
            try (CloseableHttpResponse httpResp = this.client.execute(request)) {

                if (httpResp.getEntity() != null) {
                    LOGGER.info("Start partner call... [HOST: {}]", request.getURI().getHost());
                    strResp = EntityUtils.toString(httpResp.getEntity(), DEFAULT_CHARSET);
                }

                final long end = System.currentTimeMillis();
                LOGGER.info("End partner call [T: {}ms] [CODE: {}]", end - start, httpResp.getStatusLine().getStatusCode());
                return strResp;

            } catch (final IOException e) {
                LOGGER.error("Error while partner call [T: {}ms]", System.currentTimeMillis() - start, e);
            } finally {
                count++;
            }
        }
        throw new IOException("Partner response empty");
    }

    public PaySafePaymentResponse initiate(PaySafeRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH);
        String jsonBody = parser.toJson(request);
        Header[] headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        final String responseString = doPost(PaySafeCardConstants.SCHEME, host, path, headers, jsonBody);

        // create object from PaySafeCard response
        return parser.fromJson(responseString, PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse retrievePaymentData(PaySafeCaptureRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId());
        Header[] headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        final String responseString = doGet(PaySafeCardConstants.SCHEME, host, path, headers);

        // create object from PaySafeCard response
        return parser.fromJson(responseString, PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse capture(PaySafeCaptureRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId(), PaySafeCardConstants.PATH_CAPTURE);

        String body = "";
        Header[] headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        final String responseString = doPost(PaySafeCardConstants.SCHEME, host, path, headers, body);

        // create object from PaySafeCard response
        return parser.fromJson(responseString, PaySafePaymentResponse.class);
    }

    public PaySafePaymentResponse refund(PaySafePaymentRequest request, boolean isSandbox) throws IOException, URISyntaxException {
        String host = getHost(isSandbox);
        String path = createPath(PaySafeCardConstants.PATH_VERSION, PaySafeCardConstants.PATH, request.getPaymentId(), PaySafeCardConstants.PATH_REFUND);
        String jsonBody = parser.toJson(request);
        Header[] headers = createHeaders(request.getAuthenticationHeader());

        // do the request
        final String responseString = doPost(PaySafeCardConstants.SCHEME, host, path, headers, jsonBody);

        // create object from PaySafeCard response
        return parser.fromJson(responseString, PaySafePaymentResponse.class);
    }

}
