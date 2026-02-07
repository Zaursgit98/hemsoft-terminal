package az.hemsoft.terminaljx.api;

import az.hemsoft.terminaljx.config.DatabaseManager;
import az.hemsoft.terminaljx.business.warehouse.controller.ProductController;
import az.hemsoft.terminaljx.business.warehouse.controller.WarehouseController;
import io.javalin.Javalin;

public class LocalApi {

    private static Javalin app;

    public static void start() {
        if (app != null)
            return;

        DatabaseManager db = DatabaseManager.getInstance();
        int port = Integer.parseInt(db.getSetting("server_port", "8080"));

        app = Javalin.create(config -> {
            config.showJavalinBanner = false;
        }).start(port);

        System.out.println("🚀 Local API started on port: " + port);

        // Register Annotation-Driven Controllers
        RouteManager.registerControllers(app,
                new WarehouseController(),
                new ProductController());

        // Simple endpoints
        app.get("/", ctx -> ctx.result("HEM Soft Terminal API is Running..."));
        app.get("/health", ctx -> ctx.result("OK"));
    }

    public static void stop() {
        if (app != null) {
            app.stop();
            app = null;
        }
    }
}
