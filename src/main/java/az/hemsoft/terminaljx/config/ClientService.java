package az.hemsoft.terminaljx.config;

import java.util.function.BiConsumer;

public class ClientService {
    private static ClientService instance;

    private ClientService() {
    }

    public static synchronized ClientService getInstance() {
        if (instance == null) {
            instance = new ClientService();
        }
        return instance;
    }

    public void startClient(String host, int port, BiConsumer<String, String> onEvent) {
        System.out.println("🌐 Client started, connecting to " + host + ":" + port);
        // Basic stub for now
    }
}
