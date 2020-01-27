package org.travlyn.server.apiAccess;

import com.google.gson.Gson;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class DBpediaRequest {
    private static final String BASEAPI = "http://vmdbpedia.informatik.uni-leipzig.de:8080/api/1.0.0/values";

    private String serachterm;
    private Boolean city;
    private Gson gson = new Gson();

    /**
     * Construct DBpedia request
     * @param serachterm specify what should be searched for
     * @param city specify if subject is a city; if false it is assumed that subject is POI
     */
    public DBpediaRequest(String serachterm, boolean city){
        this.serachterm = serachterm;
        this.city = city;
    }

    public String getBasicInfo(){
        Map<String,String> params = new HashMap<>();
        String result;
        APIRequest request;

        params.put("entities",serachterm);
        params.put("format","JSON");
        params.put("pretty","SHORT");
        params.put("oldVersion","false");
        params.put("offset","0");
        params.put("limit","100");
        params.put("key","1234");
        //TODO:new Pair class to change map to set and query multiple properties
        params.put("property","dbo:abstract");
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
        return gson.fromJson(result, JsonObject.class).getAsJsonObject("results").getAsJsonArray("bindings").get(0).getAsJsonObject().getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
    }
}
