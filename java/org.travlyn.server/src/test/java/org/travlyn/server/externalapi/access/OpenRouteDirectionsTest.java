package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.ExecutionInfo;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.api.User;
import org.travlyn.shared.model.db.TripEntity;
import org.travlyn.shared.model.db.TripStopEntity;

import java.util.HashSet;
import java.util.Set;

@Tag("unit")
public class OpenRouteDirectionsTest {

    @Test
    public void testTripRouteCall(){
        Stop stop = new Stop().setLatitude(-0.117408).setLongitude(51.517021);
        Set<TripStopEntity> set = new HashSet<>();
        set.add(new TripStopEntity().setStop(stop.toEntity()));
        User user = new User().email("mail@mail.com").name("name").id(1);
        TripEntity entity = new TripEntity().setStops(set).setUser(user.toEntity());
        OpenRouteTripDirectionRequest tripRequest = new OpenRouteTripDirectionRequest(51.507,-0.1275, entity.toDataTransferObject(),false);
        ExecutionInfo info = tripRequest.getResult();
        Assertions.assertEquals(85,info.getWaypoints().size());
        Assertions.assertEquals(1.5843,info.getDistance());
    }
}
