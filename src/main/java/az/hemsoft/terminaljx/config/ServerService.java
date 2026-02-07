package az.hemsoft.terminaljx.config;

import az.hemsoft.terminaljx.api.LocalApi;

public class ServerService {
    private static ServerService instance;

    private ServerService() {
    }

    public static synchronized ServerService getInstance() {
        if (instance == null) {
            instance = new ServerService();
        }
        return instance;
    }

    public void startServer() {
        LocalApi.start();
    }

    public void stopServer() {
        LocalApi.stop();
    }

    public void broadcast(String event, String data) {
        javafx.application.Platform.runLater(() -> {
            System.out.println("🔄 Local UI Sync: [" + event + "] " + data);
        });

        System.out.println("📢 Broadcast: [" + event + "] " + data);
    }
}
