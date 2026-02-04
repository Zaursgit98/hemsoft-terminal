package az.hemsoft.terminaljx;

import az.hemsoft.terminaljx.ui.controller.PosSignInController;
import az.hemsoft.terminaljx.business.restaurant.service.ConfigService;
import az.hemsoft.terminaljx.business.restaurant.service.ServerService;
import az.hemsoft.terminaljx.business.restaurant.service.ClientService;
import az.hemsoft.terminaljx.business.restaurant.service.TrayService;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import org.kordamp.bootstrapfx.BootstrapFX;

public class MainApplication extends Application {

    private static boolean startHidden = false;
    private Stage mainStage;

    @Override
    public void init() {
        Parameters params = getParameters();
        if (params != null && params.getRaw().contains("--hidden")) {
            startHidden = true;
        }
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.mainStage = primaryStage;

        // Register UI callback in ServerService so external triggers can show UI
        ServerService.getInstance().setShowUICallback(this::showUI);

        // Setup tray immediately
        TrayService.getInstance().setupTray("HEMsoft Terminal", () -> {
            Platform.runLater(this::showUI);
        });

        if (startHidden) {
            System.out.println("\uD83E\uDD22 Starting hidden in System Tray...");
            Platform.setImplicitExit(false);
        } else {
            showUI();
        }
    }

    private void showUI() {
        if (mainStage == null)
            return;

        if (mainStage.isShowing()) {
            mainStage.show();
            mainStage.toFront();
            return;
        }

        try {
            System.out.println("MainApplication loading UI...");
            PosSignInController posSignInController = new PosSignInController();
            BorderPane root = posSignInController.getView();

            // Scene creation with adaptive sizing
            javafx.geometry.Rectangle2D screenBounds = javafx.stage.Screen.getPrimary().getVisualBounds();
            Scene scene = new Scene(root, screenBounds.getWidth(), screenBounds.getHeight());
            scene.getStylesheets().add(BootstrapFX.bootstrapFXStylesheet());

            // Custom CSS
            try {
                java.net.URL cssUrl = getClass().getResource("/styles.css");
                if (cssUrl != null) {
                    scene.getStylesheets().add(cssUrl.toExternalForm());
                }
            } catch (Exception ignored) {
            }

            mainStage.setTitle("HEMsoft Terminal");
            mainStage.setScene(scene);

            // App icon
            try {
                java.net.URL iconUrl = getClass().getResource("/assets/hemsoft-logo.png");
                if (iconUrl != null) {
                    mainStage.getIcons().add(new Image(iconUrl.toExternalForm()));
                }
            } catch (Exception ignored) {
            }

            mainStage.initStyle(StageStyle.UNDECORATED);
            mainStage.setFullScreen(true);
            mainStage.setFullScreenExitHint("");

            // Detection of Alt+F4 is now handled by the default close request (minimizes to
            // tray)

            // Minimize/Hide to tray instead of exit when closing
            mainStage.setOnCloseRequest(event -> {
                event.consume();
                mainStage.hide();
                TrayService.getInstance().showMessage(
                        "HEMsoft POS",
                        "Proqram arxa fonda i\u015Fl\u0259m\u0259y\u0259 davam edir (System Tray).",
                        java.awt.TrayIcon.MessageType.NONE);
            });

            mainStage.show();
            root.requestFocus();

        } catch (Exception e) {
            System.err.println("❌ UI Error: " + e.getMessage());
            e.printStackTrace();
            // Show simple error if UI fails
            Label errorLabel = new Label("X\u0259ta: " + e.getMessage());
            mainStage.setScene(new Scene(errorLabel, 400, 200));
            mainStage.setTitle("X\u0259ta");
        }
    }

    public static void main(String[] args) {
        // Set app name for certain platforms and AWT
        System.setProperty("apple.awt.application.name", "HEMsoft POS");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HEMsoft POS");

        ConfigService configService = ConfigService.getInstance();

        // WhatsApp-like: Check if app already running
        if (checkInstanceAlive(configService.getServerPort())) {
            System.out.println("\u26A0\uFE0F App already running. Signaling to show UI...");
            System.exit(0);
        }

        if (configService.isServerMode()) {
            ServerService.getInstance().startServer();
        } else {
            ClientService.getInstance().startClient("localhost", configService.getServerPort(), (event, data) -> {
                System.out.println("\uD83D\uDCE9 Client event: " + event);
            });
        }

        launch(args);
    }

    private static boolean checkInstanceAlive(int port) {
        try {
            java.net.URL url = new java.net.URL("http://localhost:" + port + "/api/control/show-ui");
            java.net.HttpURLConnection conn = (java.net.HttpURLConnection) url.openConnection();
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(1000);
            int code = conn.getResponseCode();
            return code == 200;
        } catch (Exception e) {
            return false;
        }
    }
}
