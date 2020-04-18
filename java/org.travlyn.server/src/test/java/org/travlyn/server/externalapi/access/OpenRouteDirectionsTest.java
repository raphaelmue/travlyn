package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.db.StopEntity;
import org.travlyn.shared.model.db.TripEntity;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

@Tag("unit")
public class OpenRouteDirectionsTest {

    @Test
    public void testTripRouteCall(){
        Stop stop = new Stop().setLatitude(51.517021).setLongitude(-0.117408);
        Set<StopEntity> set = new HashSet<>();
        set.add(stop.toEntity());
        //TripEntity entity = new TripEntity().setStops(set);
        OpenRouteTripDirectionRequest tripRequest = new OpenRouteTripDirectionRequest(51.507,-0.1275, null,false);
        ExecutionInfo info = tripRequest.getResult();
        System.out.println("Hallo!");
    }
}
