package org.travlyn.server;

import com.google.common.io.CharStreams;
import okhttp3.HttpUrl;
import okhttp3.mockwebserver.MockResponse;
import okhttp3.mockwebserver.MockWebServer;

import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.nio.charset.StandardCharsets;

public abstract class ApiTest {

    private final MockWebServer server = new MockWebServer();

    protected String startServer() throws Exception {
        server.start();
        HttpUrl url = server.url("/api");
        return url.toString();
    }

    protected void enqueue(String fileName) throws Exception {
        Reader reader = new InputStreamReader(ApiTest.class.getResourceAsStream(fileName), StandardCharsets.UTF_8);
        final String json = CharStreams.toString(reader);
        reader.close();

        server.enqueue(new MockResponse()
                .addHeader("Content-Type", "application/json; charset=utf-8")
                .setResponseCode(200)
                .setBody(json));
    }

    protected void tearDown() throws IOException {
        server.shutdown();
    }

}
