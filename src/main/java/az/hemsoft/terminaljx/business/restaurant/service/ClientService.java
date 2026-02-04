package az.hemsoft.terminaljx.business.restaurant.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.concurrent.CompletableFuture;
import java.util.function.BiConsumer;

public class ClientService {
    private static ClientService instance;
    private HttpClient client;
    private boolean connected = false;

    private ClientService() {
        this.client = HttpClient.newBuilder()
                .build();
    }

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public void startClient(String serverIp, int port, BiConsumer<String, String> onEvent) {
        if (ConfigService.getInstance().isServerMode()) {
            System.out.println("ℹ️ App is in SERVER mode, skipping client connection.");
            return;
        }

        String url = "http://" + serverIp + ":" + port + "/api/sse";
        System.out.println("🔌 Connecting to Server SSE: " + url);

        // Simple SSE listener implementation using HttpClient
        CompletableFuture.runAsync(() -> {
            try {
                HttpRequest request = HttpRequest.newBuilder()
                        .uri(URI.create(url))
                        .GET()
                        .build();

                client.sendAsync(request, HttpResponse.BodyHandlers.ofLines())
                        .thenAccept(response -> {
                            if (response.statusCode() == 200) {
                                connected = true;
                                System.out.println("✅ Connected to Server SSE");
                                response.body().forEach(line -> {
                                    if (line.startsWith("event: ")) {
                                        String event = line.substring(7);
                                        // Next line should be data
                                    } else if (line.startsWith("data: ")) {
                                        String data = line.substring(6);
                                        // For now, just trigger an update
                                        onEvent.accept("update", data);
                                    }
                                });
                            }
                        }).join();
            } catch (Exception e) {
                connected = false;
                System.err.println("❌ SSE Connection failed: " + e.getMessage());
            }
        });
    }

    public boolean isConnected() {
        return connected;
    }
}
