package org.travlyn.server.apiAccess;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.City;

import java.io.IOException;
import java.net.MalformedURLException;
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
     * @param serachterm specify what should be searched for
     */
    public DBpediaCityRequest(String serachterm) {
        this.serachterm = serachterm;
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
        request = new APIRequest(BASE_API, params);

        try {
            result = request.performAPICallGET();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
        JsonObject englishContent = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                getAsJsonArray("bindings").get(0).getAsJsonObject();
        String description = englishContent.getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
        String imageURL = englishContent.getAsJsonObject("dbothumbnail").getAsJsonPrimitive("value").getAsString();
        City returnValue = new City();
        returnValue.setDescription(description);
        returnValue.setImage(imageURL);
        returnValue.setName(serachterm);
        return returnValue;
    }
}
