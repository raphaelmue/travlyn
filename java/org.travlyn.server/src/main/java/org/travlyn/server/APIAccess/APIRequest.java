package org.travlyn.server.APIAccess;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;

public class APIRequest {
    private final URL requestURL;
    //TODO: Handle Exceptions
    public APIRequest(URL apiURL, Map<String,String> parameters) throws Exception{
        this.requestURL =  apiURL;
    }
    public APIRequest(String apiURL)throws Exception{
        this.requestURL = new URL(apiURL);
    }

    public String performAPICall() throws Exception{
        HttpURLConnection con = (HttpURLConnection) requestURL.openConnection();
        con.setRequestMethod("GET");
        int responseCode = con.getResponseCode();
        BufferedReader in = new BufferedReader(
                new InputStreamReader(con.getInputStream()));
        String inputLine;
        StringBuilder content = new StringBuilder();
        while ((inputLine = in.readLine()) != null) {
            content.append(inputLine);
        }
        in.close();
        con.disconnect();

        return content.toString();
    }
}
