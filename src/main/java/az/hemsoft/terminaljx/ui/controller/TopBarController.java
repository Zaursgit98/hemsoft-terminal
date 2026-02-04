package az.hemsoft.terminaljx.ui.controller;

import com.jfoenix.controls.JFXButton;
import javafx.fxml.FXML;
import javafx.scene.control.ContentDisplay;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Region;

public class TopBarController {

    @FXML
    private HBox areaContainer;
    @FXML
    private JFXButton newOrderButton;
    @FXML
    private JFXButton checksButton;
    @FXML
    private JFXButton userBackButton;

    // Optional: remove if not needed, or make sure it's not bound in FXML
    // private JFXButton menuButton; // Removed from FXML

    private Runnable onMenuToggleAction;

    @FXML
    public void initialize() {
        // Init logic
        addMockAreas();
    }

    private void addMockAreas() {
        String[] areas = { "Zal", "Teras", "VIP", "Bar" };

        for (int i = 0; i < areas.length; i++) {
            String areaName = areas[i];
            JFXButton areaBtn = new JFXButton(areaName);
            areaBtn.getStyleClass().add("area-button");

            // Check Icon using Region (SVG Shape in CSS)
            Region checkIcon = new Region();
            checkIcon.getStyleClass().add("area-check-icon");
            checkIcon.setManaged(false);
            checkIcon.setVisible(false);

            areaBtn.setGraphic(checkIcon);
            areaBtn.setContentDisplay(ContentDisplay.RIGHT);
            areaBtn.setGraphicTextGap(6);
            areaBtn.setMnemonicParsing(false);

            // Allow ripple color customization if needed via CSS, default is usually fine
            // areaBtn.setRippleColor(Color.valueOf("#..."));

            // Set first one as active by default
            if (i == 0) {
                areaBtn.getStyleClass().add("active");
                checkIcon.setVisible(true);
                checkIcon.setManaged(true);
            }

            areaBtn.setOnAction(e -> handleAreaClick(areaBtn));
            areaContainer.getChildren().add(areaBtn);
        }
    }

    private void handleAreaClick(JFXButton clickedBtn) {
        // Remove active class from all and hide icons
        areaContainer.getChildren().forEach(node -> {
            if (node instanceof JFXButton) {
                JFXButton btn = (JFXButton) node;
                btn.getStyleClass().remove("active");
                if (btn.getGraphic() != null) {
                    btn.getGraphic().setVisible(false);
                    btn.getGraphic().setManaged(false);
                }
            }
        });

        // Add to clicked and show icon
        clickedBtn.getStyleClass().add("active");
        if (clickedBtn.getGraphic() != null) {
            clickedBtn.getGraphic().setVisible(true);
            clickedBtn.getGraphic().setManaged(true);
        }

        System.out.println("Switched to area: " + clickedBtn.getText());
        // TODO: Notify Map Controller to switch zones in future
    }

    @FXML
    private void handleNewOrder() {
        System.out.println("New Order Clicked");
        // Logic to clear selection and allow new order
    }

    @FXML
    private void handleChecks() {
        System.out.println("Checks Clicked");
        // Logic to show checks/receipts
    }

    @FXML
    private void handleBack() {
        System.out.println("Back/Exit Clicked");
        // Logic to exit or go back to login
    }

    // Kept for compatibility but might need update if button text logic changes
    public void setUserName(String name) {
        if (userBackButton != null) {
            userBackButton.setText(name);
        }
    }

    // Kept empty to avoid errors if called
    public void setUserRole(String role) {
        // userRoleLabel.setText(role);
    }

    // Kept but likely unused if Menu button is gone
    @FXML
    private void handleMenuToggle() {
        if (onMenuToggleAction != null) {
            onMenuToggleAction.run();
        }
    }

    public void setOnMenuToggle(Runnable action) {
        this.onMenuToggleAction = action;
    }

    @FXML
    private void handleMenu() {
        if (onMenuToggleAction != null) {
            onMenuToggleAction.run();
        } else {
            System.out.println("Menu Clicked (No Action Set)");
        }
    }
}
