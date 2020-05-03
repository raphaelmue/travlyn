package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.Stop;

import java.util.HashMap;
import java.util.Set;

public class DBpediaStopRequest extends DBpediaRequest<Stop> {
    private static final HashMap<String, String> MANUAL_NAMES;

    static {
        MANUAL_NAMES = new HashMap<>();
        MANUAL_NAMES.put("Schloss_Karlsruhe", "Karlsruhe_Palace");
        MANUAL_NAMES.put("Pyramide", "Karlsruhe_Pyramid");
        MANUAL_NAMES.put("Zoologischer_Stadtgarten", "Karlsruhe_Zoo");
        MANUAL_NAMES.put("Badische_Landesbibliothek", "Baden_State_Library");
        MANUAL_NAMES.put("ZKM_|_Medienmuseum", "Center_for_Art_and_Media_Karlsruhe");

        //exclude false positives for Karlsruhe
        MANUAL_NAMES.put("Heidesee", "xyz");
        MANUAL_NAMES.put("Flora", "xyz");
        MANUAL_NAMES.put("Orpheus", "xyz");
        MANUAL_NAMES.put("Fennek", "xyz");
        MANUAL_NAMES.put("Hase", "xyz");
        MANUAL_NAMES.put("Herz-Jesu-Kirche", "xyz");
        MANUAL_NAMES.put("Emu", "xyz");
        MANUAL_NAMES.put("Onager", "xyz");
        MANUAL_NAMES.put("Gondoletta", "xyz");
        MANUAL_NAMES.put("Temple", "xyz");
        MANUAL_NAMES.put("ZKM_|_Museum_für_Neue_Kunst","xyz");
    }

    private static String baseUrl = "http://vmdbpedia.informatik.uni-leipzig.de:8080/api/1.0.0/values";

    private String query;
    private Gson gson = new Gson();


    public DBpediaStopRequest(String query) {
        super(baseUrl);
        query = query.replace(" ", "_");
        this.query = query;
    }

    public static void setBaseUrl(String baseUrl) {
        DBpediaStopRequest.baseUrl = baseUrl;
    }

    @Override
    public Stop getResult() {
        //check for manual name setting
        String manualQuery = this.checkForManualName(query);
        if (manualQuery != null) {
            query = manualQuery;
        }

        Set<Pair<String, String>> params = getDefaultHeaders(query);
        params.add(new Pair<>("property", "dbo:abstract"));
        params.add(new Pair<>("property", "dbo:thumbnail"));

        String result = executeRequest(params);
        JsonArray resultArray;
        try {
            resultArray = gson.fromJson(result, JsonObject.class).getAsJsonObject("results").
                    getAsJsonArray("bindings");
        } catch (JsonSyntaxException syntaxException) {
            throw new QuotaLimitException("DBpedia quota limit is reached by stop request!");
        }

        JsonObject englishContent = this.filterLanguageToEnglish(resultArray);

        try {
            if (englishContent != null) {
                String description = englishContent.getAsJsonObject("dboabstract").getAsJsonPrimitive("value").getAsString();
                String imageURL = englishContent.getAsJsonObject("dbothumbnail").getAsJsonPrimitive("value").getAsString();

                //fix image URL in case it is broken
                if (!imageURL.contains("http://commons.wikimedia.org/")) {
                    imageURL = "http://commons.wikimedia.org/wiki/" + imageURL.substring(5);
                }

                imageURL = imageURL.replace("http://", "https://");

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

    private String checkForManualName(String query) {
        if (MANUAL_NAMES.containsKey(query)) {
            return MANUAL_NAMES.get(query);
        }
        return null;
    }
}
