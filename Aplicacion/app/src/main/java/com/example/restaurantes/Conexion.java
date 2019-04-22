package com.example.restaurantes;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;


public class Conexion extends AsyncTask<String, Void, String>{
    protected String doInBackground(String... strings) {
        URL url = null;
        try {
            url = new URL(strings[0]);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            if(!strings[1].equals("GET") && !strings[1].equals("DELETE")){
                conn.setRequestMethod(strings[1]);
                conn.setRequestProperty("Content-Type", "application/json;charset=UTF-8");
                //conn.setRequestProperty("Content-Transfer-Encoding","binary");
                conn.setRequestProperty("Accept","application/json");
                //conn.setRequestProperty("Accept-Charset", "UTF-8");
                conn.setDoOutput(true);
                conn.setDoInput(true);

                //DataOutputStream os = new DataOutputStream(conn.getOutputStream());
                BufferedWriter os = new BufferedWriter(new OutputStreamWriter(conn.getOutputStream(), "UTF-8"));
                os.write(strings[2]);

                os.flush();
                os.close();

                return conn.getResponseMessage();
            }else if(strings[1].equals("DELETE")){
                //URL url = new URL(strings[0]);
                HttpURLConnection httpCon = (HttpURLConnection) url.openConnection();
                httpCon.setDoOutput(true);
                httpCon.setRequestProperty(
                        "Content-Type", "application/x-www-form-urlencoded" );
                httpCon.setRequestMethod(strings[1]);
                httpCon.connect();
                return httpCon.getResponseMessage();
            }
            else{
                conn.setRequestMethod(strings[1]);
                conn.setRequestProperty("Content-Type","application/json");
                if (conn.getResponseCode() == HttpURLConnection.HTTP_OK) {
                    StringBuilder jsonResponse = new StringBuilder();
                    BufferedReader input = new BufferedReader(new InputStreamReader(conn.getInputStream()));
                    String strLine = null;
                    while ((strLine = input.readLine()) != null) {
                        jsonResponse.append(strLine);
                    }
                    //xmlString += urlConnection.getHeaderField("access-token");
                    input.close();
                    return jsonResponse.toString();

                }else{
                    return "Error";
                }
            }

        } catch (MalformedURLException e) {
            //e.printStackTrace();
            return e.toString();
        } catch (IOException e) {
            return e.toString();
        }

    }


}
