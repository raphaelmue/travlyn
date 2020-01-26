package org.travlyn.server.APIAccess;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Map;

public class APIRequest {
    private final URL requestURL;
    public APIRequest(String apiURL, Map<String,String> parameters) throws MalformedURLException {
        String completeURL = apiURL + ParameterStringBuilder.getParamString(parameters);
        this.requestURL = new URL(completeURL);
    }
    public APIRequest(String apiURL)throws Exception{
        this.requestURL = new URL(apiURL);
    }

    public String performAPICall() throws IOException {
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
