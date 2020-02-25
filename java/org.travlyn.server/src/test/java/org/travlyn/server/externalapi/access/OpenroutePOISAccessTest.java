package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.Stop;

import java.util.ArrayList;

@Tag("unit")
public class OpenroutePOISAccessTest {

    @Test
    public void testPOISCall(){
        OpenrouteRequest request = new OpenrouteRequest();
        ArrayList<Stop> result = request.getPOIS(0.1,2.0);
        System.out.println(result.toString());
    }
}
