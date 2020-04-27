package org.travlyn.server.externalapi.access;

import okhttp3.*;
import org.travlyn.server.util.Pair;

import java.io.IOException;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.TimeUnit;

/**
 * Generic utility class to fetch data from API via GET request.
 *
 * @author Joshua Schulz
 * @since 1.0
 */
public class APIRequest {
    public static final MediaType MEDIA_TYPE_JSON = MediaType.parse("application/json; charset=utf-8");

    private final String requestURL;
    private final OkHttpClient client = new OkHttpClient.Builder()
            .connectTimeout(2, TimeUnit.SECONDS) // For testing purposes
            .readTimeout(2, TimeUnit.SECONDS) // For testing purposes
            .writeTimeout(2, TimeUnit.SECONDS)
            .build();

    private String postBody;
    private Set<Pair<String, String>> header;

    public APIRequest(String apiURL, Set<Pair<String, String>> parameters, String postBody, Set<Pair<String, String>> header) {
        this(apiURL, parameters, postBody);
        this.header = header;
    }

    public APIRequest(String apiURL, Set<Pair<String, String>> parameters) {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(apiURL)).newBuilder();
        for (Map.Entry<String, String> entry : parameters) {
            urlBuilder.addQueryParameter(entry.getKey(), entry.getValue());
        }
        this.requestURL = urlBuilder.build().toString();
    }

    public APIRequest(String apiURL, Set<Pair<String, String>> parameters, String postBody) {
        this(apiURL, parameters);
        this.postBody = postBody;
    }

    public String performAPICallGET() throws IOException {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        this.setHeader(builder);
        okhttp3.Request request = builder.url(requestURL).build();
        Response response = client.newCall(request).execute();
        final String json = response.body().string();
        return json;
    }

    public String performAPICallPOST() throws IOException {
        okhttp3.Request.Builder builder = new okhttp3.Request.Builder();
        this.setHeader(builder);
        okhttp3.Request request = builder.url(requestURL)
                .post(RequestBody.create(postBody, MEDIA_TYPE_JSON))
                .build();

        Response response = client.newCall(request).execute();
        return response.body().string();
    }

    private void setHeader(okhttp3.Request.Builder builder) {
        builder.header("Accept", "application/json");
        if (header != null && !header.isEmpty()) {
            //if header are provided
            for (Map.Entry<String, String> entry : header) {
                builder.header(entry.getKey(), entry.getValue());
            }
        }
    }
}
