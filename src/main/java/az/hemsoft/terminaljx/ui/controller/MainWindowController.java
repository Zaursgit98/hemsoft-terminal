package az.hemsoft.terminaljx.ui.controller;

import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Duration;

import java.io.IOException;

public class MainWindowController {

    @FXML
    private StackPane rootStack;
    @FXML
    private BorderPane mainLayout;
    @FXML
    private StackPane overlayDimmer;
    @FXML
    private VBox drawerPane;

    // Injected Controller from <fx:include fx:id="topBar" />
    @FXML
    private TopBarController topBarController;

    private boolean isDrawerOpen = false;

    @FXML
    private StackPane contentArea;

    @FXML
    public void initialize() {
        // Load TableMapView
        try {
            FXMLLoader loader = new FXMLLoader(
                    getClass().getResource("/az/hemsoft/terminaljx/views/TableMapView.fxml"));
            Parent tableView = loader.load();
            contentArea.getChildren().setAll(tableView);
        } catch (IOException e) {
            e.printStackTrace();
            contentArea.getChildren().add(new javafx.scene.control.Label("Error loading Table View"));
        }

        // Setup Drawer Animation defaults
        drawerPane.setTranslateX(320); // Hidden by default
        overlayDimmer.setVisible(false);
        overlayDimmer.setOpacity(0.0);

        // Connect TopBar menu button to Drawer
        if (topBarController != null) {
            topBarController.setOnMenuToggle(this::toggleDrawer);
        }
    }

    public void initData(String username) {
        if (topBarController != null && username != null) {
            topBarController.setUserName(username);
            // topBarController.setUserRole("Admin");
        }
    }

    private void toggleDrawer() {
        if (isDrawerOpen) {
            closeDrawer();
        } else {
            openDrawer();
        }
    }

    private void openDrawer() {
        isDrawerOpen = true;

        overlayDimmer.setVisible(true);
        TranslateTransition openNav = new TranslateTransition(new Duration(300), drawerPane);
        openNav.setToX(0);
        openNav.play();

        overlayDimmer.setOpacity(0.0);
        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(Duration.millis(300), overlayDimmer);
        fade.setFromValue(0.0);
        fade.setToValue(1.0);
        fade.play();
    }

    private void closeDrawer() {
        isDrawerOpen = false;

        TranslateTransition closeNav = new TranslateTransition(new Duration(300), drawerPane);
        closeNav.setToX(320);
        closeNav.setOnFinished(e -> overlayDimmer.setVisible(false));
        closeNav.play();

        javafx.animation.FadeTransition fade = new javafx.animation.FadeTransition(Duration.millis(300), overlayDimmer);
        fade.setFromValue(1.0);
        fade.setToValue(0.0);
        fade.play();
    }

    @FXML
    private void handleOverlayClick(MouseEvent event) {
        if (isDrawerOpen) {
            closeDrawer();
        }
    }

    @FXML
    private void handleDrawerClose() {
        closeDrawer();
    }

    @FXML
    private void handleLogout() {
        try {
            // Load SignIn screen
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/az/hemsoft/terminaljx/hello-view.fxml")); // Or
                                                                                                                  // constructing
                                                                                                                  // PosSignInController
                                                                                                                  // manually
            // Since PosSignInController is manually constructed in MainApplication, we
            // should try to mimic that or create a standard way.
            // For now, let's close visual clutter and return to the start.

            // Re-launch MainApplication approach or easier: just create new
            // PosSignInController
            PosSignInController controller = new PosSignInController();
            BorderPane view = controller.getView();

            Stage stage = (Stage) rootStack.getScene().getWindow();
            Scene scene = new Scene(view, stage.getScene().getWidth(), stage.getScene().getHeight());

            // Re-apply styles if needed (MainApplication handles this usually)
            // But scene is new.
            try {
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                // BootstrapFX is needed
                scene.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
            } catch (Exception e) {
                e.printStackTrace();
            }

            stage.setScene(scene);
            stage.setFullScreen(true);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
