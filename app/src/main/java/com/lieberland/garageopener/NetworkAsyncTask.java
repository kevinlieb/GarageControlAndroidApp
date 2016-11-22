package com.lieberland.garageopener;

import android.content.ContentValues;
import android.net.Uri;
import android.os.AsyncTask;
import android.util.Log;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

/**
 * Created by klieberman on 6/2/16.
 */
public class NetworkAsyncTask extends AsyncTask<String, Void, String> {
	String id = "";
	String appName = "";
	String accessToken = "";
	
    protected String doInBackground(String... params) {
        String urlString = "https://api.particle.io/v1/devices/" + id + "/" + appName + "/";
        String urlParameters;
        StringBuffer results = new StringBuffer("");
        HttpURLConnection connection = null;

        try {
            URL url = new URL(urlString);
            connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("POST");
            connection.setDoInput(true);
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestProperty( "charset", "utf-8");


            if(params[0] == "on") {
                urlParameters = "access_token=" + accessToken + "&args=r2,HIGH";
            }
            else {
                if(params[0] == "off") {
                    urlParameters = "access_token=" + accessToken + "&args=r2,LOW";
                }
                else {
                    urlParameters = "access_token=" + accessToken + "&args=r" + params[0] + ",FLASH";
                }
            }
            byte[] postData = urlParameters.getBytes( StandardCharsets.UTF_8 );

            DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
            wr.write(postData);
            wr.flush();
            wr.close();

            connection.connect();
            InputStream inputStream = connection.getInputStream();

            BufferedReader rd = new BufferedReader(new InputStreamReader(inputStream));
            String line = "";
            while ((line = rd.readLine()) != null) {
                results.append(line);
            }
            Log.d("ASDF", "Results: " + results.toString());
        }
        catch(Exception e) {
            Log.e("ASDF", "Error doing https connection:" + e.getMessage());
            e.printStackTrace();
            String errorLine = "";
            BufferedReader rc = new BufferedReader(new InputStreamReader(connection.getErrorStream()));
            try {
                while ((errorLine = rc.readLine()) != null) {
                    Log.d("ASDF", errorLine);
                }
            }
            catch(Exception ed) {
                Log.d("ASDF", "Error reading error");
            }
        }
        return("DONE");
    }

    protected void onPostExecute(String result) {
        Log.d("ASDF", "Result in onPostExecute is " + result);

    }
}
