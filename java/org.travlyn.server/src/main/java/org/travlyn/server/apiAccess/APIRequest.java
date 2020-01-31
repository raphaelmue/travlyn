package org.travlyn.server.apiAccess;

import okhttp3.*;
import org.travlyn.server.util.Pair;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;

/**
 * Generic utility class to fetch data from API via GET request.
 *
 * @author Joshua Schulz
 * @since 1.0
 */
public class APIRequest {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final String requestURL;
    private final OkHttpClient client;
    private String postBody;

    public APIRequest(String apiURL, Set<Pair<String, String>> parameters) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(apiURL)).newBuilder();
        for (Map.Entry<String, String> entry : parameters) {
            urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
        }
        client = new OkHttpClient();
        this.requestURL = urlBuilder.build().toString();
    }

    public APIRequest(String apiURL, Set<Pair<String, String>> parameters, String postBody){
        this(apiURL,parameters);
        this.postBody = postBody;
    }

    public APIRequest(String apiURL) throws Exception {
        this.requestURL = apiURL;
        client = new OkHttpClient();
    }

    public String performAPICallGET() throws IOException {
        Request request = new Request.Builder().header("Accept","application/json").url(requestURL).build();
        return client.newCall(request).execute().body().string();
    }

    public String performAPICallPOST() throws IOException {
        Request request = new Request.Builder()
                .url(requestURL)
                .post(RequestBody.create(postBody, MEDIA_TYPE_JSON))
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }
}
