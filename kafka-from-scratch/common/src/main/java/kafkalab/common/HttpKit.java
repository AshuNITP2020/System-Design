package kafkalab.common;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.concurrent.Executors;

/**
 * JDK-only HTTP helpers so no stage needs a web framework. Nothing here is Kafka-related;
 * it exists purely so stages 0 and 1 are 80 lines instead of 300.
 */
public final class HttpKit {

    private static final HttpClient CLIENT = HttpClient.newBuilder()
            .connectTimeout(Duration.ofSeconds(2))
            .build();

    private HttpKit() {}

    /** Start a server on {@code port}; routes are added by the caller. */
    public static HttpServer server(int port) throws IOException {
        HttpServer s = HttpServer.create(new InetSocketAddress(port), 0);
        s.setExecutor(Executors.newVirtualThreadPerTaskExecutor());
        return s;
    }

    public static void route(HttpServer s, String path, HttpHandler h) {
        s.createContext(path, h);
    }

    public static String body(HttpExchange ex) throws IOException {
        return new String(ex.getRequestBody().readAllBytes(), StandardCharsets.UTF_8);
    }

    public static void respond(HttpExchange ex, int status, String body) throws IOException {
        byte[] b = body.getBytes(StandardCharsets.UTF_8);
        ex.getResponseHeaders().add("Content-Type", "application/json");
        ex.sendResponseHeaders(status, b.length);
        try (var os = ex.getResponseBody()) {
            os.write(b);
        }
    }

    /** POST JSON. Throws on any non-2xx or connection failure — stage 1 needs to feel that. */
    public static void postJson(String url, String json) throws Exception {
        HttpRequest req = HttpRequest.newBuilder(URI.create(url))
                .timeout(Duration.ofSeconds(5))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();
        HttpResponse<String> res = CLIENT.send(req, HttpResponse.BodyHandlers.ofString());
        if (res.statusCode() / 100 != 2) {
            throw new RuntimeException("POST " + url + " -> " + res.statusCode() + " " + res.body());
        }
    }
}
