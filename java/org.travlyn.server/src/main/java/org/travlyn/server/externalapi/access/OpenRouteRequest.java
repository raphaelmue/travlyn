package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.travlyn.server.service.TravlynService;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.db.CategoryEntity;
import org.travlyn.shared.model.db.CityEntity;
import org.travlyn.shared.model.db.StopEntity;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OpenRouteRequest implements Request<Set<StopEntity>> {
    private static final String BASE_URL = "https://api.openrouteservice.org/pois";
    private static final HashSet<Integer> excludedCategories = new HashSet<>();

    private final Gson gson = new Gson();
    private Map<Integer, CategoryEntity> categoryList;

    private final double latitude;
    private final double longitude;
    private final CityEntity city;

    public OpenRouteRequest(double latitude, double longitude, CityEntity city, Map<Integer, CategoryEntity> categoryList) {
        //set Categories that should be excluded (necessary due to filter limitations at ORS)
        excludedCategories.add(136);
        excludedCategories.add(237);
        excludedCategories.add(238);
        excludedCategories.add(231);
        excludedCategories.add(234);
        excludedCategories.add(261);
        excludedCategories.add(262);
        excludedCategories.add(267);
        excludedCategories.add(271);
        excludedCategories.add(280);
        excludedCategories.add(282);
        excludedCategories.add(283);
        excludedCategories.add(288);
        excludedCategories.add(290);
        excludedCategories.add(293);
        excludedCategories.add(297);
        excludedCategories.add(298);
        excludedCategories.add(302);
        excludedCategories.add(303);
        excludedCategories.add(307);
        excludedCategories.add(623);
        excludedCategories.add(624);
        excludedCategories.add(626);


        this.latitude = latitude;
        this.longitude = longitude;
        this.city = city;
        this.categoryList = categoryList;
    }

    @Override
    public Set<StopEntity> getResult() {
        Set<StopEntity> resultList = new HashSet<>();
        Set<Pair<String, String>> header = new HashSet<>();
        header.add(new Pair<>("Authorization", "5b3ce3597851110001cf62487839b1884ada4627bbe7c52c372087fd"));

        for (int row = -1; row <= 1; row++) {
            for (int column = -1; column <= 1; column++) {
                APIRequest request = new APIRequest(BASE_URL, new HashSet<>(), this.genPostBody(
                        BigDecimal.valueOf(latitude + (row * 0.02)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        BigDecimal.valueOf(longitude + (column * 0.02)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        BigDecimal.valueOf(latitude + ((row + 1) * 0.02)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        BigDecimal.valueOf(longitude + ((column + 1) * 0.02)).setScale(2, RoundingMode.FLOOR).doubleValue(),
                        latitude + (row * 0.02),
                        longitude + (column * 0.02)), header);
                String apiResult;
                try {
                    apiResult = request.performAPICallPOST();
                    System.out.println(apiResult);
                } catch (IOException e) {
                    continue;
                }
                resultList.addAll(getStopsFromJson(gson.fromJson(apiResult, JsonObject.class).getAsJsonArray("features")));
            }
        }
        return resultList;
    }

    private Set<StopEntity> getStopsFromJson(JsonArray jsonArray) {
        Set<String> stopNames = new HashSet<>();
        Set<StopEntity> resultList = new HashSet<>();

        for (JsonElement poi : jsonArray) {
            JsonObject poiObject = poi.getAsJsonObject();
            StopEntity stop = new StopEntity();
            try {
                String name = poiObject.getAsJsonObject("properties").getAsJsonObject("osm_tags").getAsJsonPrimitive("name").getAsString();
                if (stopNames.contains(name)) {
                    continue;
                }
                JsonObject categories = poiObject.getAsJsonObject("properties").getAsJsonObject("category_ids");
                CategoryEntity category = new CategoryEntity();
                for (Map.Entry<String, JsonElement> entry : categories.entrySet()) {
                    category = categoryList.get(Integer.parseInt(entry.getKey()));
                    if (category == null) {
                        category = new CategoryEntity();
                        category.setId(Integer.parseInt(entry.getKey()));
                        category.setName(entry.getValue().getAsJsonObject().getAsJsonPrimitive("category_name").getAsString());
                        categoryList.put(category.getId(), category);
                    }
                }
                stop.setCategory(category);
                if (excludedCategories.contains(category.getId())){
                    continue;
                }
                stop.setName(name);
                stopNames.add(name);
            } catch (NullPointerException nullPointer) {
                //stop is not identified with a name --> exclude
                continue;
            }
            stop.setLatitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(0).getAsDouble());
            stop.setLongitude(poiObject.getAsJsonObject("geometry").getAsJsonArray("coordinates").get(1).getAsDouble());
            stop.setCity(city);
            resultList.add(stop);
        }
        return resultList;
    }

    private String genPostBody(double lat1, double lon1, double lat2, double lon2, double middleLat, double middleLon) {
        return "{\"request\":\"pois\",\"geometry\":{\"bbox\":[[" + lon1 + "," + lat1 + "],[" + lon2 + "," + lat2 +
                "]],\"geojson\":{\"type\":\"Point\",\"coordinates\":[" + middleLon + "," + middleLat +
                "]},\"buffer\":2000},\"filters\":{\"category_group_ids\":[620,130,220,330,260]}}";
    }
}
