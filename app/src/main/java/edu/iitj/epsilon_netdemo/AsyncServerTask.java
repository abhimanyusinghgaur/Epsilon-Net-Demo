package edu.iitj.epsilon_netdemo;

import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.lang.reflect.Method;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLEncoder;

/**
 * Created by user on 14-11-2016.
 */
public class AsyncServerTask extends AsyncTask<String, Void, String> {

    public final static String GET = "GET";
    public final static String POST = "POST";

    private String urlString;
    private Object object;
    private Method completionCallback;

    private String postParameters;
    private String requestMethod;

    public AsyncServerTask() {
        this.requestMethod = POST;
        this.postParameters = "";
    }

    public AsyncServerTask(String urlString, Object object, Method callback) {
        this.requestMethod = POST;
        this.postParameters = "";
        this.urlString = urlString;
        this.object = object;
        this.completionCallback = callback;

    }

    //setters
    public void setUrlString(String urlString) {
        this.urlString = urlString;
    }

    public void setObject(Object object) {
        this.object = object;
    }

    public void setCompletionCallback(Method completionCallback) {
        this.completionCallback = completionCallback;
    }

    public void addPostParameter(String parameter, String value) {
        try {
            if(!postParameters.equals(""))
                postParameters += "&";
            postParameters += parameter + "=" + URLEncoder.encode(value, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
    }

    public void setRequestMethod(String requestMethod) {
        this.requestMethod = requestMethod;
    }

    @Override
    protected void onPreExecute() {

    }

    @Override
    protected String doInBackground(String... params) {
        InputStream is = null;
        StringBuilder response = new StringBuilder();
        URL url = null;
        try {
            url = new URL(urlString);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        try {
            HttpURLConnection c = (HttpURLConnection) url.openConnection();
            c.setDoOutput(true);

            if(requestMethod.equals(POST)) {
                c.setRequestMethod(POST);
                c.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");

                c.setFixedLengthStreamingMode(postParameters.getBytes().length);
                DataOutputStream out = new DataOutputStream(c.getOutputStream());
                out.writeBytes(postParameters);
                out.flush();
                out.close();
            }

            int responseCode = c.getResponseCode();

            is = c.getInputStream();
            BufferedReader rd = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            String line;
            while((line = rd.readLine()) != null) {
                response.append(line);
                response.append('\r');
            }
            rd.close();
            Log.d("post", "Response Code: "+responseCode);
        } catch (IOException e) {
            e.printStackTrace();
        }

        return response.toString();
    }

    @Override
    protected void onPostExecute(String result) {
        String s = result.trim();
        try {
            if(completionCallback != null)
                completionCallback.invoke(object, s);
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }
}
