package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.db.CityEntity;
import org.travlyn.shared.model.db.StopEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashSet;
import java.util.Set;

public class OpenrouteRequest {
    private static final String BASE_URL = "https://api.openrouteservice.org/pois";


    private Gson gson = new Gson();

    public Set<StopEntity> getPOIS(double lon, double lat, CityEntity city){
        HashSet<StopEntity> resultList = new HashSet<>();

        HashSet<Pair<String,String>> header = new HashSet<>();
        header.add(new Pair<>("Authorization","5b3ce3597851110001cf62487839b1884ada4627bbe7c52c372087fd"));

        for (int row = -1; row <= 1 ; row++) {
            for (int column = -1; column <= 1; column++) {
                APIRequest request = new APIRequest(BASE_URL, new HashSet<>(), this.genPostBody(
                        new BigDecimal(lat + (row * 0.04)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        new BigDecimal(lon + (column * 0.04)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        new BigDecimal(lat+((row+1)*0.04)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        new BigDecimal(lon + ((column+1) * 0.04)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        lat + (row * 0.04),
                        lon + (column * 0.04)), header);
                String apiResult;
                try {
                    apiResult = request.performAPICallPOST();
                } catch (IOException e) {
                    return null;
                }
                JsonArray jsonArray = gson.fromJson(apiResult, JsonObject.class).getAsJsonArray("features");
                for (JsonElement poi : jsonArray) {
                    JsonObject poiObject = poi.getAsJsonObject();
                    StopEntity stop = new StopEntity();
                    try {
                        String name = poiObject.getAsJsonObject("properties").getAsJsonObject("osm_tags").getAsJsonPrimitive("name").getAsString();
                        stop.setName(name);
                    } catch (NullPointerException nullPointer) {
                        //stop is not identified with a name --> exclude
                        continue;
                    }
                    stop.setLatitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsDouble());
                    stop.setLongitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(1).getAsDouble());
                    stop.setCity(city);
                    //stop.getCity().setStops(null);
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
