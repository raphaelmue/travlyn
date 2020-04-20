package org.travlyn.server.externalapi.access;

import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;

import java.util.ArrayList;
import java.util.List;

public class OpenRouteRedirectionRequest extends OpenRouteDirectionRequest{
    private final double startLat;
    private final double startLon;
    private final Stop stop;
    private final String lang;

    public OpenRouteRedirectionRequest(double startLat, double startLon, Stop stop, String lang) {
        this.startLat = startLat;
        this.startLon = startLon;
        this.stop = stop;
        this.lang = lang;
    }

    @Override
    public ExecutionInfo getResult() {
        List<Pair<Double,Double>> wayPoints = new ArrayList<>();
        wayPoints.add(new Pair<>(startLon,startLat));
        wayPoints.add(new Pair<>(stop.getLongitude(),stop.getLatitude()));
        JsonObject result = this.makeAPICall(wayPoints,lang);
        return this.extractExecutionInfo(result);
    }
}
