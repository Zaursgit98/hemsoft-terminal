package az.hemsoft.terminaljx;

import az.hemsoft.terminaljx.ui.controller.PosSignInController;
import az.hemsoft.terminaljx.config.ConfigService;
import az.hemsoft.terminaljx.config.ServerService;
import az.hemsoft.terminaljx.config.ClientService;
import az.hemsoft.terminaljx.config.TrayService;
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

        // Setup tray immediately
        TrayService.getInstance().setupTray("HEMsoft Terminal", () -> {
            Platform.runLater(this::showUI);
        });

        if (startHidden) {
            System.out.println("🧟 Starting hidden in System Tray...");
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

            // Minimize/Hide to tray instead of exit when closing
            mainStage.setOnCloseRequest(event -> {
                event.consume();
                mainStage.hide();
                TrayService.getInstance().showMessage(
                        "HEMsoft POS",
                        "Proqram arxa fonda işləməyə davam edir (System Tray).",
                        java.awt.TrayIcon.MessageType.NONE);
            });

            mainStage.show();
            root.requestFocus();

        } catch (Exception e) {
            System.err.println("❌ UI Error: " + e.getMessage());
            e.printStackTrace();
            // Show simple error if UI fails
            Label errorLabel = new Label("Xəta: " + e.getMessage());
            mainStage.setScene(new Scene(errorLabel, 400, 200));
            mainStage.setTitle("Xəta");
        }
    }

    public static void main(String[] args) {
        // Set app name for certain platforms and AWT
        System.setProperty("apple.awt.application.name", "HEMsoft POS");
        System.setProperty("com.apple.mrj.application.apple.menu.about.name", "HEMsoft POS");

        ConfigService configService = ConfigService.getInstance();

        if (configService.isServerMode()) {
            ServerService.getInstance().startServer();
        } else {
            ClientService.getInstance().startClient("localhost", configService.getServerPort(), (event, data) -> {
                System.out.println("📩 Client event: " + event);
            });
        }

        launch(args);
    }
}
