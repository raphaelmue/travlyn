package org.travlyn.server.externalapi.access;

import org.junit.After;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.travlyn.server.ApiTest;
import org.travlyn.shared.model.api.City;
import org.travlyn.shared.model.api.Stop;
import org.travlyn.shared.model.db.StopEntity;

import java.io.IOException;
import java.util.HashMap;
import java.util.Set;

@Tag("unit")
public class OpenRouteStopAccessTest extends ApiTest {

    @Test
    public void testStopCall() throws Exception {
        enqueue("openroute-response-karlsruhe.json");
        for (int i = 0; i < 8; i++) enqueue("openroute-response-karlsruhe-empty.json");

        final String url = startServer();
        OpenRoutePOIRequest.setBaseUrl(url);

        City city = new City()
                .latitude(49.0092097)
                .longitude(8.4039514);
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

    @After
    @AfterEach
    public void tearDown() throws IOException {
        stopServer();
    }
}
