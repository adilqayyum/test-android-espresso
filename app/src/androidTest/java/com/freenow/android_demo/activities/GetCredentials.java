package com.freenow.android_demo.activities;

import com.google.gson.*;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.net.MalformedURLException;
import java.io.*;

@SuppressWarnings("WeakerAccess")
public class GetCredentials{

    static String uName;
    static String pWord;

    public void credentialsMethod() throws IOException {
        String sURL = "https://randomuser.me/api/?seed=a1f30d446f820665";
        try {
            URL url = new URL(sURL);
            URLConnection request = url.openConnection();
            request.connect();

            // Convert to a JSON object to print data
            JsonParser jp = new JsonParser();
            JsonElement root = jp.parse(new InputStreamReader((InputStream) request.getContent())); //Convert the input stream to a json element
            JsonObject rootObject = root.getAsJsonObject();
            JsonArray jsonArray = rootObject.getAsJsonArray("results");
            JsonElement item = jsonArray.get(0);
            JsonObject jsonList = item.getAsJsonObject();
            JsonObject loginDetails = jsonList.getAsJsonObject("login");
            String username = loginDetails.get("username").toString();
            String password = loginDetails.get("password").toString();
            uName = username.replaceAll("^\"|\"$", "");
            pWord = password.replaceAll("^\"|\"$", "");
            System.out.print(uName+'\n');
            System.out.print(pWord);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }
}

