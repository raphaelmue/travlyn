package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;

import java.util.ArrayList;

@Tag("unit")
public class OpenRouteDirectionsTest {

    @Test
    public void testTripRouteCall(){
        ArrayList<Stop> stops = new ArrayList<Stop>();
        stops.add(new Stop().setLatitude(51.517021).setLongitude(-0.117408));
        OpenRouteTripDirectionRequest tripRequest = new OpenRouteTripDirectionRequest(51.507,-0.1275, stops,false);
        ExecutionInfo info = tripRequest.getResult();
        System.out.println("Hallo!");
    }
}
