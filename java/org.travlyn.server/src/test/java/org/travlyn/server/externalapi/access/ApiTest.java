package org.travlyn.server.externalapi.access;

import com.google.common.io.CharStreams;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;

public abstract class ApiTest {

    private final MockWebServer server = new MockWebServer();

    protected String setUp(String fileName) throws Exception {
        Reader reader = new InputStreamReader(ApiTest.class.getResourceAsStream(fileName));
        final String json = CharStreams.toString(reader);
        reader.close();

        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(200)
                .setBody(json));
        server.start();
        HttpUrl url = server.url("/api");
        return url.toString();
    }

    protected void tearDown() throws IOException {
        server.shutdown();
    }

}
