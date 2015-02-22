/*
 *  Copyright 2014 Abid Hasan Mujtaba
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package com.abid_mujtaba.bitcoin.tracker.network;

import android.content.Context;

import com.abid_mujtaba.bitcoin.tracker.network.exceptions.ClientException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.HttpsURLConnection;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManagerFactory;

import java.net.SocketTimeoutException;
import java.net.URL;
import java.security.KeyManagementException;
import java.security.KeyStoreException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.Certificate;
import java.security.KeyStore;
import java.security.cert.CertificateException;
import java.security.cert.CertificateFactory;


/**
 * Encapsulates all network I/O associated with the Client functionality of the application.
 */

public class Client
{
    // Socket and Connection timeouts (in milliseconds):
    private final static int CONNECTION_TIMEOUT = 2000;            // Timeout waiting for the connection to be established in the first place
    private final static int READ_TIMEOUT = 5000;                  // Timeout waiting for the response to be read from the server (once the connection has been established)
    private static final int SOCKET_TIMEOUT = 3 * 1000;


    public static String get_btc_price() throws ClientException
    {
        final String url = "https://www.bitstamp.net/api/ticker/";

        HttpClient client = getHttpClient();
        HttpGet get = new HttpGet(url);

        long time = System.currentTimeMillis() / 1000;
        float buy_price, sell_price;

        try
        {
            // Fetch buy price from coinbase backend
            HttpResponse response = client.execute(get);

            JSONObject jResponse = new JSONObject( HttpResponseToString(response) );
            buy_price = Float.parseFloat( jResponse.getString("ask") );
            sell_price = Float.parseFloat( jResponse.getString("bid") );

            return String.format("%d %.2f %.2f", time, buy_price, sell_price);
        }
        catch (IOException e) { throw new ClientException("GET failure.", e); }
        catch (JSONException e) { throw new ClientException("Failed to convert GET response to JSON.", e); }
    }


    private static HttpClient getHttpClient()       // Returns an HTTP client
    {
        HttpParams params = new BasicHttpParams();

        HttpConnectionParams.setConnectionTimeout(params, CONNECTION_TIMEOUT);
        HttpConnectionParams.setSoTimeout(params, SOCKET_TIMEOUT);

        return new DefaultHttpClient(params);
    }


    public static JSONObject get_json(Context context, String url_string) throws ClientException
    {
        try
        {
            return new JSONObject(get(context, url_string));
        }
        catch (JSONException e) { throw new ClientException("Failed to parse JSON response.", e); }
    }


    private static String get(Context context, String url_string) throws ClientException
    {
        HttpsURLConnection connection = null;            // NOTE: fetchImage is set up to use HTTP not HTTPS

        try
        {
            connection = setupSSL(context, url_string);

            connection.setConnectTimeout(CONNECTION_TIMEOUT);
            connection.setReadTimeout(READ_TIMEOUT);

            InputStream is = new BufferedInputStream( connection.getInputStream() );

            int response_code;

            if((response_code = connection.getResponseCode()) != 200)
            {
                throw new ClientException("Error code returned by response: " + response_code);
            }

            return InputStreamToString(is);
        }
        catch (SocketTimeoutException e) { throw new ClientException("Socket timed out.", e); }
        catch (IOException e) { throw new ClientException("IO Exception raised while attempting to GET response.", e); }
        finally
        {
            if (connection != null) { connection.disconnect(); }
        }
    }


    private static String InputStreamToString(InputStream content) throws ClientException
    {
        try
        {
            BufferedReader br = new BufferedReader(new InputStreamReader(content, "UTF-8"));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            br.close();

            return sb.toString();
        }
        catch (UnsupportedEncodingException e) { throw new ClientException("Content InputStream has unsupported encoding.", e); }
        catch (IOException e) { throw new ClientException("IOException thrown by BufferedReader while reading InputStream.", e); }
    }


    // Make the app trust a self-signed SSL certificate.
    // Source: http://littlesvr.ca/grumble/2014/07/21/android-programming-connect-to-an-https-server-with-self-signed-certificate/

    // The tutorial needs only one modification. To get the X509 SSL certificate follow the instructions from: https://coderwall.com/p/wv6fpq/add-self-signed-ssl-certificate-to-android-for-browsing

    private final static String SSL_CERT = "marzipan.whatbox.ca.crt";

    public static HttpsURLConnection setupSSL(Context context, String url_string) throws ClientException
    {
        try
        {
            CertificateFactory cf = CertificateFactory.getInstance("X.509");
            InputStream is = new BufferedInputStream(context.getAssets().open(SSL_CERT));

            Certificate ca = cf.generateCertificate(is);

            KeyStore keystore = KeyStore.getInstance(KeyStore.getDefaultType());
            keystore.load(null, null);
            keystore.setCertificateEntry("ca", ca);

            TrustManagerFactory tmf = TrustManagerFactory.getInstance(TrustManagerFactory.getDefaultAlgorithm());
            tmf.init(keystore);

            SSLContext sslcontext = SSLContext.getInstance("TLS");
            sslcontext.init(null, tmf.getTrustManagers(), null);

            // Tell the URLConnection to use a SocketFactory from our SSLContext
            URL url = new URL(url_string);
            HttpsURLConnection connection = (HttpsURLConnection) url.openConnection();
            connection.setSSLSocketFactory(sslcontext.getSocketFactory());

            return connection;
        }
        catch (CertificateException e) {throw new ClientException("SSL Error", e);}
        catch (IOException e) {throw new ClientException("SSL Error", e);}
        catch (KeyStoreException e) {throw new ClientException("SSL Error", e);}
        catch (NoSuchAlgorithmException e) {throw new ClientException("SSL Error", e);}
        catch (KeyManagementException e) {throw new ClientException("SSL Error", e);}
    }


    private static String HttpResponseToString(HttpResponse response) throws ClientException
    {
        try
        {
            InputStream is = response.getEntity().getContent();

            BufferedReader br = new BufferedReader(new InputStreamReader(is, "UTF-8"));

            String line;
            StringBuilder sb = new StringBuilder();

            while ((line = br.readLine()) != null)
            {
                sb.append(line);
            }

            br.close();

            return sb.toString();
        }
        catch (UnsupportedEncodingException e) { throw new ClientException("Error converting HttpResponse to String.", e); }
        catch (IOException e) { throw new ClientException("Error converting HttpResponse to String.", e); }
    }
}
