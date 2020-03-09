package org.travlyn.server.externalapi.access;

import org.travlyn.server.util.Pair;

import java.io.IOException;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for executing requests to DBPedia
 *
 * @param <K> Object to be parsed to
 */
public abstract class DBPediaRequest<K> implements Request<K> {
    private final String baseURL;

    DBPediaRequest(String baseURL) {
        this.baseURL = baseURL;
    }

    /**
     * Returns the default headers for a DBPedia Request
     *
     * @param query query to search for
     * @return Set of parameters
     */
    final Set<Pair<String, String>> getDefaultHeaders(String query) {
        Set<Pair<String, String>> params = new HashSet<>();
        params.add(new Pair<>("entities", query));
        params.add(new Pair<>("format", "JSON"));
        params.add(new Pair<>("pretty", "SHORT"));
        params.add(new Pair<>("oldVersion", "false"));
        params.add(new Pair<>("offset", "0"));
        params.add(new Pair<>("limit", "100"));
        params.add(new Pair<>("key", "1234"));
        return params;
    }

    /**
     * Executes the request with the given parameters. Returns null, if result is empty.
     *
     * @param params headers
     * @return Request result
     */
    final String executeRequest(Set<Pair<String, String>> params) {
        APIRequest request = new APIRequest(baseURL, params);
        try {
            return request.performAPICallGET();
        } catch (IOException e) {
            //request could not be made due to some network errors
            return null;
        }
    }
}
