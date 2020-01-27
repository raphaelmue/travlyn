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
        Assert.assertEquals("\"Wesel (German pronunciation: [ˈveːzl̩]) is a city in North Rhine-Westphalia, Germany. It is the capital of the Wesel district.\"",result);
    }

    @Test
    public void testImageCall(){
        WikimediaRequest request = new WikimediaRequest("Wesel");
        String result = request.getImage();
        Assert.assertEquals("https://upload.wikimedia.org/wikipedia/commons/9/93/Braun_Wesel_UBHD.jpg",result);
    }
}
