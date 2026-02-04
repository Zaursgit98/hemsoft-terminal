package az.hemsoft.terminaljx.ui.controller;

import az.hemsoft.terminaljx.business.restaurant.service.UserService;
import az.hemsoft.terminaljx.business.restaurant.service.DateValidationService;
import az.hemsoft.terminaljx.business.restaurant.service.LogoService;
import javafx.animation.Animation;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.geometry.HPos;
import javafx.scene.control.*;
import javafx.scene.effect.DropShadow;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.stage.Modality;
import javafx.util.Duration;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.Window;
import javafx.scene.Scene;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

import java.io.File;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class PosSignInController {

    private BorderPane root;
    private String enteredPin = "";
    private boolean isPinIncorrect = false;
    private List<String> backgroundImages;
    private List<String> dynamicStories = new ArrayList<>();
    private File storiesDir;
    private int currentImageIndex = 0;
    private ImageView currentImageView;
    private HBox pinDotsContainer;
    private HBox indicatorsContainer;
    private Label timeLabel;
    private Label dateLabel;
    private Label deviceIdLabel;
    private Label appVersionLabel;
    private TranslateTransition shakeAnimation;
    private Timeline clockTimer;
    private UserService userService;

    public PosSignInController() {
        try {
            System.out.println("PosSignInController yarad\u0131l\u0131r...");

            // Initializing list immediately to avoid NPE in initializeUI
            this.backgroundImages = new ArrayList<>();

            // Sistem tarixinin geri Ã§É™kilmÉ™sini yoxla
            if (DateValidationService.checkDateManipulation()) {
                System.err.println("âš ï¸ XÆBÆRDARLIQ: Sistem tarixi geri Ã§É™kilmiÅŸdir!");
                showDateManipulationAlert();
            }

            DateValidationService.startPeriodicCheck(() -> {
                javafx.application.Platform.runLater(() -> {
                    showDateManipulationAlert();
                });
            });

            this.userService = new UserService();

            // Initialize root immediately
            root = new BorderPane();
            root.setStyle("-fx-background-color: #f5f5f5;");

            // Defer heavy initialization to runtime JavaFX thread
            javafx.application.Platform.runLater(() -> {
                System.out.println("Deferred Initialization Started...");
                try {
                    System.out.println("Background images y\u00fckl\u0259nir...");
                    initializeBackgroundImages();
                    System.out.println("Background images say\u0131: " + backgroundImages.size());
                    initializeStories();
                    // Reload image to show it after loading
                    // Reload image to show it after loading
                    // loadCurrentImage(); // REMOVED: Called in initializeUI -> createImageSlider

                    System.out.println("UI initialize edilir...");
                    initializeUI();
                    System.out.println("Animations setup edilir...");
                    setupAnimations();
                    System.out.println("Clock ba\u015Flad\u0131l\u0131r...");
                    startClock();
                    System.out.println("Device ID y\u00fckl\u0259nir...");
                    loadDeviceId();
                    System.out.println("App version y\u00fckl\u0259nir...");
                    loadAppVersion();
                    System.out.println("PosSignInController haz\u0131rd\u0131r!");
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            });
        } catch (Exception e) {
            System.err.println("PosSignInController yarad\u0131lark\u0259n x\u0259ta: " + e.getMessage());
            e.printStackTrace();
            if (root == null) {
                root = new BorderPane();
                root.setStyle("-fx-background-color: #f5f5f5;");
            }
            Label errorLabel = new Label("X\u0259ta: " + e.getMessage());
            root.setCenter(errorLabel);
        }
    }

    private void initializeBackgroundImages() {
        // ÅÉ™kil adlarÄ±nÄ± saxla - LogoService ilÉ™ yÃ¼klÉ™nÉ™cÉ™k
        String[] imageNames = {
                "slider 1.jpg",
                "slider 2.jpg",
                "slider 3.jpg",
                "slider 5.jpg",
                "posSignIn.png",
                "coffee-shop.jpg"
        };

        // ÅÉ™kil adlarÄ±nÄ± list-É™ É™lavÉ™ et (LogoService-dÉ™n yÃ¼klÉ™nÉ™cÉ™k)
        for (String imageName : imageNames) {
            backgroundImages.add(imageName);
        }

        // Background thread-dÉ™ ÅŸÉ™killÉ™ri DB-yÉ™ kÃ¶Ã§Ã¼r
        LogoService logoService = LogoService.getInstance();
        for (String imageName : imageNames) {
            logoService.migrateLogoToDatabase(imageName);
        }
    }

    private void initializeUI() {
        // Did not create new BorderPane, use existing root

        // SplitPane il\u0259 iki t\u0259r\u0259fi ay\u0131r
        SplitPane splitPane = new SplitPane();
        // Sol t\u0259r\u0259fi ki\u00e7ild\u0259k - Flutter: _getImageSectionWidth
        // g\u00f6r\u0259 58-65% aras\u0131
        splitPane.setDividerPositions(0.62); // 62% sol, 38% sa\u011f (bir az art\u0131r\u0131ld\u0131)

        // Sol tÉ™rÉ™f - ÅÉ™kil slider
        StackPane leftPane = createImageSlider();
        // SplitPane-dÉ™ elementlÉ™rin dÃ¼zgÃ¼n gÃ¶rÃ¼nmÉ™si Ã¼Ã§Ã¼n
        leftPane.setMinWidth(400);
        leftPane.setMinHeight(300);
        splitPane.getItems().add(leftPane);

        // SaÄŸ tÉ™rÉ™f - Login card
        StackPane rightPane = createLoginCard();
        // SplitPane-dÉ™ elementlÉ™rin dÃ¼zgÃ¼n gÃ¶rÃ¼nmÉ™si Ã¼Ã§Ã¼n
        rightPane.setMinWidth(400);
        rightPane.setMinHeight(300);
        splitPane.getItems().add(rightPane);

        // CSS file (styles.css) handles the divider styling now

        // Divider-Ä± sÃ¼rÃ¼klÉ™mÉ™ni qadaÄŸan et - mouse event-lÉ™rini blokla
        splitPane.addEventFilter(MouseEvent.MOUSE_PRESSED, e -> {
            javafx.scene.Node target = (javafx.scene.Node) e.getTarget();
            String styleClass = target.getStyleClass().toString();
            if (styleClass.contains("split-pane-divider")) {
                e.consume();
            }
        });
        splitPane.addEventFilter(MouseEvent.MOUSE_DRAGGED, e -> {
            javafx.scene.Node target = (javafx.scene.Node) e.getTarget();
            String styleClass = target.getStyleClass().toString();
            if (styleClass.contains("split-pane-divider")) {
                e.consume();
            }
        });
        splitPane.addEventFilter(MouseEvent.MOUSE_ENTERED, e -> {
            javafx.scene.Node target = (javafx.scene.Node) e.getTarget();
            String styleClass = target.getStyleClass().toString();
            if (styleClass.contains("split-pane-divider")) {
                target.setCursor(javafx.scene.Cursor.DEFAULT);
                e.consume();
            }
        });
        splitPane.addEventFilter(MouseEvent.MOUSE_MOVED, e -> {
            javafx.scene.Node target = (javafx.scene.Node) e.getTarget();
            String styleClass = target.getStyleClass().toString();
            if (styleClass.contains("split-pane-divider")) {
                target.setCursor(javafx.scene.Cursor.DEFAULT);
                e.consume();
            }
        });

        root.setCenter(splitPane);

        // Debug: SplitPane-in dÃ¼zgÃ¼n yaradÄ±ldÄ±ÄŸÄ±nÄ± yoxla
        System.out.println("SplitPane yaradÄ±ldÄ±, items sayÄ±: " + splitPane.getItems().size());
        System.out.println("Root center set edildi");

        // Klaviatura event-lÉ™ri
        root.setOnKeyPressed(this::handleKeyPress);
        root.setFocusTraversable(true);

        // Root resizing is handled by Scene
        root.setMinSize(800, 600);
    }

    private StackPane createImageSlider() {
        StackPane imagePane = new StackPane();
        imagePane.setStyle("-fx-background-color: #000000;");

        // Cari ÅŸÉ™kil - full screen Ã¼Ã§Ã¼n
        currentImageView = new ImageView();
        currentImageView.setPreserveRatio(false);
        currentImageView.fitWidthProperty().bind(imagePane.widthProperty());
        currentImageView.fitHeightProperty().bind(imagePane.heightProperty());
        currentImageView.setSmooth(true);
        loadCurrentImage();

        imagePane.getChildren().add(currentImageView);

        // Sol tÉ™rÉ™fdÉ™ Ã¼st mÉ™lumatlar (Company name, tarix, saat) - Flutter: top:
        // 10,
        // left: 15
        VBox topInfo = new VBox();
        topInfo.setAlignment(Pos.TOP_LEFT);
        // Flutter: mainAxisSize: MainAxisSize.min - yalnÄ±z mÉ™zmun qÉ™dÉ™r
        // geniÅŸlÉ™nsin
        topInfo.setMaxWidth(Region.USE_PREF_SIZE);
        topInfo.setMaxHeight(Region.USE_PREF_SIZE);
        StackPane.setAlignment(topInfo, Pos.TOP_LEFT);
        StackPane.setMargin(topInfo, new Insets(10, 0, 0, 15));

        HBox companyBox = new HBox(12);
        companyBox.setAlignment(Pos.CENTER_LEFT);
        companyBox.setMaxWidth(Region.USE_PREF_SIZE);

        Label companyLabel = new Label("HEMSOFT");
        companyLabel.setStyle(
                "-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #1e40af; -fx-letter-spacing: 0.5px;");

        // Touch icon - Flutter: Container with Icon.touch_app
        StackPane touchIconContainer = new StackPane();
        touchIconContainer.setPrefSize(40, 40);
        touchIconContainer.setStyle("-fx-background-color: #dbeafe; " +
                "-fx-background-radius: 10; " +
                "-fx-border-color: #93c5fd; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 10;");
        // Icon simulyasiyasÄ± - Circle yerinÉ™ Unicode icon istifadÉ™ edÉ™k
        Label touchIconLabel = new Label("\uD83D\uDC46");
        touchIconLabel.setStyle("-fx-font-size: 20px;");
        touchIconContainer.getChildren().add(touchIconLabel);

        companyBox.getChildren().addAll(companyLabel, touchIconContainer);

        // Touch icon click handler - Open Story Dialog
        touchIconContainer.setCursor(javafx.scene.Cursor.HAND);
        touchIconContainer.setOnMouseClicked(e -> showStoryDialog());

        HBox dateTimeBox = new HBox(10);
        dateTimeBox.setAlignment(Pos.CENTER_LEFT);
        dateTimeBox.setMaxWidth(Region.USE_PREF_SIZE);
        dateLabel = new Label();
        dateLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #2563eb;");

        timeLabel = new Label();
        timeLabel.setStyle("-fx-font-size: 13px; -fx-font-weight: 600; -fx-text-fill: #2563eb;");

        dateTimeBox.getChildren().addAll(dateLabel, timeLabel);

        VBox infoContainer = new VBox(5);
        infoContainer.setMaxWidth(Region.USE_PREF_SIZE);
        infoContainer.setMaxHeight(Region.USE_PREF_SIZE);
        infoContainer.getChildren().addAll(companyBox, dateTimeBox);
        infoContainer.setPadding(new Insets(14, 20, 14, 20));
        // Flutter: gradient background vÉ™ shadow
        infoContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, rgba(255,255,255,0.95), rgba(219,234,254,0.9)); "
                        +
                        "-fx-background-radius: 16; " +
                        "-fx-border-color: #93c5fd; " +
                        "-fx-border-width: 2; " +
                        "-fx-border-radius: 16; " +
                        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 15, 0, 0, 6), " +
                        "dropshadow(gaussian, rgba(0,0,0,0.1), 10, 0, 0, 3);");

        topInfo.getChildren().add(infoContainer);

        // AÅŸaÄŸÄ±da navigation controls - Flutter: bottom: 20
        HBox navigationControls = new HBox(20);
        navigationControls.setAlignment(Pos.CENTER);
        navigationControls.setMaxWidth(Region.USE_PREF_SIZE);

        Button prevButton = new Button("\u25C0");
        prevButton.setStyle("-fx-background-color: rgba(0,0,0,0.6); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-background-radius: 20; " +
                "-fx-border-width: 0; " + // Border yoxdur
                "-fx-padding: 8;");
        prevButton.setOnAction(e -> previousImage());

        // Indicators
        indicatorsContainer = new HBox(4);
        indicatorsContainer.setAlignment(Pos.CENTER);
        updateIndicators();

        Button nextButton = new Button("\u25B6");
        nextButton.setStyle("-fx-background-color: rgba(0,0,0,0.6); " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 16px; " +
                "-fx-background-radius: 20; " +
                "-fx-border-width: 0; " + // Border yoxdur
                "-fx-padding: 8;");
        nextButton.setOnAction(e -> nextImage());

        navigationControls.getChildren().addAll(prevButton, indicatorsContainer, nextButton);

        // Mouse wheel dÉ™stÉ™yi
        imagePane.setOnScroll(e -> {
            if (e.getDeltaY() > 0) {
                previousImage();
            } else {
                nextImage();
            }
        });

        // Navigation controls Ã¼Ã§Ã¼n Region ilÉ™ spacer É™lavÉ™ edÉ™k - aÅŸaÄŸÄ±da
        // yerlÉ™ÅŸsin
        // VBox ilÉ™ wrapper edÉ™k vÉ™ Region ilÉ™ spacer É™lavÉ™ edÉ™k
        VBox bottomContainer = new VBox();
        bottomContainer.setAlignment(Pos.BOTTOM_CENTER);
        Region spacer = new Region();
        VBox.setVgrow(spacer, Priority.ALWAYS);
        bottomContainer.getChildren().addAll(spacer, navigationControls);
        bottomContainer.setPadding(new Insets(0, 0, 20, 0)); // Flutter: bottom: 20

        // Top info vÉ™ bottom container StackPane-dÉ™
        imagePane.getChildren().addAll(topInfo, bottomContainer);
        StackPane.setAlignment(topInfo, Pos.TOP_LEFT);
        StackPane.setAlignment(bottomContainer, Pos.BOTTOM_CENTER);
        StackPane.setMargin(topInfo, new Insets(10, 0, 0, 15));

        return imagePane;
    }

    private StackPane createLoginCard() {
        StackPane loginPane = new StackPane();
        loginPane.setStyle("-fx-background-color: linear-gradient(from 0% 0% to 0% 100%, " +
                "#93c5fd 0%, #dbeafe 30%, #eff6ff 70%, #ffffff 100%);");

        // Login card
        VBox loginCard = new VBox(20);
        loginCard.setAlignment(Pos.CENTER);
        loginCard.setPadding(new Insets(30, 20, 30, 20));
        loginCard.setMaxWidth(420);
        loginCard.setMaxHeight(520);
        loginCard.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 24; " +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.15), 40, 0, 0, 15);");

        // Logo - SQLite-d\u0259n y\u00fckl\u0259
        ImageView logoView = new ImageView();
        try {
            LogoService logoService = LogoService.getInstance();
            Image logo = logoService.loadLogo("hem.png");

            if (logo != null) {
                // Logo y\u00fckl\u0259nm\u0259sini g\u00f6zl\u0259
                logo.progressProperty().addListener((observable, oldValue, newValue) -> {
                    if (newValue.doubleValue() == 1.0) {
                        System.out.println("\u2705 Logo y\u00fckl\u0259ndi, \u00f6l\u00e7\u00fc: " + logo.getWidth()
                                + "x" + logo.getHeight());
                    }
                });

                logoView.setImage(logo);
                logoView.setFitWidth(240);
                logoView.setFitHeight(100);
                logoView.setPreserveRatio(true);
                logoView.setSmooth(true); // Smooth rendering
                logoView.setCache(true); // Cache aktivl\u0259\u015fdir
                loginCard.getChildren().add(logoView);
            } else {
                // Logo yoxdursa, placeholder
                Label logoPlaceholder = new Label("HEMSOFT");
                logoPlaceholder.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");
                loginCard.getChildren().add(logoPlaceholder);
            }
        } catch (Exception e) {
            // Logo y\u00fckl\u0259n\u0259rk\u0259n x\u0259ta
            Label logoPlaceholder = new Label("HEMSOFT");
            logoPlaceholder.setStyle("-fx-font-size: 32px; -fx-font-weight: bold; -fx-text-fill: #1e40af;");
            loginCard.getChildren().add(logoPlaceholder);
            System.err.println("\u274C Logo y\u00fckl\u0259n\u0259rk\u0259n x\u0259ta: " + e.getMessage());
        }

        // PIN dots
        Region spacer1 = new Region();
        spacer1.setPrefHeight(15);
        loginCard.getChildren().add(spacer1);

        pinDotsContainer = new HBox();
        pinDotsContainer.setAlignment(Pos.CENTER);
        updatePinDots();
        loginCard.getChildren().add(pinDotsContainer);

        // Numeric keypad
        Region spacer2 = new Region();
        spacer2.setPrefHeight(20);
        loginCard.getChildren().add(spacer2);

        GridPane keypad = createKeypad();
        loginCard.getChildren().add(keypad);

        loginPane.getChildren().add(loginCard);

        // Telefon n\u00f6mr\u0259si (sol yuxar\u0131da)
        HBox phoneContainer = new HBox(10);
        phoneContainer.setAlignment(Pos.CENTER_LEFT);
        phoneContainer.setMaxWidth(Region.USE_PREF_SIZE);
        phoneContainer.setMaxHeight(Region.USE_PREF_SIZE);

        StackPane phoneIconContainer = new StackPane();
        phoneIconContainer.setPrefSize(30, 30);
        phoneIconContainer.setPadding(new Insets(6));
        phoneIconContainer.setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 8;");
        Label phoneIconLabel = new Label("\uD83D\uDCDE");
        phoneIconLabel.setStyle("-fx-font-size: 18px; -fx-text-alignment: center;");
        StackPane.setAlignment(phoneIconLabel, Pos.CENTER);
        phoneIconContainer.getChildren().add(phoneIconLabel);

        Label phoneLabel = new Label("+994 70 555 09 19");
        phoneLabel.setStyle(
                "-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #2563eb; -fx-letter-spacing: 0.5px;");

        phoneContainer.getChildren().addAll(phoneIconContainer, phoneLabel);

        phoneContainer.setPadding(new Insets(12, 16, 12, 16));
        phoneContainer.setStyle(
                "-fx-background-color: linear-gradient(to bottom right, rgba(255,255,255,0.95), rgba(219,234,254,0.8)); "
                        +
                        "-fx-background-radius: 16; " +
                        "-fx-border-color: #93c5fd; " +
                        "-fx-border-width: 1.5; " +
                        "-fx-border-radius: 16; " +
                        "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 12, 0, 0, 4), " +
                        "dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 2);");

        StackPane.setAlignment(phoneContainer, Pos.TOP_LEFT);
        StackPane.setMargin(phoneContainer, new Insets(10, 0, 0, 15));
        loginPane.getChildren().add(phoneContainer);

        // Menu button - top right
        StackPane menuContainer = new StackPane();
        menuContainer.setMaxWidth(Region.USE_PREF_SIZE);
        menuContainer.setMaxHeight(Region.USE_PREF_SIZE);

        String commonContainerStyle = "-fx-background-color: linear-gradient(to bottom right, rgba(255,255,255,0.95), rgba(239,246,255,0.8)); "
                + "-fx-background-radius: 16; " +
                "-fx-border-color: #bfdbfe; " +
                "-fx-border-width: 1.5; " +
                "-fx-border-radius: 16; " +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.2), 12, 0, 0, 4), " +
                "dropshadow(gaussian, rgba(0,0,0,0.05), 6, 0, 0, 2);";

        menuContainer.setStyle(commonContainerStyle);
        menuContainer.setPadding(new Insets(19));

        Label menuIcon = new Label("\u2630");
        menuIcon.setStyle("-fx-font-size: 20px; -fx-text-fill: #1d4ed8;");
        menuContainer.getChildren().add(menuIcon);
        menuContainer.setCursor(javafx.scene.Cursor.HAND);

        StackPane.setAlignment(menuContainer, Pos.TOP_RIGHT);
        StackPane.setMargin(menuContainer, new Insets(30, 20, 0, 0));
        loginPane.getChildren().add(menuContainer);

        // Device ID & Version
        HBox deviceIdContainer = new HBox(10);
        deviceIdContainer.setAlignment(Pos.CENTER_LEFT);
        deviceIdContainer.setMaxWidth(Region.USE_PREF_SIZE);
        deviceIdContainer.setMaxHeight(Region.USE_PREF_SIZE);
        deviceIdContainer.setStyle(commonContainerStyle);
        deviceIdContainer.setPadding(new Insets(12, 16, 12, 16));

        StackPane deviceIconContainer = new StackPane();
        deviceIconContainer.setPrefSize(30, 30);
        deviceIconContainer.setPadding(new Insets(6));
        deviceIconContainer.setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 8;");
        Label devIconLbl = new Label("\uD83D\uDCBB");
        devIconLbl.setStyle("-fx-font-size: 18px; -fx-text-fill: #1d4ed8;");
        StackPane.setAlignment(devIconLbl, Pos.CENTER);
        deviceIconContainer.getChildren().add(devIconLbl);

        VBox deviceIdBox = new VBox(2);
        Label deviceIdTitle = new Label("App ID");
        deviceIdTitle.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: 500; -fx-text-fill: #2563eb; -fx-letter-spacing: 0.5px;");
        deviceIdLabel = new Label("Loading...");
        deviceIdLabel.setStyle(
                "-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #2563eb; -fx-letter-spacing: 0.3px;");
        deviceIdBox.getChildren().addAll(deviceIdTitle, deviceIdLabel);
        deviceIdContainer.getChildren().addAll(deviceIconContainer, deviceIdBox);

        HBox versionContainer = new HBox(10);
        versionContainer.setAlignment(Pos.CENTER_LEFT);
        versionContainer.setMaxWidth(Region.USE_PREF_SIZE);
        versionContainer.setMaxHeight(Region.USE_PREF_SIZE);
        versionContainer.setStyle(commonContainerStyle);
        versionContainer.setPadding(new Insets(12, 16, 12, 16));

        StackPane versionIconContainer = new StackPane();
        versionIconContainer.setPrefSize(30, 30);
        versionIconContainer.setPadding(new Insets(6));
        versionIconContainer.setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 8;");
        Label vermIconLbl = new Label("\u2139\uFE0F");
        vermIconLbl.setStyle("-fx-font-size: 18px; -fx-text-fill: #1d4ed8;");
        StackPane.setAlignment(vermIconLbl, Pos.CENTER);
        versionIconContainer.getChildren().add(vermIconLbl);

        VBox versionBox = new VBox(2);
        Label versionTitle = new Label("Version");
        versionTitle.setStyle(
                "-fx-font-size: 10px; -fx-font-weight: 500; -fx-text-fill: #2563eb; -fx-letter-spacing: 0.5px;");
        appVersionLabel = new Label("V 1.0.0");
        appVersionLabel.setStyle(
                "-fx-font-size: 11px; -fx-font-weight: 600; -fx-text-fill: #2563eb; -fx-letter-spacing: 0.3px;");
        versionBox.getChildren().addAll(versionTitle, appVersionLabel);
        versionContainer.getChildren().addAll(versionIconContainer, versionBox);

        StackPane.setAlignment(deviceIdContainer, Pos.BOTTOM_LEFT);
        StackPane.setMargin(deviceIdContainer, new Insets(0, 0, 20, 15));

        StackPane.setAlignment(versionContainer, Pos.BOTTOM_RIGHT);
        StackPane.setMargin(versionContainer, new Insets(0, 15, 20, 0));

        loginPane.getChildren().addAll(deviceIdContainer, versionContainer);

        return loginPane;
    }

    private GridPane createKeypad() {
        GridPane keypad = new GridPane();
        // Flutter: crossAxisSpacing: 18, mainAxisSpacing: 20
        keypad.setHgap(18);
        keypad.setVgap(20);
        keypad.setAlignment(Pos.CENTER);
        // Flutter: padding: symmetric(horizontal: 12)
        keypad.setPadding(new Insets(0, 12, 0, 12));

        // Use constraints for perfect alignment
        for (int i = 0; i < 3; i++) {
            javafx.scene.layout.ColumnConstraints colConst = new javafx.scene.layout.ColumnConstraints();
            colConst.setPercentWidth(33.33);
            colConst.setHalignment(HPos.CENTER);
            keypad.getColumnConstraints().add(colConst);
        }

        // Flutter: itemCount: 12, index 9 = X (Clear), index 10 = 0, index 11 =
        // Backspace
        for (int index = 0; index < 12; index++) {
            int row = index / 3;
            int col = index % 3;

            if (index == 9) {
                // Clear button (X)
                Button clearButton = createActionButton("X", this::clearPin);
                keypad.add(clearButton, col, row);
            } else if (index == 10) {
                // Zero button
                Button zeroButton = createNumberButton("0");
                keypad.add(zeroButton, col, row);
            } else if (index == 11) {
                // Backspace button
                Button backspaceButton = createActionButton("\u232B", this::deleteLastDigit);
                keypad.add(backspaceButton, col, row);
            } else {
                // 1-9 rÉ™qÉ™mlÉ™ri (index 0-8)
                Button numButton = createNumberButton(String.valueOf(index + 1));
                keypad.add(numButton, col, row);
            }
        }

        return keypad;
    }

    private Button createNumberButton(String number) {
        Button button = new Button(number);
        // Flutter: childAspectRatio: 1.8
        button.setPrefSize(120, 67);

        // Gradient blue styles
        String baseStyle = "-fx-background-color: linear-gradient(to bottom right, #3b82f6, #1d4ed8); " +
                "-fx-background-radius: 12; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 36px; " +
                "-fx-font-weight: 600; " +
                "-fx-letter-spacing: 1.0; " +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.3), 8, 0, 0, 4);";

        button.setStyle(baseStyle);
        button.setCursor(javafx.scene.Cursor.HAND);
        button.setOnAction(e -> numberPressed(number));

        // Hover effect
        button.setOnMouseEntered(
                e -> button.setStyle(baseStyle.replace("#3b82f6", "#2563eb").replace("#1d4ed8", "#1e40af")));
        button.setOnMouseExited(e -> button.setStyle(baseStyle));

        return button;
    }

    private Button createActionButton(String text, Runnable action) {
        // Clear (X) vÉ™ Backspace (âŒ«) dÃ¼ymÉ™lÉ™ri - Solid blue color to match
        // Flutter
        // request
        Button button = new Button(text);
        button.setPrefSize(120, 67);

        // Flutter button gradient: [Colors.blue.shade600, Colors.blue.shade600] ->
        // solid #2563eb
        String actionStyle = "-fx-background-color: #2563eb; " + // Solid blue
                "-fx-background-radius: 12; " +
                "-fx-text-fill: white; " +
                "-fx-font-size: 36px; " + // Same size as numbers
                "-fx-font-weight: 600; " +
                "-fx-effect: dropshadow(gaussian, rgba(59,130,246,0.3), 8, 0, 0, 4);";

        // Backspace icon might need adjustment if using special char
        if (text.equals("\u232B")) {
            actionStyle += "-fx-font-size: 28px;";
        }

        button.setStyle(actionStyle);
        button.setCursor(javafx.scene.Cursor.HAND);
        button.setOnAction(e -> action.run());

        // Hover effect
        String finalActionStyle = actionStyle;
        button.setOnMouseEntered(e -> button.setStyle(finalActionStyle.replace("#2563eb", "#1d4ed8")));
        button.setOnMouseExited(e -> button.setStyle(finalActionStyle));

        return button;
    }

    private void updatePinDots() {
        pinDotsContainer.getChildren().clear();
        for (int i = 0; i < 4; i++) {
            Circle dot = new Circle(9);
            if (i < enteredPin.length()) {
                dot.setFill(isPinIncorrect ? Color.RED : Color.BLACK);
            } else {
                dot.setFill(isPinIncorrect ? Color.RED : Color.LIGHTGRAY);
            }
            if (i > 0) {
                HBox.setMargin(dot, new Insets(0, 0, 0, 16));
            }
            pinDotsContainer.getChildren().add(dot);
        }
    }

    private void numberPressed(String number) {
        if (enteredPin.length() < 4) {
            enteredPin += number;
            isPinIncorrect = false;
            updatePinDots();

            if (enteredPin.length() == 4) {
                confirmPin();
            }
        }
    }

    private void deleteLastDigit() {
        if (!enteredPin.isEmpty()) {
            enteredPin = enteredPin.substring(0, enteredPin.length() - 1);
            isPinIncorrect = false;
            updatePinDots();
        }
    }

    private void clearPin() {
        enteredPin = "";
        isPinIncorrect = false;
        updatePinDots();
    }

    private void confirmPin() {
        // Hardcoded testing PIN or Service check
        boolean success = "1111".equals(enteredPin) || (userService != null && userService.signIn(enteredPin));

        if (success) {
            System.out.println("PIN doÄŸrulandÄ±: " + enteredPin);
            clearPin();

            javafx.application.Platform.runLater(() -> {
                try {
                    openMainWindow();
                } catch (Exception e) {
                    e.printStackTrace();
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("XÉ™ta");
                    alert.setHeaderText("GiriÅŸ XÉ™tasÄ±");
                    alert.setContentText("Æsas sÉ™hifÉ™yÉ™ keÃ§id zamanÄ± xÉ™ta: " + e.getMessage());
                    alert.showAndWait();
                }
            });
        } else {
            isPinIncorrect = true;
            updatePinDots();
            shakeAnimation.play();
            enteredPin = "";
            Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
                isPinIncorrect = false;
                updatePinDots();
            }));
            timeline.play();
        }
    }

    private void setupAnimations() {
        shakeAnimation = new TranslateTransition(Duration.millis(50), pinDotsContainer);
        shakeAnimation.setFromX(0);
        shakeAnimation.setToX(10);
        shakeAnimation.setAutoReverse(true);
        shakeAnimation.setCycleCount(6);
    }

    private void startClock() {
        clockTimer = new Timeline(new KeyFrame(Duration.seconds(1), e -> updateClock()));
        clockTimer.setCycleCount(Animation.INDEFINITE);
        clockTimer.play();
        updateClock();
    }

    private void updateClock() {
        LocalDateTime now = LocalDateTime.now();
        dateLabel.setText(now.format(DateTimeFormatter.ofPattern("dd.MM.yyyy")));
        timeLabel.setText(now.format(DateTimeFormatter.ofPattern("HH:mm:ss")));
    }

    private void loadCurrentImage() {
        if (backgroundImages.isEmpty()) {
            System.out.println("Background images boÅŸdur!");
            return;
        }

        try {
            String imageName = backgroundImages.get(currentImageIndex);

            LogoService logoService = LogoService.getInstance();
            Image image = logoService.loadLogo(imageName);

            currentImageView.setImage(image);
        } catch (Exception e) {
            e.printStackTrace();
            currentImageView.setImage(null);
        }
        updateIndicators();
    }

    private void updateIndicators() {
        if (indicatorsContainer == null || backgroundImages.isEmpty())
            return;

        indicatorsContainer.getChildren().clear();
        for (int i = 0; i < backgroundImages.size(); i++) {
            Circle indicator = new Circle(i == currentImageIndex ? 6 : 4);
            indicator.setFill(i == currentImageIndex ? Color.WHITE : Color.rgb(255, 255, 255, 0.5));
            DropShadow shadow = new DropShadow(4, Color.rgb(0, 0, 0, 0.3));
            shadow.setOffsetY(2);
            indicator.setEffect(shadow);
            indicatorsContainer.getChildren().add(indicator);
        }
    }

    private void nextImage() {
        if (backgroundImages.isEmpty())
            return;
        currentImageIndex = (currentImageIndex + 1) % backgroundImages.size();
        loadCurrentImage();
    }

    private void previousImage() {
        if (backgroundImages.isEmpty())
            return;
        currentImageIndex = (currentImageIndex - 1 + backgroundImages.size()) % backgroundImages.size();
        loadCurrentImage();
    }

    private void loadDeviceId() {
        if (deviceIdLabel != null) {
            String deviceId = System.getProperty("user.name");
            if (deviceId.length() > 20) {
                deviceId = "â€¦" + deviceId.substring(deviceId.length() - 20);
            }
            deviceIdLabel.setText(deviceId);
        }
    }

    private void loadAppVersion() {
        if (appVersionLabel != null) {
            appVersionLabel.setText("V 1.0.0");
        }
    }

    private void handleKeyPress(KeyEvent event) {
        if (event.getCode().isDigitKey()) {
            String digit = event.getText();
            if (!digit.isEmpty()) {
                numberPressed(digit);
            }
        } else if (event.getCode() == KeyCode.ENTER) {
            if (enteredPin.length() == 4) {
                confirmPin();
            }
        } else if (event.getCode() == KeyCode.BACK_SPACE) {
            deleteLastDigit();
        } else if (event.getCode() == KeyCode.DELETE) {
            clearPin();
        }
    }

    public BorderPane getView() {
        return root;
    }

    public void dispose() {
        if (clockTimer != null) {
            clockTimer.stop();
        }
    }

    private void showDateManipulationAlert() {
        try {
            javafx.scene.control.Alert alert = new javafx.scene.control.Alert(
                    javafx.scene.control.Alert.AlertType.WARNING);
            alert.setTitle("âš ï¸ XÆBÆRDARLIQ");
            alert.setHeaderText("Sistem Tarixi Geri Ã‡É™kilmiÅŸdir!");
            alert.setContentText("Sistem tarixi geri Ã§É™kilmiÅŸdir. Bu, tÉ™hlÃ¼kÉ™sizlik riski yarada bilÉ™r.\n\n" +
                    "ZÉ™hmÉ™t olmasa sistem tarixini dÃ¼zgÃ¼n tÉ™yin edin vÉ™ proqramÄ± yenidÉ™n baÅŸladÄ±n.");

            alert.initModality(Modality.APPLICATION_MODAL);
            alert.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void initializeStories() {
        try {
            String userHome = System.getProperty("user.home");
            File appDir = new File(userHome, ".hemsoft");
            storiesDir = new File(appDir, "pos_stories");

            if (!storiesDir.exists()) {
                storiesDir.mkdirs();
            }

            File[] files = storiesDir.listFiles((dir, name) -> {
                String lower = name.toLowerCase();
                return lower.endsWith(".jpg") || lower.endsWith(".jpeg") ||
                        lower.endsWith(".png") || lower.endsWith(".gif") ||
                        lower.endsWith(".mp4") || lower.endsWith(".mov");
            });

            if (files != null) {
                for (File f : files) {
                    if (!dynamicStories.contains(f.getAbsolutePath())) {
                        dynamicStories.add(f.getAbsolutePath());
                    }
                }
                dynamicStories.sort((p1, p2) -> {
                    File f1 = new File(p1);
                    File f2 = new File(p2);
                    return Long.compare(f2.lastModified(), f1.lastModified());
                });
            }
        } catch (Exception e) {
            System.err.println("Stories init error: " + e.getMessage());
        }
    }

    private void showStoryDialog() {
        Dialog<Void> dialog = new Dialog<>();
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(javafx.stage.StageStyle.TRANSPARENT);

        if (root.getScene() != null && root.getScene().getWindow() != null) {
            dialog.initOwner(root.getScene().getWindow());
        }

        DialogPane dialogPane = new DialogPane();
        dialogPane
                .setBackground(new Background(new BackgroundFill(Color.TRANSPARENT, CornerRadii.EMPTY, Insets.EMPTY)));
        dialog.setDialogPane(dialogPane);

        VBox mainContainer = new VBox(20);
        mainContainer.setPrefSize(800, 600);
        mainContainer.setPadding(new Insets(20));
        mainContainer.setStyle("-fx-background-color: linear-gradient(to bottom, #eff6ff, white); " +
                "-fx-background-radius: 20; " +
                "-fx-border-color: #93c5fd; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 20; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 20, 0, 0, 10);");

        HBox header = new HBox(16);
        header.setAlignment(Pos.CENTER_LEFT);

        StackPane iconContainer = new StackPane();
        iconContainer.setPadding(new Insets(12));
        iconContainer.setStyle("-fx-background-color: #dbeafe; -fx-background-radius: 12;");
        Label iconLabel = new Label("ğŸ–¼ï¸");
        iconLabel.setStyle("-fx-font-size: 24px;");
        iconContainer.getChildren().add(iconLabel);

        Label titleLabel = new Label("AnlÄ±q HekayÉ™lÉ™r");
        titleLabel.setStyle("-fx-font-size: 20px; -fx-font-weight: bold; -fx-text-fill: #1f2937;");
        HBox.setHgrow(titleLabel, Priority.ALWAYS);
        titleLabel.setMaxWidth(Double.MAX_VALUE);

        Button closeButton = new Button("âœ•");
        closeButton.setStyle(
                "-fx-background-color: transparent; -fx-text-fill: #9ca3af; -fx-font-size: 18px; -fx-cursor: hand;");
        closeButton.setOnAction(e -> dialog.setResult(null));

        header.getChildren().addAll(iconContainer, titleLabel, closeButton);

        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setFitToWidth(true);
        scrollPane.setStyle("-fx-background: transparent; -fx-background-color: transparent;");

        TilePane grid = new TilePane();
        grid.setPrefColumns(3);
        grid.setHgap(12);
        grid.setVgap(12);
        grid.setStyle("-fx-background-color: transparent;");

        updateStoryGrid(grid, dialog);

        scrollPane.setContent(grid);
        VBox.setVgrow(scrollPane, Priority.ALWAYS);

        mainContainer.getChildren().addAll(header, scrollPane);
        dialogPane.setContent(mainContainer);

        dialog.showAndWait();
    }

    private void updateStoryGrid(TilePane grid, Dialog<Void> dialog) {
        grid.getChildren().clear();

        for (String path : dynamicStories) {
            StackPane item = createStoryItem(path, () -> {
                deleteStory(path);
                updateStoryGrid(grid, dialog);
            });
            grid.getChildren().add(item);
        }

        StackPane addButton = new StackPane();
        addButton.setPrefSize(200, 200);
        addButton.setStyle("-fx-background-color: #eff6ff; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #93c5fd; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 12; " +
                "-fx-border-style: dashed; " +
                "-fx-cursor: hand;");

        VBox addContent = new VBox(8);
        addContent.setAlignment(Pos.CENTER);
        Label addIcon = new Label("â•");
        addIcon.setStyle("-fx-font-size: 32px; -fx-text-fill: #1d4ed8;");
        Label addText = new Label("Yeni É™lavÉ™ et");
        addText.setStyle("-fx-font-size: 12px; -fx-font-weight: 600; -fx-text-fill: #1d4ed8;");
        addContent.getChildren().addAll(addIcon, addText);

        addButton.getChildren().add(addContent);
        addButton.setOnMouseClicked(e -> {
            addNewStory(dialog.getOwner());
            updateStoryGrid(grid, dialog);
        });

        grid.getChildren().add(addButton);
    }

    private StackPane createStoryItem(String path, Runnable onDelete) {
        StackPane item = new StackPane();
        item.setPrefSize(200, 200);
        item.setStyle("-fx-background-color: white; " +
                "-fx-background-radius: 12; " +
                "-fx-border-color: #93c5fd; " +
                "-fx-border-width: 2; " +
                "-fx-border-radius: 12; " +
                "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 4, 0, 2, 0);");

        try {
            boolean isVideo = path.toLowerCase().endsWith(".mp4") || path.toLowerCase().endsWith(".mov");

            if (isVideo) {
                item.setStyle(item.getStyle() + "-fx-background-color: black;");
                VBox videoContent = new VBox(8);
                videoContent.setAlignment(Pos.CENTER);
                Label videoIcon = new Label("ğŸ¥");
                videoIcon.setStyle("-fx-font-size: 32px; -fx-text-fill: white;");
                Label videoText = new Label("Video");
                videoText.setStyle("-fx-font-size: 12px; -fx-text-fill: white;");
                videoContent.getChildren().addAll(videoIcon, videoText);
                item.getChildren().add(videoContent);
            } else {
                File file = new File(path);
                Image img = new Image(file.toURI().toString(), 200, 200, true, true);
                ImageView iv = new ImageView(img);
                iv.setFitWidth(196);
                iv.setFitHeight(196);

                javafx.scene.shape.Rectangle clip = new javafx.scene.shape.Rectangle(196, 196);
                clip.setArcWidth(12);
                clip.setArcHeight(12);
                iv.setClip(clip);

                item.getChildren().add(iv);
            }

            Button deleteBtn = new Button("âœ•");
            deleteBtn.setStyle(
                    "-fx-background-color: #dc2626; -fx-text-fill: white; -fx-font-size: 10px; -fx-background-radius: 50%; -fx-min-width: 24px; -fx-min-height: 24px; -fx-cursor: hand;");
            StackPane.setAlignment(deleteBtn, Pos.TOP_RIGHT);
            StackPane.setMargin(deleteBtn, new Insets(8));
            deleteBtn.setOnAction(e -> onDelete.run());

            item.getChildren().add(deleteBtn);

        } catch (Exception e) {
            e.printStackTrace();
        }

        return item;
    }

    private void addNewStory(Window owner) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Media seÃ§in");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Media Files", "*.jpg", "*.jpeg", "*.png", "*.gif", "*.mp4", "*.mov"),
                new FileChooser.ExtensionFilter("Images", "*.jpg", "*.jpeg", "*.png", "*.gif"),
                new FileChooser.ExtensionFilter("Videos", "*.mp4", "*.mov"));

        File selectedFile = fileChooser.showOpenDialog(owner);
        if (selectedFile != null) {
            try {
                String ext = selectedFile.getName().substring(selectedFile.getName().lastIndexOf("."));
                String newName = "story_" + System.currentTimeMillis() + ext;
                File destFile = new File(storiesDir, newName);

                Files.copy(selectedFile.toPath(), destFile.toPath(), StandardCopyOption.REPLACE_EXISTING);

                dynamicStories.add(0, destFile.getAbsolutePath());
            } catch (Exception e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR,
                        "Fayl yadda saxlanÄ±larkÉ™n xÉ™ta baÅŸ verdi: " + e.getMessage());
                alert.show();
            }
        }
    }

    private void deleteStory(String path) {
        try {
            File f = new File(path);
            if (f.exists()) {
                f.delete();
            }
            dynamicStories.remove(path);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void openMainWindow() throws java.io.IOException {
        javafx.fxml.FXMLLoader loader = new javafx.fxml.FXMLLoader(
                getClass().getResource("/az/hemsoft/terminaljx/views/MainWindow.fxml"));
        javafx.scene.Parent mainRoot = loader.load();

        MainWindowController controller = loader.getController();
        if (userService != null && userService.getCurrentUser() != null) {
            // Decoupled from User model for UI testing
            controller.initData("Admin");
        } else {
            controller.initData("Admin");
        }

        Stage stage = (Stage) root.getScene().getWindow();
        Scene scene = new Scene(mainRoot, stage.getScene().getWidth(), stage.getScene().getHeight());

        // Re-apply styles
        scene.getStylesheets().add(org.kordamp.bootstrapfx.BootstrapFX.bootstrapFXStylesheet());
        try {
            java.net.URL cssUrl = getClass().getResource("/styles.css");
            if (cssUrl != null) {
                scene.getStylesheets().add(cssUrl.toExternalForm());
            } else {
                // Try file system if resource not found (dev mode)
                java.io.File cssFile = new java.io.File("src/main/resources/styles.css");
                if (cssFile.exists()) {
                    scene.getStylesheets().add(cssFile.toURI().toString());
                }
            }
        } catch (Exception e) {
            System.err.println("Style load error: " + e.getMessage());
        }

        stage.setScene(scene);
        stage.setFullScreen(true);
    }
}
