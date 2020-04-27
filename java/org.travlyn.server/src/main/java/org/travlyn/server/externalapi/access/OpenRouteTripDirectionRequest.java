package org.travlyn.server.externalapi.access;

import com.google.gson.JsonObject;
import org.travlyn.server.util.Pair;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.api.Trip;

import java.util.ArrayList;
import java.util.List;

public class OpenRouteTripDirectionRequest extends OpenRouteDirectionRequest {

    private final boolean roundTrip;
    private final Trip trip;
    private final String lang;

    public OpenRouteTripDirectionRequest(Trip trip, boolean roundTrip, String lang) {
        this.roundTrip = roundTrip;
        this.trip = trip;
        this.lang = lang;
    }

    @Override
    public ExecutionInfo getResult() {
        List<Pair<Double, Double>> wayPoints = new ArrayList<>();
        for (Stop stop : trip.getStops()) {
            wayPoints.add(new Pair<>(stop.getLongitude(), stop.getLatitude()));
        }
        if (roundTrip) {
            wayPoints.add(new Pair<>(trip.getStops().get(0).getLongitude(), trip.getStops().get(0).getLongitude()));
        }
        JsonObject result = this.makeAPICall(wayPoints, lang);
        ExecutionInfo info = this.extractExecutionInfo(result);
        info.setTripId(trip.getId());
        return info;
    }
}
