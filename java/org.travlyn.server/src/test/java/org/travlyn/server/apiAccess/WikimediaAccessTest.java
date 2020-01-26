package org.travlyn.server.apiAccess;

import org.junit.Test;
import org.junit.jupiter.api.Tag;

@Tag("unit")
public class WikimediaAccessTest {

    @Test
    public void testIntroCall(){
        WikimediaRequest request = new WikimediaRequest("Düsseldorf");
        String result = request.getIntro();
        System.out.println(result);
    }
}
