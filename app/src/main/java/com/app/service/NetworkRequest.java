package com.app.service;

import android.content.Context;
import android.content.SharedPreferences;

import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class NetworkRequest {
    private Context context;
    private String token = null;
    private String cusId = null;

    public NetworkRequest (Context context){
        this.context = context;
        evaluateUser();
    }

    private void evaluateUser(){
        // Get SharedPreferences
        SharedPreferences sharedPreferences = context.getSharedPreferences("MyAppPrefs", context.MODE_PRIVATE);
        // Retrieve the token
        this.token = sharedPreferences.getString("auth_token", null);
        this.cusId = sharedPreferences.getString("cus_id", null);
    }

    public String getCusId(){
        return this.cusId;
    }

    // Method for sending GET request
    public String sendGetRequest(String requestUrl) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("GET");
            if(this.token != null){
                urlConnection.setRequestProperty("Authorization", "Bearer " + this.token);
            }

            // Read the response
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return null;  // Handle non-200 responses
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle exceptions
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    // Method for sending POST request
    public String sendPostRequest(String requestUrl, JSONObject postData) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("POST");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if(this.token != null){
                urlConnection.setRequestProperty("Authorization", "Bearer " + this.token);
            }

            // Send JSON data
            OutputStream os = urlConnection.getOutputStream();
            os.write(postData.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            // Read the response
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return null;  // Handle non-200 responses
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle exceptions
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    // Method for sending PUT request
    public String sendPutRequest(String requestUrl, JSONObject putData) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setDoOutput(true);
            urlConnection.setRequestMethod("PUT");
            urlConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
            if(this.token != null){
                urlConnection.setRequestProperty("Authorization", "Bearer " + this.token);
            }

            // Send JSON data
            OutputStream os = urlConnection.getOutputStream();
            os.write(putData.toString().getBytes("UTF-8"));
            os.flush();
            os.close();

            // Read the response
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return null;  // Handle non-200 responses
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle exceptions
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }

    // Method for sending DELETE request
    public String sendDeleteRequest(String requestUrl) {
        HttpURLConnection urlConnection = null;
        try {
            URL url = new URL(requestUrl);
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setRequestMethod("DELETE");
            if(this.token != null){
                urlConnection.setRequestProperty("Authorization", "Bearer " + this.token);
            }

            // Read the response
            int responseCode = urlConnection.getResponseCode();
            if (responseCode == HttpURLConnection.HTTP_OK) {
                BufferedReader reader = new BufferedReader(new InputStreamReader(urlConnection.getInputStream(), "UTF-8"));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();
                return response.toString();
            } else {
                return null;  // Handle non-200 responses
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;  // Handle exceptions
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
        }
    }
}
