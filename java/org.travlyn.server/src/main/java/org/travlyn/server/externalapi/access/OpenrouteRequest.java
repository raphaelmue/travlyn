package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.Stop;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.HashSet;

public class OpenrouteRequest {
    private static final String BASE_URL = "https://api.openrouteservice.org/pois";


    private Gson gson = new Gson();

    public ArrayList<Stop> getPOIS(double lon, double lat){
        ArrayList<Stop> resultList = new ArrayList<>();

        HashSet<Pair<String,String>> header = new HashSet<>();
        header.add(new Pair<>("Authorization","5b3ce3597851110001cf62487839b1884ada4627bbe7c52c372087fd"));

        for (int row = -1; row <= 1 ; row++) {
            for (int column = -1; column <= 1; column++) {
                APIRequest request = new APIRequest(BASE_URL, new HashSet<>(), this.genPostBody(
                        new BigDecimal(lat).setScale(2, RoundingMode.HALF_UP).doubleValue() + ((row+1) * 0.05),
                        new BigDecimal(lon).setScale(2, RoundingMode.HALF_UP).doubleValue() + ((column+1) * 0.05),
                        new BigDecimal(lat).setScale(2, RoundingMode.HALF_UP).doubleValue()+(row*0.05),
                        new BigDecimal(lon).setScale(2, RoundingMode.HALF_UP).doubleValue() + (column * 0.05),
                        lat + (row * 0.05),
                        lon + (column * 0.05)), header);
                String apiResult;
                try {
                    apiResult = request.performAPICallPOST();
                } catch (IOException e) {
                    return null;
                }
                JsonArray jsonArray = gson.fromJson(apiResult, JsonObject.class).getAsJsonArray("features");
                for (JsonElement poi : jsonArray) {
                    JsonObject poiObject = poi.getAsJsonObject();
                    Stop stop = new Stop();
                    try {
                        String name = poiObject.getAsJsonObject("properties").getAsJsonObject("osm_tags").getAsJsonPrimitive("name").getAsString();
                        stop.setName(name);
                    } catch (NullPointerException nullPointer) {
                        //stop is not identified with a name --> exclude
                        continue;
                    }
                    stop.setLatitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsDouble());
                    stop.setLongitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(1).getAsDouble());
                    resultList.add(stop);
                }
            }
        }
        return resultList;
    }

    private String genPostBody(double lat1, double lon1, double lat2, double lon2,double middleLat, double middleLon){
        return "{\"request\":\"pois\",\"geometry\":{\"bbox\":[[" + lon1 +","+ lat1 + "],["+lon2+","+lat2+
                "]],\"geojson\":{\"type\":\"Point\",\"coordinates\":["+ middleLon +","+ middleLat +
                "]},\"buffer\":200},\"filters\":{\"category_group_ids\":[620,130,220]}}";
    }
}
