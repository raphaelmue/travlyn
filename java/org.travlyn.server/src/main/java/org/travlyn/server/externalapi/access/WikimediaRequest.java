package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Map;

/**
 * Utility class for making requests on various actions of the wikimedia api.
 *
 * @author Joshua Schulz
 * @since 1.0
 */
public class WikimediaRequest {
    private static final String BASE_API = "https://en.wikipedia.org/w/api.php";

    private String serachterm;
    private Gson gson = new Gson();

    public WikimediaRequest(String searchterm) {
        this.serachterm = searchterm;
    }

    /**
     * Gets the first paragraph of the corresponding wiki article and returns it after cleaning all HTML-tags.
     *
     * @return Content of first paragraph as string.
     */
    public String getIntro() {
        HashSet<Pair<String, String>> params = new HashSet<>();
        String result;
        JsonObject formattedResult;
        APIRequest request;

        params.add(new Pair<>("action", "query"));
        params.add(new Pair<>("prop", "extracts"));
        params.add(new Pair<>("exintro", null));
        params.add(new Pair<>("explaintext", null));
        params.add(new Pair<>("redirects", "1"));
        params.add(new Pair<>("format", "json"));
        params.add(new Pair<>("titles", serachterm));
        request = new APIRequest(BASE_API, params);
        try {
            result = request.performAPICallGET();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
        formattedResult = gson.fromJson(result, JsonObject.class);
        JsonObject innerContent = formattedResult.getAsJsonObject("query").getAsJsonObject("pages");
        String title = null;
        for (Map.Entry<String, JsonElement> entry : innerContent.entrySet()) {
            title = entry.getValue().getAsJsonObject().getAsJsonPrimitive("extract").toString();
        }
        if (title != null) {
            title = title.replace("\\n", "");
        }
        return title;
    }

    /**
     * Returns URL to image from wiki. Experimental, because we can not get the main picture, but have to choose from
     * all pictures of the corresponding wiki page. Can take a long time to complete.
     *
     * @return URL to image
     */
    public String getImage() {
        HashSet<Pair<String, String>> params = new HashSet<>();
        String result;
        String title = "";
        JsonObject formattedResult;
        APIRequest request;

        params.add(new Pair<>("action", "query"));
        params.add(new Pair<>("prop", "images"));
        params.add(new Pair<>("format", "json"));
        params.add(new Pair<>("titles", serachterm));
        request = new APIRequest(BASE_API, params);
        try {
            result = request.performAPICallGET();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
        formattedResult = gson.fromJson(result, JsonObject.class);
        JsonObject innerContent = formattedResult.getAsJsonObject("query").getAsJsonObject("pages");
        for (Map.Entry<String, JsonElement> entry : innerContent.entrySet()) {
            title = entry.getValue().getAsJsonObject().getAsJsonArray("images").get(0).getAsJsonObject().get("title").toString().split(":")[1];
            title = title.substring(0, title.length() - 1);
        }
        params.clear();
        params.add(new Pair<>("action", "query"));
        params.add(new Pair<>("prop", "imageinfo"));
        params.add(new Pair<>("iiprop", "url"));
        params.add(new Pair<>("format", "json"));
        params.add(new Pair<>("titles", "Image:" + title.replace(" ", "_")));
        request = new APIRequest(BASE_API, params);
        try {
            result = request.performAPICallGET();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
        innerContent = gson.fromJson(result, JsonObject.class).getAsJsonObject("query").getAsJsonObject("pages");
        Iterator<Map.Entry<String, JsonElement>> iterator = innerContent.entrySet().iterator();

        if (!iterator.hasNext()){
            return null;
        }
        return iterator.next().getValue().getAsJsonObject().getAsJsonArray("imageinfo").get(0).getAsJsonObject().get("url").toString().replace("\"", "");
    }
}
