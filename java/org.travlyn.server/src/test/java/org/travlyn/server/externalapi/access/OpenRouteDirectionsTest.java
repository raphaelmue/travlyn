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
        Stop stop = new Stop().setLatitude(51.517021).setLongitude(-0.117408);
        Set<TripStopEntity> set = new HashSet<>();
        set.add(new TripStopEntity().setStop(stop.toEntity()));
        User user = new User().email("mail@mail.com").name("name").id(1);
        TripEntity entity = new TripEntity().setStops(set).setUser(user.toEntity());
        OpenRouteTripDirectionRequest tripRequest = new OpenRouteTripDirectionRequest(51.507,-0.1275, entity.toDataTransferObject(),false,"en");
        ExecutionInfo info = tripRequest.getResult();
        Assertions.assertNotNull(info);
        Assertions.assertEquals(85,info.getWaypoints().size());
        Assertions.assertEquals(1.5843,info.getDistance());
    }

    @Test
    public void testRedirectionCall(){
        Stop stop = new Stop().setLatitude(49.007865).setLongitude(8.398634);
        OpenRouteRedirectionRequest redirectionRequest = new OpenRouteRedirectionRequest(49.0092097,8.4039514,stop,"en");
        ExecutionInfo info = redirectionRequest.getResult();
        Assertions.assertNotNull(info);
        Assertions.assertEquals(0.555,info.getDistance());
        Assertions.assertEquals(35,info.getWaypoints().size());
    }
}
