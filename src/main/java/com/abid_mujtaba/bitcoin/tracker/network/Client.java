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

import com.abid_mujtaba.bitcoin.tracker.network.exceptions.ClientException;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;

import javax.net.ssl.HttpsURLConnection;
import java.net.SocketTimeoutException;
import java.net.URL;


/**
 * Encapsulates all network I/O associated with the Client functionality of the application.
 */

public class Client
{
    // Socket and Connection timeouts (in milliseconds):
    private final static int CONNECTION_TIMEOUT = 2000;            // Timeout waiting for the connection to be established in the first place
    private final static int READ_TIMEOUT = 5000;                  // Timeout waiting for the response to be read from the server (once the connection has been established)


    public static JSONObject get_json(String url_string) throws ClientException
    {
        try
        {
            return new JSONObject(get(url_string));
        }
        catch (JSONException e) { throw new ClientException("Failed to parse JSON response.", e); }
    }


    private static String get(String url_string) throws ClientException
    {
        HttpsURLConnection connection = null;            // NOTE: fetchImage is set up to use HTTP not HTTPS

        try
        {
            URL url = new URL(url_string);
            connection = (HttpsURLConnection) url.openConnection();

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
}
