package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.Stop;

@Tag("unit")
public class OpenRouteStopAccessTest {

    @Test
    public void testStopCall(){
        //OpenrouteRequest request = new OpenrouteRequest();
        //City city = new City();
        //Set<Stop> result = request.getPOIS(8.404435,49.013513,city);
        //System.out.println(result.toString());
    }
    @Test
    public void testDBPediaStop(){
        DBPediaStopRequest request = new DBPediaStopRequest("Karlsruhe_Palace");
        Stop stop = request.getResult();
        System.out.println(stop.toString());
    }
}
