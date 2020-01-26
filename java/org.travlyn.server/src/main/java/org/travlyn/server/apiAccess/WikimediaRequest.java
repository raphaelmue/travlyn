package org.travlyn.server.apiAccess;

import com.fasterxml.jackson.databind.util.JSONPObject;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.internal.LinkedHashTreeMap;
import com.google.gson.internal.LinkedTreeMap;
import springfox.documentation.spring.web.json.Json;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

public class WikimediaRequest {
    private static final String BASEAPI = "https://en.wikipedia.org/w/api.php";

    private String serachterm;
    private APIRequest request;
    private Gson gson = new Gson();

    public WikimediaRequest(String searchterm) {
        this.serachterm = searchterm;
    }

    public String getIntro(){
        Map<String,String> params = new HashMap<String, String>();
        String result = "";
        JsonObject formattedResult = new JsonObject();
        params.put("action","query");
        params.put("prop","extracts");
        params.put("exintro",null);
        params.put("format","json");
        params.put("titles",serachterm);
        try {
            request = new APIRequest(BASEAPI,params);
        }catch (MalformedURLException ex){
            //request could not be build due to a malformed URL
            return null;
        }
        try {
            result = request.performAPICall();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
        formattedResult = gson.fromJson(result, JsonObject.class);
        String innerContent= formattedResult.getAsJsonObject("query").getAsJsonObject("pages").toString();
        //TODO: examine alternative ways to get correct tag
        String[] arr = innerContent.split("extract\":");
        //TODO: do not just delte html tags but parse string in correct way
        return arr[1].split("}")[0].replaceAll("\\<.*?\\>", "");
    }
}
