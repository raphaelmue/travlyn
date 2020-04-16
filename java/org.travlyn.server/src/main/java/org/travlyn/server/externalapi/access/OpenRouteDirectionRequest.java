package org.travlyn.server.externalapi.access;

import com.google.gson.Gson;
import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public abstract class OpenRouteDirectionRequest implements Request<ExecutionInfo> {
    private static final String PROFILE = "foot-walking";
    private static final String BASE_URL = "https://api.openrouteservice.org/v2/directions/"+PROFILE+"/geojson";
    private final Gson gson = new Gson();

    @Override
    public abstract ExecutionInfo getResult();

    protected JsonObject makeAPICall(List<Pair<Double,Double>> wayPoints){
        Set<Pair<String, String>> header = new HashSet<>();
        header.add(new Pair<>("Authorization", "5b3ce3597851110001cf62487839b1884ada4627bbe7c52c372087fd"));
        header.add(new Pair<>("Content-Type","application/json; charset=utf-8"));
        header.add(new Pair<>("Accept","application/json, application/geo+json, application/gpx+xml, img/png; charset=utf-8"));

        APIRequest request = new APIRequest(BASE_URL,new HashSet<>(),this.genPostBody(wayPoints),header);
        String apiResult;
        try {
            apiResult = request.performAPICallPOST();
        } catch (IOException e) {
            return null;
        }
        return gson.fromJson(apiResult,JsonObject.class);
    }
    private String genPostBody(List<Pair<Double,Double>> wayPoints){
        StringBuilder builder = new StringBuilder();
        builder.append("{\"coordinates\":[");
        for (Pair<Double, Double> wayPoint : wayPoints) {
            builder.append("[");
            builder.append(wayPoint.getKey());
            builder.append(",");
            builder.append(wayPoint.getValue());
            builder.append("],");
        }
        if (builder.charAt(builder.length()-1) == ','){
            builder.deleteCharAt(builder.length()-1);
        }
        builder.append("]}");
        return builder.toString();
    }

    protected ExecutionInfo extractExecutionInfo(JsonObject apiResult){
        ExecutionInfo executionInfo = new ExecutionInfo();

        return executionInfo;
    }
}
