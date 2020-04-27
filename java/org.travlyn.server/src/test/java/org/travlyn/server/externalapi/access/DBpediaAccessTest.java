package org.travlyn.server.externalapi.access;

import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.travlyn.server.ApiTest;
import org.travlyn.shared.model.api.City;

import java.io.IOException;

@Tag("unit")
public class DBpediaAccessTest extends ApiTest {

    @Before
    @BeforeEach
    public void setUp() throws Exception {
        enqueue("dbpedia-response-karlsruhe.json");
        DBpediaCityRequest.setBaseUrl(startServer());
    }

    @Test
    public void testInfoCall() {
        DBpediaCityRequest request = new DBpediaCityRequest("Karlsruhe");
        City result = request.getResult();
        Assert.assertEquals("Karlsruhe (German pronunciation: [ˈkaɐ̯lsˌʁuːə] ; formerly Carlsruhe) is the second-largest city in the state of Baden-Württemberg, in southwest Germany, near the Franco-German border. It has a population of 296,033. The city is the seat of the two highest courts in Germany: the Federal Constitutional Court and the Federal Court of Justice. Its most remarkable building is Karlsruhe Palace, which was built in 1715.", result.getDescription());
        Assert.assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Karlsruhe_town_centre_air.jpg?width=300", result.getImage());
        Assert.assertEquals(8.4039514, result.getLongitude(), 0.0000001);
        Assert.assertEquals(49.0092097, result.getLatitude(), 0.0000001);
    }

    @After
    @AfterEach
    public void tearDown() throws IOException {
        stopServer();
    }

}
