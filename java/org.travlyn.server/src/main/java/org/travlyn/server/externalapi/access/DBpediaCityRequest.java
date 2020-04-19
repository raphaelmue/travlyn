package org.travlyn.server.externalapi.access;

import com.google.gson.*;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.City;

import java.util.Set;

/**
 * Utility class for querying the DBmedia API for a specified city.
 *
 * @author Joshua Schulz
 */
public class DBpediaCityRequest extends DBpediaRequest<City> {
    private static final String BASE_URL = "http://vmdbpedia.informatik.uni-leipzig.de:8080/api/1.0.0/values";

    private String query;
    private Gson gson = new Gson();

    /**
     * Construct DBpedia request
     *
     * @param query specify what should be searched for
     */
    public DBpediaCityRequest(String query) {
        super(BASE_URL);
        this.query = query.replace(" ", "_");
    }

    /**
     * Queries DBmedia API to get basic data about city.
     *
     * @return Filled CityEntity with the fetched data.
     */
    public City getResult() throws QuotaLimitException{
        Set<Pair<String, String>> params = getDefaultHeaders(query);
        params.add(new Pair<>("property", "dbo:abstract"));
        params.add(new Pair<>("property", "dbo:thumbnail"));
        params.add(new Pair<>("property", "georss:point"));


        String result = executeRequest(params);
        JsonArray resultArray;
        try {
            resultArray = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                    getAsJsonArray("bindings");
        } catch (
                JsonSyntaxException syntaxException) {
            //quota limit reached...exclude stop TODO
            throw new QuotaLimitException("DBpedia quota limit is reached by stop request!");
        }

        JsonObject englishContent = this.filterLanguageToEnglish(resultArray);

        try {
            if (englishContent != null) {
                String description = englishContent.getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
                String imageURL = englishContent.getAsJsonObject("dbothumbnail").getAsJsonPrimitive("value").getAsString();
                String[] location = englishContent.getAsJsonObject("georsspoint").getAsJsonPrimitive("value").getAsString().split(" ");

                return new City()
                        .longitude(Double.parseDouble(location[1]))
                        .latitude(Double.parseDouble(location[0]))
                        .name(query)
                        .description(description)
                        .image(imageURL);
            }
        } catch (NullPointerException exception) {
            // invalid search term leads to no results
        }
        return null;
    }
}
