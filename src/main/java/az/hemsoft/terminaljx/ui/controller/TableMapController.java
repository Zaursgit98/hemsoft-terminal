package az.hemsoft.terminaljx.ui.controller;

import javafx.fxml.FXML;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.input.MouseButton;
import javafx.scene.input.ScrollEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.StackPane;
import javafx.scene.transform.Scale;
import javafx.scene.transform.Translate;

public class TableMapController {

    @FXML
    private StackPane mapContainer;
    @FXML
    private Pane mapCanvas;

    private final double MIN_SCALE = 0.5;
    private final double MAX_SCALE = 3.0;

    // Transforms
    private Scale currentScale = new Scale(1, 1, 0, 0); // Pivot 0,0 initially
    private Translate currentTranslate = new Translate(0, 0);

    // Mouse drag context
    private double mouseAnchorX;
    private double mouseAnchorY;
    private double translateAnchorX;
    private double translateAnchorY;

    @FXML
    public void initialize() {
        // Setup Transforms
        mapCanvas.getTransforms().addAll(currentTranslate, currentScale);

        // Event Handlers for Panning
        mapContainer.setOnMousePressed(event -> {
            mouseAnchorX = event.getSceneX();
            mouseAnchorY = event.getSceneY();
            translateAnchorX = currentTranslate.getX();
            translateAnchorY = currentTranslate.getY();
        });

        mapContainer.setOnMouseDragged(event -> {
            double deltaX = event.getSceneX() - mouseAnchorX;
            double deltaY = event.getSceneY() - mouseAnchorY;

            // Adjust translation
            currentTranslate.setX(translateAnchorX + deltaX);
            currentTranslate.setY(translateAnchorY + deltaY);
        });

        // Event Handler for Zooming (Scroll)
        mapContainer.setOnScroll(this::handleScrollZoom);

        // Populate with Mock Tables
        addMockTables();
    }

    private void handleScrollZoom(ScrollEvent event) {
        double delta = event.getDeltaY();
        double scaleFactor = (delta > 0) ? 1.1 : 0.9;

        applyZoom(scaleFactor, event.getX(), event.getY());
        event.consume();
    }

    @FXML
    private void handleZoomIn() {
        applyZoom(1.2, mapContainer.getWidth() / 2, mapContainer.getHeight() / 2);
    }

    @FXML
    private void handleZoomOut() {
        applyZoom(0.8, mapContainer.getWidth() / 2, mapContainer.getHeight() / 2);
    }

    private void applyZoom(double factor, double pivotX, double pivotY) {
        double oldScale = currentScale.getX();
        double newScale = oldScale * factor;

        // Clamp scale
        if (newScale < MIN_SCALE)
            newScale = MIN_SCALE;
        if (newScale > MAX_SCALE)
            newScale = MAX_SCALE;

        // Re-calculate factor to match clamped scale
        factor = newScale / oldScale;

        // Adjust scale
        currentScale.setX(newScale);
        currentScale.setY(newScale);

        double f = factor - 1;

        double dx = (pivotX - currentTranslate.getX()) * f;
        double dy = (pivotY - currentTranslate.getY()) * f;

        currentTranslate.setX(currentTranslate.getX() - dx);
        currentTranslate.setY(currentTranslate.getY() - dy);
    }

    private void addMockTables() {
        // Table 1 (Free)
        Pane table1 = createTable("M-01", 100, 100, 100, 100, false, 0, null, null);
        mapCanvas.getChildren().add(table1);

        // Table 2 (Occupied, Round, Active)
        Pane table2 = createRoundTable("M-02", 300, 150, 60, true, 45.50, "12:30", "Ali");
        mapCanvas.getChildren().add(table2);

        // Table 3 (Occupied, Rect, Active)
        Pane table3 = createTable("M-03", 500, 200, 140, 100, true, 125.00, "13:15", "V\u0259li");
        mapCanvas.getChildren().add(table3);

        // Table 4 (Free)
        Pane table4 = createTable("M-04", 150, 400, 100, 100, false, 0, null, null);
        mapCanvas.getChildren().add(table4);
    }

    private Pane createTable(String name, double x, double y, double w, double h, boolean isOccupied, double amount,
            String time, String waiterBox) {
        StackPane tableGroup = new StackPane();
        tableGroup.setLayoutX(x);
        tableGroup.setLayoutY(y);
        tableGroup.setPrefSize(w, h);
        tableGroup.getStyleClass().add("table-node");
        tableGroup.getStyleClass().add(isOccupied ? "table-occupied" : "table-free");

        tableGroup.getChildren().add(buildTableContent(name, isOccupied, amount, time, waiterBox));

        // Setup Interactions (Click + Context Menu)
        setupTableInteraction(tableGroup, name, isOccupied);

        return tableGroup;
    }

    private Pane createRoundTable(String name, double x, double y, double r, boolean isOccupied, double amount,
            String time, String waiterName) {
        StackPane tableGroup = new StackPane();
        tableGroup.setLayoutX(x);
        tableGroup.setLayoutY(y);
        tableGroup.setPrefSize(r * 2, r * 2);
        tableGroup.getStyleClass().add("table-node");
        tableGroup.getStyleClass().add(isOccupied ? "table-occupied" : "table-free");
        tableGroup.getStyleClass().add("table-round");

        tableGroup.getChildren().add(buildTableContent(name, isOccupied, amount, time, waiterName));

        // Setup Interactions (Click + Context Menu)
        setupTableInteraction(tableGroup, name, isOccupied);

        return tableGroup;
    }

    private javafx.scene.layout.VBox buildTableContent(String name, boolean isOccupied, double amount, String time,
            String waiterName) {
        javafx.scene.layout.VBox content = new javafx.scene.layout.VBox(2);
        content.setAlignment(javafx.geometry.Pos.CENTER);

        // Name
        Label nameLbl = new Label(name);
        nameLbl.getStyleClass().add("table-name");
        content.getChildren().add(nameLbl);

        if (isOccupied) {
            // Time (Active since)
            if (time != null && !time.isEmpty()) {
                Label timeLbl = new Label(time);
                timeLbl.getStyleClass().add("table-info");
                content.getChildren().add(timeLbl);
            }

            // Icons Row (Eye + Waiter)
            javafx.scene.layout.HBox iconsRow = new javafx.scene.layout.HBox(4);
            iconsRow.setAlignment(javafx.geometry.Pos.CENTER);

            Label eyeIcon = new Label("\uD83D\uDC41"); // Unicode Eye
            eyeIcon.setStyle("-fx-text-fill: #2563eb; -fx-font-size: 12px;");

            Label waiterLbl = new Label(waiterName != null ? waiterName : "-");
            waiterLbl.getStyleClass().add("table-info");
            waiterLbl.setStyle("-fx-font-weight: bold; -fx-font-size: 10px;");

            iconsRow.getChildren().addAll(eyeIcon, waiterLbl);
            content.getChildren().add(iconsRow);

            Label statusBadge = new Label("Aktiv");
            statusBadge.setStyle(
                    "-fx-background-color: #dcfce7; -fx-text-fill: #166534; -fx-background-radius: 4; -fx-padding: 2 6 2 6; -fx-font-size: 9px; -fx-font-weight: bold;");
            content.getChildren().add(statusBadge);

        } else {
            // Free State
            Label freeLbl = new Label("Bo\u015f");
            freeLbl.getStyleClass().add("table-info");
            content.getChildren().add(freeLbl);
        }

        return content;
    }

    private void setupTableInteraction(StackPane tableGroup, String name, boolean isOccupied) {
        // Visual click effect
        tableGroup.setOnMousePressed(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                tableGroup.setScaleX(0.95);
                tableGroup.setScaleY(0.95);
            }
        });
        tableGroup.setOnMouseReleased(e -> {
            if (e.getButton() == MouseButton.PRIMARY) {
                tableGroup.setScaleX(1.0);
                tableGroup.setScaleY(1.0);
                System.out.println("Left Click on Table: " + name);
            }
        });

        // Context Menu
        ContextMenu contextMenu = new ContextMenu();
        if (isOccupied) {
            MenuItem itemInfo = new MenuItem("Masa M\u0259lumat\u0131");
            MenuItem itemOrders = new MenuItem("Sifari\u015fl\u0259r");
            MenuItem itemMove = new MenuItem("Masadan k\u00f6\u00e7\u00fcr");
            MenuItem itemClose = new MenuItem("Masadan Ba\u011fla (\u00d6d\u0259ni\u015f)");
            contextMenu.getItems().addAll(itemInfo, itemOrders, itemMove, itemClose);
        } else {
            MenuItem itemOpen = new MenuItem("Yeni Sifari\u015f");
            MenuItem itemReserve = new MenuItem("Rezerv et");
            contextMenu.getItems().addAll(itemOpen, itemReserve);
        }

        tableGroup.setOnContextMenuRequested(e -> {
            contextMenu.show(tableGroup, e.getScreenX(), e.getScreenY());
        });
    }
}
