package org.travlyn.server.externalapi.access;

import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;

import java.util.ArrayList;
import java.util.List;

public class OpenRouteTripDirectionRequest extends OpenRouteDirectionRequest {

    private final double startLat;
    private final double startLon;
    private final boolean roundTrip;
    private final List<Stop> tripStops;

    public OpenRouteTripDirectionRequest(double startLat, double startLon, List<Stop> stops, boolean roundTrip) {
        this.startLat = startLat;
        this.startLon = startLon;
        this.roundTrip = roundTrip;
        this.tripStops = stops;
    }

    @Override
    public ExecutionInfo getResult() {
        List<Pair<Double,Double>> wayPoints = new ArrayList<>();
        wayPoints.add(new Pair<>(startLon,startLat));
        for (Stop stop : tripStops) {
            wayPoints.add(new Pair<>(stop.getLongitude(),stop.getLatitude()));
        }
        if(roundTrip){
            wayPoints.add(new Pair<>(startLon,startLat));
        }
        JsonObject result = this.makeAPICall(wayPoints);
        return this.extractExecutionInfo(result);
    }
}
