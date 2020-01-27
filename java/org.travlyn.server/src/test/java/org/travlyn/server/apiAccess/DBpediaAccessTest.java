package org.travlyn.server.apiAccess;

import org.junit.Test;
import org.junit.jupiter.api.Tag;

@Tag("unit")
public class DBpediaAccessTest {
    @Test
    public void testInfoCall() {
        DBpediaRequest request = new DBpediaRequest("Wesel",true);
        String result = request.getBasicInfo();
    }
}
