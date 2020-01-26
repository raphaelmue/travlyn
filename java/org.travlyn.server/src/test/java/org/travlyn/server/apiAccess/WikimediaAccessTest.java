package org.travlyn.server.apiAccess;

import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.Tag;

@Tag("unit")
public class WikimediaAccessTest {

    @Test
    public void testIntroCall(){
        WikimediaRequest request = new WikimediaRequest("Wesel");
        String result = request.getIntro();
        Assert.assertEquals("\"Wesel (German pronunciation: [ˈveːzl̩]) is a city in North Rhine-Westphalia, Germany. It is the capital of the Wesel district.\\n\"",result);
    }
}
