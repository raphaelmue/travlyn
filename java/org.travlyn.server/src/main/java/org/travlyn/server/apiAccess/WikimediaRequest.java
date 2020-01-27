package org.travlyn.server.apiAccess;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;

/**
 * Utility class for making requests on various actions of the wikimedia api.
 * @author Joshua Schulz
 * @since 1.0
 */
public class WikimediaRequest {
    private static final String BASEAPI = "https://en.wikipedia.org/w/api.php";

    private String serachterm;
    private Gson gson = new Gson();

    public WikimediaRequest(String searchterm) {
        this.serachterm = searchterm;
    }

    /**
     * Gets the first paragraph of the corresponding wiki article and returns it after cleaning all HTML-tags.
     * @return Content of first paragraph as string.
     */
    public String getIntro(){
        Map<String,String> params = new HashMap<>();
        String result;
        JsonObject formattedResult;
        APIRequest request;

        params.put("action","query");
        params.put("prop","extracts");
        params.put("exintro",null);
        params.put("explaintext",null);
        params.put("redirects","1");
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
        JsonObject innerContent= formattedResult.getAsJsonObject("query").getAsJsonObject("pages");
        String title = null;
        for(Map.Entry<String, JsonElement> entry : innerContent.entrySet()){
            title =  entry.getValue().getAsJsonObject().getAsJsonPrimitive("extract").toString();
        }
        return title;
    }

    /**
     * Returns URL to image from wiki. Experimental, because we can not get the main picture, but have to choose from
     * all pictures of the corresponding wiki page. Can take a long time to complete.
     *
     * @return URL to image
     */
    public String getImage(){
        Map<String,String> params = new HashMap<>();
        String result;
        String title = "";
        JsonObject formattedResult;
        APIRequest request;

        params.put("action","query");
        params.put("prop","images");
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
        JsonObject innerContent= formattedResult.getAsJsonObject("query").getAsJsonObject("pages");
        for(Map.Entry<String, JsonElement> entry : innerContent.entrySet()){
                title =  entry.getValue().getAsJsonObject().getAsJsonArray("images").get(0).getAsJsonObject().get("title").toString().split(":")[1];
        }
        params.clear();
        params.put("action","query");
        params.put("prop","imageinfo");
        params.put("iiprop","url");
        params.put("format","json");
        params.put("titles","Image:"+ title.replace(" ","_"));
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
        innerContent = gson.fromJson(result,JsonObject.class).getAsJsonObject("query").getAsJsonObject("pages");
        for(Map.Entry<String, JsonElement> entry : innerContent.entrySet()){
            return entry.getValue().getAsJsonObject().getAsJsonArray("imageinfo").get(0).getAsJsonObject().get("url").toString().replace("\"","");
        }
        return null;
    }
}
