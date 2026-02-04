package az.hemsoft.terminaljx.business.restaurant.service;

import io.javalin.Javalin;
import io.javalin.http.sse.SseClient;
import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;

public class ServerService {
    private static ServerService instance;
    private Javalin app;
    private final Queue<SseClient> sseClients = new ConcurrentLinkedQueue<>();
    private Runnable showUICallback;

    private ServerService() {
    }

    public static synchronized ServerService getInstance() {
        if (instance == null) {
            instance = new ServerService();
        }
        return instance;
    }

    public void setShowUICallback(Runnable callback) {
        this.showUICallback = callback;
    }

    public void startServer() {
        ConfigService configService = ConfigService.getInstance();
        if (!configService.isServerEnabled()) {
            System.out.println("ℹ️ Backend Server is disabled.");
            return;
        }

        int port = configService.getServerPort();

        new Thread(() -> {
            try {
                app = Javalin.create(javalinConfig -> {
                    javalinConfig.showJavalinBanner = false;
                    javalinConfig.jetty.modifyServer(server -> {
                        // Optional: Jetty tweaks
                    });
                }).start(port);

                // Define SSE endpoint
                app.sse("/api/sse", client -> {
                    client.keepAlive();
                    sseClients.add(client);
                    System.out.println("🔌 New Client Connected: " + client.ctx().ip());

                    client.onClose(() -> {
                        sseClients.remove(client);
                        System.out.println("❌ Client Disconnected: " + client.ctx().ip());
                    });
                });

                // Example API endpoint
                app.get("/api/status", ctx -> ctx.result("Server is Running"));

                // Single Instance control endpoint
                app.post("/api/control/show-ui", ctx -> {
                    if (showUICallback != null) {
                        javafx.application.Platform.runLater(showUICallback);
                        ctx.result("OK");
                    } else {
                        ctx.status(500).result("No UI Callback registered");
                    }
                });

                System.out.println("🚀 Backend Server Started on Port " + port);

            } catch (Exception e) {
                System.err.println("❌ Failed to start server: " + e.getMessage());
                e.printStackTrace(); // Port likely in use
            }
        }).start();
    }

    public void stopServer() {
        if (app != null) {
            app.stop();
            System.out.println("🛑 Server Stopped");
        }
    }

    public void broadcast(String event, String data) {
        // 1. Send to all connected clients (Other POS devices)
        sseClients.forEach(client -> client.sendEvent(event, data));

        // 2. Local UI update (since server doesn't connect to itself as a client)
        javafx.application.Platform.runLater(() -> {
            System.out.println("🔄 Local UI Sync: [" + event + "] " + data);
            // This is where we will trigger UI refresh for the Server POS itself
        });

        System.out.println("📢 Broadcast: [" + event + "] " + data);
    }
}
