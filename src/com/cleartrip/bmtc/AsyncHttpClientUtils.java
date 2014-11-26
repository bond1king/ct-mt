package com.cleartrip.bmtc;

import java.io.IOException;
import java.security.KeyManagementException;
import java.security.KeyStore;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.UnrecoverableKeyException;
import java.security.cert.CertificateException;
import java.util.HashMap;
import java.util.Map;

import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;

import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.PersistentCookieStore;

public class AsyncHttpClientUtils {

    protected static AsyncHttpClient asyncHttpClient;

    protected static AsyncHttpClient mHotelAsyncHttpClient;


    private static String deviceToken = null;

    private static String version = "";


    private AsyncHttpClientUtils() {

    }

    public static AsyncHttpClient getCommonAsyncHTTPClient() {

        if (asyncHttpClient == null) {
            asyncHttpClient = new AsyncHttpClient();
        }

        asyncHttpClient = addHeaders(asyncHttpClient);

        asyncHttpClient = addAdditionalHttpHeaders(asyncHttpClient);

        return asyncHttpClient;
    }

    public static AsyncHttpClient getHotelAsyncHTTPClient() {

        if (mHotelAsyncHttpClient == null) {
            mHotelAsyncHttpClient = new AsyncHttpClient();
        }

        mHotelAsyncHttpClient = addHeaders(mHotelAsyncHttpClient);

        mHotelAsyncHttpClient.addHeader("X-CT-SOURCETYPE", "MOBILE");

        mHotelAsyncHttpClient = addAdditionalHttpHeaders(mHotelAsyncHttpClient);

        return mHotelAsyncHttpClient;
    }

    private static AsyncHttpClient addAdditionalHttpHeaders(AsyncHttpClient localasyncHttpClient) {


        localasyncHttpClient.setTimeout(75000);
        localasyncHttpClient.addHeader("app-version", version);

        if (deviceToken != null && !deviceToken.isEmpty()) {
            localasyncHttpClient.addHeader("device-token", deviceToken);
        }
        localasyncHttpClient.removeHeader("content-type");
        localasyncHttpClient.removeHeader("Content-Type");

        localasyncHttpClient.removeHeader("Accept");
        localasyncHttpClient.removeHeader("accept");

        return localasyncHttpClient;
    }

    public static Map<String, String> getDefaultHeaders() {
        return new HashMap<String, String>() {
            {
                put("app-agent", "AndroidApp");
                put("app-platform", Build.VERSION.RELEASE);
                put("api-version", "1.0");
            }
        };
    }

    private static AsyncHttpClient addHeaders(AsyncHttpClient addHeadersToAsyncHttpClient) {

        try {
            KeyStore trustStore = KeyStore.getInstance(KeyStore.getDefaultType());
            trustStore.load(null, null);
        } catch (NoSuchAlgorithmException e) {
        } catch (KeyStoreException e) {
        } catch (CertificateException e) {
        } catch (IOException e) {
        } catch (Exception e) {
        }

            addHeadersToAsyncHttpClient.addHeader("user-agent", "Android App Version ");
            addHeadersToAsyncHttpClient.addHeader("app-agent", "AndroidApp");

        addHeadersToAsyncHttpClient.setTimeout(75000);
        addHeadersToAsyncHttpClient.addHeader("app-platform", Build.VERSION.RELEASE);

        addHeadersToAsyncHttpClient.addHeader("api-version", "1.0");
        return addHeadersToAsyncHttpClient;
    }

}
