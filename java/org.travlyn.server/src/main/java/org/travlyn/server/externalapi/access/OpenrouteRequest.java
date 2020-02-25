package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.Stop;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Map;

public class OpenrouteRequest {
    private static final String BASE_URL = "https://api.openrouteservice.org/pois";
    private Gson gson = new Gson();

    public ArrayList<Stop> getPOIS(double lon, double lat){
        ArrayList<Stop> resultList = new ArrayList<>();

        HashSet<Pair<String,String>> header = new HashSet<>();
        header.add(new Pair<>("Authorization","5b3ce3597851110001cf62487839b1884ada4627bbe7c52c372087fd"));
        APIRequest request = new APIRequest(BASE_URL,new HashSet<>(),this.genPostBody(),header);
        String apiResult;
        try {
            apiResult = request.performAPICallPOST();
        }catch (IOException e){
            return null;
        }
        JsonArray jsonArray = gson.fromJson(apiResult,JsonObject.class).getAsJsonArray("features");
        for(JsonElement poi : jsonArray){
            JsonObject poiObject = poi.getAsJsonObject();
            Stop stop = new Stop();
            try {
                String name = poiObject.getAsJsonObject("properties").getAsJsonObject("osm_tags").getAsJsonPrimitive("name").getAsString();
                stop.setName(name);
            }catch (NullPointerException nullpointer){
                //stop is not identified with a name --> exclude
                continue;
            }
            stop.setLatitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsLong());
            stop.setLongitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(1).getAsLong());
            resultList.add(stop);
        }
        return resultList;
    }

    private String genPostBody(){
        return "{\"request\":\"pois\",\"geometry\":{\"bbox\":[[" + 8.40 +","+ 49.01 + "],["+8.45+","+49.06+
                "]],\"geojson\":{\"type\":\"Point\",\"coordinates\":["+8.404435 +","+ 49.013513+
                "]},\"buffer\":200},\"filters\":{\"category_group_ids\":[620]}}";
    }
}
