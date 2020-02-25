package org.travlyn.server.externalapi.access;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Tag;
import org.travlyn.shared.model.api.City;

@Tag("unit")
public class DBpediaAccessTest {
    @Test
    public void testInfoCall() {
        DBpediaCityRequest request = new DBpediaCityRequest("Wesel");
        City result = request.getResult();
        Assert.assertEquals("Wesel (German pronunciation: [ˈveːzəl]) is a city in North Rhine-Westphalia, Germany. It is the capital of the Wesel district.", result.getDescription());
        Assert.assertEquals("http://commons.wikimedia.org/wiki/Special:FilePath/Wesel_willibrordi_dom_chor.jpg?width=300", result.getImage());
    }
}
