package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.Stop;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

public class DBpediaStopRequest extends DBpediaRequest<Stop> {
    private static final HashMap<String,String> MANUAL_NAMES;
        static {
            MANUAL_NAMES = new HashMap<>();
            MANUAL_NAMES.put("Schloss_Karlsruhe", "Karlsruhe_Palace");
            MANUAL_NAMES.put("Pyramide","Karlsruhe_Pyramid");
            MANUAL_NAMES.put("Großherzog-Karl-Friedrich-Denkmal","Charles_Frederick%2C_Grand_Duke_of_Baden");
        }

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
        //check for manual name setting
        String manualQuery = this.checkForManualName(query);
        if (manualQuery != null){
            query = manualQuery;
        }

        Set<Pair<String, String>> params = getDefaultHeaders(query);
        params.add(new Pair<>("property", "dbo:abstract"));
        params.add(new Pair<>("property", "dbo:thumbnail"));

        String result = executeRequest(params);
        JsonObject englishContent;
        try {
            englishContent = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                    getAsJsonArray("bindings").get(0).getAsJsonObject();
        }catch (JsonSyntaxException syntaxException){
            //quota limit reached...exclude stop TODO
            return null;
        }
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

    private String checkForManualName(String query){
        if (MANUAL_NAMES.containsKey(query)){
            return MANUAL_NAMES.get(query);
        }
        return null;
    }
}
