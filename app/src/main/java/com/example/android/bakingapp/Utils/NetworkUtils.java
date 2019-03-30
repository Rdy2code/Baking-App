package com.example.android.bakingapp.Utils;

import android.net.Uri;

import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public class NetworkUtils {

    //URL String constants for building a URL query to the Baking App Json link
    private final static String RECIPE_BASE_URL = "https://d17h27t6h515a5.cloudfront.net";
    private final static String PATH_NAME = "topher";
    private final static String YEAR = "2017";
    private final static String MONTH = "May";
    private final static String PATH_ID = "59121517_baking";
    private final static String PATH_FILE = "baking.json";

    public static Uri buildUri () {
        //Build a network Url
        Uri builtUri = Uri.parse(RECIPE_BASE_URL).buildUpon()
                .appendEncodedPath(PATH_NAME)
                .appendEncodedPath(YEAR)
                .appendEncodedPath(MONTH)
                .appendEncodedPath(PATH_ID)
                .appendEncodedPath(PATH_FILE)
                .build();
        return builtUri;
    }

    public static URL buildNetworkUrl (Uri uri) {
        //Convert the Uri to a URL
        URL url = null;
        try {
            url =new URL(uri.toString());
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        return url;
    }

    public static String getJsonFromHttpUrl (URL url) throws IOException {
        //Create an object instance of an open socket
        HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();

        try{
            InputStream in = urlConnection.getInputStream();
            //Read the contents of the InputStream
            Scanner scanner = new Scanner(in);
            scanner.useDelimiter("\\A");
            boolean hasInput = scanner.hasNext();
            if(hasInput) {
                return scanner.next();
            } else {
                return null;
            }
        } finally {
            urlConnection.disconnect();
        }
    }
}
