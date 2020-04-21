package org.travlyn.server.externalapi.access;

import org.junit.After;
import org.junit.Before;
import org.junit.jupiter.api.*;
import org.travlyn.shared.model.api.Stop;

import java.io.IOException;

@Tag("unit")
public class DBpediaStopRequestTest extends ApiTest{

    @Before
    @BeforeEach
    public void setUp() throws Exception {
        DBpediaStopRequest.setBaseUrl(setUp("dbpedia-stop-response-admirality-arch.json"));
    }

    @Test
    public void getResult() throws QuotaLimitException{
        DBpediaStopRequest dbPediaStopRequest = new DBpediaStopRequest("Admiralty Arch");
        Stop stop = dbPediaStopRequest.getResult();
        Assertions.assertEquals("Admiralty Arch is a landmark building in London which incorporates an archway " +
                "providing road and pedestrian access between The Mall, which extends to the southwest, and Trafalgar " +
                "Square to the northeast. Admiralty Arch, commissioned by King Edward VII in memory of his mother, Queen " +
                "Victoria and designed by Aston Webb is now a Grade I listed building. In the past, it served as residence " +
                "of the First Sea Lord and was used by the Admiralty. Until 2011, the building housed government offices, " +
                "but in 2012 the government sold a 125-year lease over the building to a property developer (Prime Investors " +
                "Capital, run by Rafael Serrano) for redevelopment into a luxury hotel, restaurant and apartments.", stop.getDescription());
        Assertions.assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Arco_del_Almirantazgo,_Lond" +
                "res,_Inglaterra,_2014-08-11,_DD_186.JPG?width=300", stop.getImage());
    }

    @After
    @AfterEach
    public void tearDown() throws IOException {
        super.tearDown();
    }
}