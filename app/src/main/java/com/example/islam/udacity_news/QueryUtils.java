/*
 * Copyright (C) 2016 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.islam.udacity_news;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;

// this class contain Helper methods related to requesting and receiving earthquake data from The Guardian.
public final class QueryUtils {

    // Tag for the log messages
    private static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {}

    // this method to ties all steps together
    public static List<News> fetchEarthquakeData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem making the HTTP request.", e);
        }

        // Extract relevant fields from the JSON response and create a list of {@link Earthquake}s
        List<News> earthquakes = extractFeatureFromJson(jsonResponse);

        // Return the list of {@link Earthquake}s
        return earthquakes;
    }

    // this method to return new URL object from the given string URL, and handle any error occur in spelling URL.
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Problem building the URL ", e);
        }
        return url;
    }


    // Make an HTTP request to the given URL and return a String as the response.
    private static String makeHttpRequest(URL url) throws IOException {
        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = "";

        // if URL is null don't perform HTTP request and return early
        if (url == null) {
            return jsonResponse;
        }

        // HttpURLConnection is subclass from URLConnection class
        HttpURLConnection urlConnection = null;
        // inputStream containing the result retrieve from server in bytes
        InputStream inputStream = null;
        try {
            // setting of connection

            /**
             * openConnection method to return URL connection instance, such if not return URL...
             * meaning occur Exception(problem) in opening connection
             **/
            urlConnection = (HttpURLConnection) url.openConnection();

            // time of read data from server in milliseconds
            urlConnection.setReadTimeout(10000);

            // time of connect is opening in milliseconds
            urlConnection.setConnectTimeout(15000);

            // GET is HTTP request method use to read information from the server and sending it to client.
            urlConnection.setRequestMethod("GET");

            /**
             * From this line is actually establish the http connection with server
             and everything before this line of code is about setting up http request
             and everything after this line about receiving response
             */
            urlConnection.connect();

            /**
             * Check HTTP response code after connection has been established
             by calling method urlConnection.getResponseCode, that returns integer
             if response code equal 200 then we read from inputStream to extract JsonResponse
             otherwise if we have an error response code not 200 we do nothing and only send log
             message to know error
             **/
            if (urlConnection.getResponseCode() == 200) {
                // inputStream to retrieve the result from server in bytes
                inputStream = urlConnection.getInputStream();
                // readFromInputStream method to read data from inputStream and put into jsonResponse
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            // IOException to handle any error occurred when connect to server
            Log.e(LOG_TAG, "Problem retrieving the earthquake JSON results.", e);
        } finally {
            /**
             * finally block is executed regardless whether or not exception is thrown.
             * we use finally block to disconnect the URLConnection and closing inputStream
             to cleaning up resources immediately after finished use because operating system
             himself release that later and by difficultly.
             **/
            if (urlConnection != null) {
                // that's meaning i get value so close connect
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // when inputStream not null meaning contain value so close this because i get value
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * readFromStream method convert inputStream into String to put into jsonResponse block by block
     instead of byte by byte to transfer data to jsonResponse by more speed, so convert from
     bytes then readable characters then to string, inputStream -> InputStreamReader -> BufferedStream
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        // StringBuilder is can change after created(mutable), but String is immutable(not change)
        StringBuilder output = new StringBuilder();

        if (inputStream != null) {

            /**
             * Charset specify how to translate(decode) each byte into specific human readable character
             * UTF-8 is the Unicode character encoding used for almost every piece of text
             you will find on the web
             */
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));

            /**
             * use BufferReader to read stream of characters (data stored in Buffered as strings)
             * inputStreameder only allow read single character(build up characters to readable)
             * use inputStream to actually provide the data from the server(bytes not readable)
             * so use BufferedReader to more performance to fast reading large chunks of data
              that's all to read data back from HttpURLConnection is successful
             **/
            BufferedReader reader = new BufferedReader(inputStreamReader);

            // reading line by line from Buffered
            String line = reader.readLine();
            while (line != null) {
                // output is StringBuilder variable, and append to put in StringBuilder
                output.append(line);
                // read another line from BufferedReader
                line = reader.readLine();
            }
        }
        // note after building StringBuilder convert to frozen immutable string by toString
        return output.toString();
    }

    // Return a list of Book objects that has been built up from parsing the given JSON response.
    private static List<News> extractFeatureFromJson(String NewsJSON) {
        /**
         * before extracting information NewsJSON response we should check if the
         * NewsJSON isEmpty or not by TextUtils.isEmpty method
         * Note TextUtils.isEmpty is help method define in Android Studio framework
         */
        if (TextUtils.isEmpty(NewsJSON)) {
            // return null because no valid object
            return null;
        }

        // Create an empty ArrayList that we can start adding News to
        List<News> newsList = new ArrayList<>();

        // try parse JSON response
        try {

            // Create a JSONObject from the JSON response string
            JSONObject baseJsonResponse = new JSONObject(NewsJSON);

            // extract JSONObject with key response, which contain array of items
            JSONObject response = baseJsonResponse.getJSONObject("response");

            // extract JSONArray with key results, which represent a list of results(News)
            JSONArray results = response.getJSONArray("results");

            // For access each object(News) in the results Array
            for (int i = 0; i < results.length(); i++) {
                // Get a single News at position i within the list of News
                JSONObject currentNews = results.getJSONObject(i);

                // Extract the value for the key called  webTitle
                String webTitle = currentNews.getString("webTitle");

                // Extract the value for the key called  sectionName
                String sectionName = currentNews.getString("sectionName");

                // Extract the value for the key called  webPublicationDate
                String str = currentNews.getString("webPublicationDate");
                // split string before T to only take date
                String webPublicationDate = str.substring(0, str.indexOf("T"));

                // Extract the value for the key called  webUrl
                String webUrl = currentNews.getString("webUrl");

                // create a new News object with the webTitle, sectionName, webPublicationDate, webUrl
                News New = new News(webTitle, sectionName, webPublicationDate, webUrl);

                // Add the new News to the list of News.
                newsList.add(New);
            }

          // handle any error in JSON Response
        } catch (JSONException e) {
            Log.e("QueryUtils", "Problem parsing the earthquake JSON results", e);
        }

        // Return the list of News
        return newsList;
    }

}
