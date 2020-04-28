package org.travlyn.server.externalapi.access;

import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.api.Trip;

import java.util.ArrayList;
import java.util.List;

public class OpenRouteTripDirectionRequest extends OpenRouteDirectionRequest {

    private final double startLat;
    private final double startLon;
    private final boolean roundTrip;
    private final Trip trip;
    private final String lang;

    public OpenRouteTripDirectionRequest(double startLat, double startLon, Trip trip, boolean roundTrip, String lang) {
        this.startLat = startLat;
        this.startLon = startLon;
        this.roundTrip = roundTrip;
        this.trip = trip;
        this.lang = lang;
    }

    @Override
    public ExecutionInfo getResult() {
        List<Pair<Double, Double>> wayPoints = new ArrayList<>();
        wayPoints.add(new Pair<>(startLon, startLat));
        List<Integer> stopIds = new ArrayList<>();
        for (Stop stop : trip.getStops()) {
            wayPoints.add(new Pair<>(stop.getLongitude(), stop.getLatitude()));
            if (stop.getId() > 0) {
                stopIds.add(stop.getId());
            }
        }
        if (roundTrip) {
            wayPoints.add(new Pair<>(startLon, startLat));
        }
        JsonObject result = this.makeAPICall(wayPoints, lang);
        ExecutionInfo info = this.extractExecutionInfo(result);
        info.setTripId(trip.getId());
        info.setStopIds(stopIds);
        return info;
    }
}
