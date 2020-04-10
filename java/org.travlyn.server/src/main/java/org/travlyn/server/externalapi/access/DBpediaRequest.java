package org.travlyn.server.externalapi.access;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.travlyn.server.service.TravlynService;
import org.travlyn.server.util.Pair;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashSet;
import java.util.Set;

/**
 * Abstract class for executing requests to DBPedia
 *
 * @param <K> Object to be parsed to
 */
public abstract class DBpediaRequest<K> implements Request<K> {
    private static final String API_KEY;
    private static final Logger logger = LoggerFactory.getLogger(DBpediaRequest.class);
    static {
        String readLine="1234";
        try (
                BufferedReader fileReader = new BufferedReader(new InputStreamReader(
                        DBpediaRequest.class.getClassLoader().getResourceAsStream("DBpediaKey.txt")))) {
            readLine = fileReader.readLine();
        } catch (IOException e) {
            logger.error(e.getMessage());
        }
        API_KEY = readLine;
    }
    private final String baseURL;

    DBpediaRequest(String baseURL) {
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
        params.add(new Pair<>("key", API_KEY));
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
