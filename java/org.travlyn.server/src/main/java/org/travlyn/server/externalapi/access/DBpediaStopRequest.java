package org.travlyn.server.externalapi.access;

import com.google.gson.*;
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
        JsonArray resultArray;
        JsonObject englishContent = null;
        try {
             resultArray = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                    getAsJsonArray("bindings");
        }catch (JsonSyntaxException syntaxException){
            //quota limit reached...exclude stop TODO
            return null;
        }

        // filter for english language
        for (JsonElement content : resultArray) {
            if (content.getAsJsonObject().has("dboabstract") &&
                    content.getAsJsonObject().getAsJsonObject("dboabstract").getAsJsonPrimitive("xml:lang").getAsString().equals("en")) {
                englishContent = content.getAsJsonObject();
                break;
            }
        }

        try {
            if(englishContent != null) {
                String description = englishContent.getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
                String imageURL = englishContent.getAsJsonObject("dbothumbnail").getAsJsonPrimitive("value").getAsString();

                //fix image URL in case it is broken
                if (!imageURL.contains("http://commons.wikimedia.org/")){
                    imageURL = "http://commons.wikimedia.org/wiki/" + imageURL.substring(5);
                }

                return new Stop().name(query)
                        .description(description)
                        .image(imageURL);
            }
        } catch (NullPointerException exception) {
            //invalid search term leads to no results
            return null;
        }
        return null;
    }

    private String checkForManualName(String query){
        if (MANUAL_NAMES.containsKey(query)){
            return MANUAL_NAMES.get(query);
        }
        return null;
    }
}
