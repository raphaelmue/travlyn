package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.db.StopEntity;

import java.util.Set;

@Tag("unit")
public class OpenRouteStopAccessTest {

    @Test
    public void testStopCall() {
        City city = new City()
                .latitude(51.507222222222225)
                .longitude(-0.1275);
        OpenRouteRequest request = new OpenRouteRequest(city.getLatitude(), city.getLongitude(), city.toEntity());
        Set<StopEntity> result = request.getResult();
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    public void testDBPediaStop() {
        DBpediaStopRequest request = new DBpediaStopRequest("Karlsruhe_Palace");
        Stop stop = request.getResult();
        Assertions.assertNotNull(stop.getName());
        Assertions.assertNotNull(stop.getDescription());
        Assertions.assertNotNull(stop.getImage());
    }
}
