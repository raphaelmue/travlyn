package org.travlyn.server.apiAccess;

import okhttp3.Call;
import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import org.jetbrains.annotations.NotNull;
import org.travlyn.server.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
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
    private final String requestURL;
    private final OkHttpClient client;

    public APIRequest(String apiURL, Set<Pair<String, String>> parameters) throws MalformedURLException {
        HttpUrl.Builder urlBuilder = Objects.requireNonNull(HttpUrl.parse(apiURL)).newBuilder();
        for (Map.Entry<String, String> entry : parameters) {
            urlBuilder.addQueryParameter(entry.getKey(),entry.getValue());
        }
        client = new OkHttpClient();
        this.requestURL = urlBuilder.build().toString();
    }

    public APIRequest(String apiURL) throws Exception {
        this.requestURL = apiURL;
        client = new OkHttpClient();
    }

    public String performAPICall() throws IOException {
        Request request = new Request.Builder().header("Accept","application/json").url(requestURL).build();
        return client.newCall(request).execute().body().string();
    }
}
