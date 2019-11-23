package de.travlyn.prototyping;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.util.Map;

public class APIRequest {
    private final URL requestURL;
    //TODO: Handle Exceptions
    public APIRequest(URL apiURL, Map<String,String> parameters,String apikey) throws Exception{
        requestURL=new URL(new URL(apiURL,"?api_key="+apikey),ParameterStringBuilder.getParamsString(parameters));
    }
    public APIRequest(String apiURL,String apikey)throws Exception{
        requestURL=new URL(apiURL+"&api_key="+apikey);
    }

    public APIRequest(String url) throws Exception{
        requestURL = new URL(url);
    }

    public String performAPIGetCall() throws Exception{
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

    public String performAPIPostCall(String body) throws Exception{
        HttpURLConnection connection = (HttpURLConnection) requestURL.openConnection();
        connection.setRequestMethod( "POST" );
        connection.setDoInput( true );
        connection.setDoOutput( true );
        connection.setUseCaches( false );
        connection.setRequestProperty( "Content-Type",
                "application/json" );
        String key = "";
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                Test.class.getResourceAsStream("OpenRouteService.txt")))) {
            key = fileReader.readLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
        connection.setRequestProperty("Authorization", key);
        connection.setRequestProperty( "Content-Length", String.valueOf(body.length()) );

        OutputStreamWriter writer = new OutputStreamWriter( connection.getOutputStream() );
        writer.write( body );
        writer.flush();


        BufferedReader reader = new BufferedReader(
                new InputStreamReader(connection.getInputStream()) );

        StringBuilder content = new StringBuilder();
        for ( String line; (line = reader.readLine()) != null; )
        {
            content.append(line);
        }

        writer.close();
        reader.close();
        return content.toString();
    }

}
