package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.travlyn.server.ApiTest;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.db.StopEntity;

import java.util.HashMap;
import java.util.Set;

@Tag("unit")
public class OpenRouteStopAccessTest extends ApiTest {

    @Test
    public void testStopCall() {
        City city = new City()
                .latitude(51.507222222222225)
                .longitude(-0.1275);
        OpenRoutePOIRequest request = new OpenRoutePOIRequest(city.getLatitude(), city.getLongitude(), city.toEntity(), new HashMap<>());
        Set<StopEntity> result = request.getResult();
        Assertions.assertTrue(result.size() > 0);
    }

    @Test
    public void testDBPediaStop() throws Exception {
        enqueue("dbpedia-response-karlsruhe_palace.json");
        DBpediaStopRequest.setBaseUrl(startServer());

        DBpediaStopRequest request = new DBpediaStopRequest("Karlsruhe_Palace");
        Stop stop = request.getResult();
        Assertions.assertNotNull(stop.getName());
        Assertions.assertNotNull(stop.getDescription());
        Assertions.assertNotNull(stop.getImage());
    }
}
