package com.uprise.ordering.rest;

import android.util.Log;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpPut;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.protocol.HTTP;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;

/**
 * Created by cicciolina on 11/8/16.
 */

public class HttpClient {
    private static final String TAG = "HttpClient";


    public static JSONObject SendHttpGetWithoutParamAndAuth(String URL) {

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGetRequest = new HttpGet(URL);

//            StringEntity se;
//            se = new StringEntity(jsonObjSend.toString());

            httpGetRequest.setHeader(HTTP.CONTENT_TYPE, "application/json");
//            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpGetRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
//                Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    instream = new GZIPInputStream(instream);
//                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
//                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }
    public static JSONObject SendHttpGetWithoutParamWithAuthorization(String URL, String token) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();

            HttpGet httpGetRequest = new HttpGet(URL);

//            StringEntity se;
//            se = new StringEntity(jsonObjSend.toString());

            httpGetRequest.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpGetRequest.addHeader("Authorization","JWT "+token);
//            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpGetRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
//                Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    instream = new GZIPInputStream(instream);
//                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
//                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                // Transform the String into a JSONObject
                 JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject SenHttpPutWithAuthentication(String URL, JSONObject jsonObjSend, String token) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPut httpPutRequest = new HttpPut(URL);

            StringEntity se;
            se = new StringEntity(jsonObjSend.toString());

            httpPutRequest.setEntity(se);
            httpPutRequest.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPutRequest.addHeader("Authorization","JWT "+token);
//            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPutRequest);

            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
//                Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    instream = new GZIPInputStream(instream);
//                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
//                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject SenHttpPostWithAuthentication(String URL, JSONObject jsonObjSend, String token) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

            StringEntity se;
            se = new StringEntity(jsonObjSend.toString());

            httpPostRequest.setEntity(se);
            httpPostRequest.setHeader(HTTP.CONTENT_TYPE, "application/json");
            httpPostRequest.addHeader("Authorization","JWT "+token);
//            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);

            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
//                Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    instream = new GZIPInputStream(instream);
//                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
//                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject SenHttpPostWithAuthentication(String URL, ArrayList<NameValuePair> nameValuePairs, String token) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

//            StringEntity se;
//            se = new StringEntity(jsonObjSend.toString());

            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPostRequest.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
            httpPostRequest.addHeader("Authorization","JWT "+token);
//            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
//                Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    instream = new GZIPInputStream(instream);
//                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
//                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static JSONObject SendHttpPost(String URL, ArrayList<NameValuePair> nameValuePairs) {
        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

//            StringEntity se;
//            se = new StringEntity(jsonObjSend.toString());

            httpPostRequest.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            httpPostRequest.setHeader(HTTP.CONTENT_TYPE, "application/x-www-form-urlencoded");
//            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis() - t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
//                Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    instream = new GZIPInputStream(instream);
//                }

                // convert content stream to a String
                String resultString = convertStreamToString(instream);
                instream.close();
//                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i(TAG, "<JSONObject>\n" + jsonObjRecv.toString() + "\n</JSONObject>");

                return jsonObjRecv;
            }

        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }




    public static JSONObject SendHttpPost(String URL, JSONObject jsonObjSend) {

        try {
            DefaultHttpClient httpclient = new DefaultHttpClient();
            HttpPost httpPostRequest = new HttpPost(URL);

            StringEntity se;
            se = new StringEntity(jsonObjSend.toString());

            httpPostRequest.setEntity(se);
            httpPostRequest.setHeader(HTTP.CONTENT_TYPE, "application/json");
//            httpPostRequest.setHeader("Accept-Encoding", "gzip"); // only set this parameter if you would like to use gzip compression

            long t = System.currentTimeMillis();
            HttpResponse response = (HttpResponse) httpclient.execute(httpPostRequest);
            Log.i(TAG, "HTTPResponse received in [" + (System.currentTimeMillis()-t) + "ms]");

            // Get hold of the response entity (-> the data):
            HttpEntity entity = response.getEntity();

            if (entity != null) {
                // Read the content stream
                InputStream instream = entity.getContent();
//                Header contentEncoding = response.getFirstHeader("Content-Encoding");
//                if (contentEncoding != null && contentEncoding.getValue().equalsIgnoreCase("gzip")) {
//                    instream = new GZIPInputStream(instream);
//                }

                // convert content stream to a String
                String resultString= convertStreamToString(instream);
                instream.close();
//                resultString = resultString.substring(1,resultString.length()-1); // remove wrapping "[" and "]"

                // Transform the String into a JSONObject
                JSONObject jsonObjRecv = new JSONObject(resultString);
                // Raw DEBUG output of our received JSON object:
                Log.i(TAG,"<JSONObject>\n"+jsonObjRecv.toString()+"\n</JSONObject>");

                return jsonObjRecv;
            }

        }
        catch (Exception e)
        {
            // More about HTTP exception handling in another tutorial.
            // For now we just print the stack trace.
            e.printStackTrace();
        }
        return null;
    }


    private static String convertStreamToString(InputStream is) {
		/*
		 * To convert the InputStream to String we use the BufferedReader.readLine()
		 * method. We iterate until the BufferedReader return null which means
		 * there's no more data to read. Each line will appended to a StringBuilder
		 * and returned as String.
		 *
		 * (c) public domain: http://senior.ceng.metu.edu.tr/2009/praeda/2009/01/11/a-simple-restful-client-at-android/
		 */
        BufferedReader reader = new BufferedReader(new InputStreamReader(is));
        StringBuilder sb = new StringBuilder();

        String line = null;
        try {
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                is.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return sb.toString();
    }

}
