package org.travlyn.server.externalapi.access;

import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.Stop;
import java.util.Set;

@Tag("unit")
public class OpenroutePOISAccessTest {

    @Test
    public void testPOISCall(){
        OpenrouteRequest request = new OpenrouteRequest();
        Set<Stop> result = request.getPOIS(8.404435,49.013513);
        System.out.println(result.toString());
    }
}