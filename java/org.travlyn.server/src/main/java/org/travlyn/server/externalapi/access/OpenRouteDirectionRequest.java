package org.travlyn.server.externalapi.access;

import com.google.gson.*;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Step;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.api.Waypoint;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.IntStream;

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
        JsonObject properties;
        try{
            properties = apiResult.getAsJsonArray("features").get(0).getAsJsonObject().get("properties").getAsJsonObject();
        }catch (JsonSyntaxException ignored){
            //JSON response is malformed
            return null;
        }

        executionInfo.setDistance(properties.getAsJsonObject("summary").get("distance").getAsDouble()/1000.0);
        executionInfo.setDuration(properties.getAsJsonObject("summary").get("duration").getAsDouble()/60.0);

        ArrayList<Waypoint> waypoints = new ArrayList<>();
        for (JsonElement element: apiResult.getAsJsonArray("features").get(0).getAsJsonObject().getAsJsonObject("geometry").getAsJsonArray("coordinates")){
            JsonArray coord = element.getAsJsonArray();
            waypoints.add(new Waypoint().setLongitude(coord.get(0).getAsDouble())
                                            .setLatitude(coord.get(1).getAsDouble()));
        }
        executionInfo.setWaypoints(waypoints);

        ArrayList<Step> steps = new ArrayList<>();
        for (JsonElement step : properties.getAsJsonArray("segments").get(0).getAsJsonObject().getAsJsonArray("steps")){
            JsonObject stepObject = step.getAsJsonObject();
            Step stepInstance = new Step();
            stepInstance.setType(stepObject.get("type").getAsInt());
            stepInstance.setInstruction(stepObject.get("instruction").getAsString());
            ArrayList<Integer> indices = new ArrayList<>();
            IntStream.rangeClosed(stepObject.get("way_points").getAsJsonArray().get(0).getAsInt(),stepObject.get("way_points").getAsJsonArray().get(1).getAsInt()).forEachOrdered(indices::add);
            stepInstance.setWaypointIndices(indices);
            steps.add(stepInstance);
        }
        executionInfo.setSteps(steps);
        return executionInfo;
    }
}
