package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.Stop;

import java.util.Set;

public class DBpediaStopRequest extends DBpediaRequest<Stop> {
    private static final String BASE_URL = "http://vmdbpedia.informatik.uni-leipzig.de:8080/api/1.0.0/values";

    private String query;
    private Gson gson = new Gson();

    public DBpediaStopRequest(String query) {
        super(BASE_URL);
        query = query.replace(" ", "_");
        this.query = query;
    }

    @Override
    public Stop getResult() {
        Set<Pair<String, String>> params = getDefaultHeaders(query);
        params.add(new Pair<>("property", "dbo:abstract"));
        params.add(new Pair<>("property", "dbo:thumbnail"));

        String result = executeRequest(params);
        JsonObject englishContent = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                getAsJsonArray("bindings").get(0).getAsJsonObject();
        try {
            String description = englishContent.getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
            String imageURL = englishContent.getAsJsonObject("dbothumbnail").getAsJsonPrimitive("value").getAsString();

            return new Stop().name(query)
                    .description(description)
                    .image(imageURL);
        } catch (NullPointerException exception) {
            //invalid search term leads to no results
            return null;
        }
    }
}
