package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Stop;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

public class DBpediaPOIRequest implements DBpediaRequest<Stop> {
    private static final String BASE_API = "http://vmdbpedia.informatik.uni-leipzig.de:8080/api/1.0.0/values";

    private String serachterm;
    private Gson gson = new Gson();

    public DBpediaPOIRequest(String serachterm) {
        serachterm = serachterm.replace(" ","_");
        this.serachterm = serachterm;
    }

    @Override
    public Stop getResult() {
        Set<Pair<String, String>> params = new HashSet<>();
        String result;
        APIRequest request;

        params.add(new Pair<>("entities", serachterm));
        params.add(new Pair<>("format", "JSON"));
        params.add(new Pair<>("pretty", "SHORT"));
        params.add(new Pair<>("oldVersion", "false"));
        params.add(new Pair<>("offset", "0"));
        params.add(new Pair<>("limit", "100"));
        params.add(new Pair<>("key", "1234"));
        params.add(new Pair<>("property", "dbo:abstract"));
        params.add(new Pair<>("property", "dbo:thumbnail"));
        request = new APIRequest(BASE_API, params);

        try {
            result = request.performAPICallGET();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
        JsonObject englishContent = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                getAsJsonArray("bindings").get(0).getAsJsonObject();
        try {
            String description = englishContent.getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
            String imageURL = englishContent.getAsJsonObject("dbothumbnail").getAsJsonPrimitive("value").getAsString();

            return new Stop().name(serachterm)
                            .description(description)
                            .image(imageURL);
        } catch (NullPointerException exception) {
            //invalid search term leads to no results
            return null;
        }
    }
}
