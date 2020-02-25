package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Tag;

@Tag("unit")
public class OpenroutePOISAccessTest {

    @Test
    public void testPOISCall(){
        OpenrouteRequest request = new OpenrouteRequest();
        String result = request.getPOIS(0.1,2.0);
        System.out.println(result);
    }
}
