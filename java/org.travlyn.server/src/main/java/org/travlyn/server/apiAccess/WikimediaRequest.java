package org.travlyn.server.apiAccess;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

public class WikimediaRequest {
    private static final String BASEAPI = "https://en.wikipedia.org/w/api.php";

    private String serachterm;
    private APIRequest request;
    public WikimediaRequest(String searchterm) {
        this.serachterm = searchterm;
    }

    public String getIntro(){
        Map<String,String> params = new HashMap<String, String>();
        params.put("action","query");
        params.put("prop","extracts");
        params.put("","exintro");
        params.put("format","json");
        params.put("titles",serachterm);
        try {
            request = new APIRequest(BASEAPI,params);
        }catch (MalformedURLException ex){
            //request could not be build due to a malformed URL
            return null;
        }
        try {
            return request.performAPICall();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
    }
}
