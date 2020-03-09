package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.travlyn.server.externalapi.APIRequest;
import org.travlyn.server.externalapi.DBpediaRequest;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.City;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Utility class for querying the DBmedia API for a specified city.
 *
 * @author Joshua Schulz
 */
public class DBpediaCityRequest implements DBpediaRequest<City> {
    private static final String BASE_API = "http://vmdbpedia.informatik.uni-leipzig.de:8080/api/1.0.0/values";

    private String serachterm;
    private Gson gson = new Gson();

    /**
     * Construct DBpedia request
     *
     * @param query specify what should be searched for
     */
    public DBpediaCityRequest(String query) {
        this.serachterm = query.replace(" ", "_");
    }

    /**
     * Queries DBmedia API to get basic data about city.
     *
     * @return Filled CityEntity with the fetched data.
     */
    public City getResult() {
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
        params.add(new Pair<>("property", "georss:point"));
        request = new org.travlyn.server.externalapi.APIRequest(BASE_API, params);

        try {
            result = request.performAPICallGET();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
        JsonArray resultArray = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                getAsJsonArray("bindings");

        JsonObject englishContent = null;

        // filter for english language
        for (JsonElement content : resultArray) {
            if (content.getAsJsonObject().has("dboabstract") &&
                    content.getAsJsonObject().getAsJsonObject("dboabstract").getAsJsonPrimitive("xml:lang").getAsString().equals("en")) {
                englishContent = content.getAsJsonObject();
                break;
            }
        }

        try {
            if (englishContent != null) {
                String description = englishContent.getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
                String imageURL = englishContent.getAsJsonObject("dbothumbnail").getAsJsonPrimitive("value").getAsString();
                String[] location = englishContent.getAsJsonObject("georsspoint").getAsJsonPrimitive("value").getAsString().split(" ");

                return new City()
                        .longitude(Double.parseDouble(location[1]))
                        .latitude(Double.parseDouble(location[0]))
                        .name(serachterm)
                        .description(description)
                        .image(imageURL);
            }
        } catch (NullPointerException exception) {
            // invalid search term leads to no results
        }
        return null;
    }
}
