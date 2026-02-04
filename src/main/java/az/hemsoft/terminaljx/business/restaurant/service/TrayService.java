package az.hemsoft.terminaljx.business.restaurant.service;

import javafx.application.Platform;
import java.awt.*;
import java.awt.image.BufferedImage;

public class TrayService {
    private static TrayService instance;
    private TrayIcon trayIcon;

    private TrayService() {
    }

    public static synchronized TrayService getInstance() {
        if (instance == null) {
            instance = new TrayService();
        }
        return instance;
    }

    public void setupTray(String title, Runnable onShowApp) {
        if (!SystemTray.isSupported()) {
            System.err.println("\u274C SystemTray is not supported!");
            return;
        }

        try {
            SystemTray tray = SystemTray.getSystemTray();

            // Load actual icon from assets
            Image image = null;
            try (java.io.InputStream is = getClass().getResourceAsStream("/assets/hemsoft-logo.png")) {
                if (is != null) {
                    image = javax.imageio.ImageIO.read(is);
                }
            } catch (Exception e) {
                System.err.println("Error loading tray icon: " + e.getMessage());
            }

            if (image == null) {
                // Fallback to simple circular icon if loading fails
                int size = 16;
                BufferedImage fallbackImg = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
                Graphics2D g2 = fallbackImg.createGraphics();
                g2.setColor(new java.awt.Color(33, 150, 243)); // Blue #2196F3
                g2.fillOval(0, 0, size, size);
                g2.dispose();
                image = fallbackImg;
            }

            PopupMenu popup = new PopupMenu();

            MenuItem showItem = new MenuItem("S\u0259hif\u0259ni a\u00e7");
            showItem.addActionListener(e -> Platform.runLater(onShowApp));
            popup.add(showItem);

            popup.addSeparator();

            MenuItem exitItem = new MenuItem("\u00c7\u0131x\u0131\u015F");
            exitItem.addActionListener(e -> {
                ServerService.getInstance().stopServer();
                System.exit(0);
            });
            popup.add(exitItem);

            trayIcon = new TrayIcon(image.getScaledInstance(16, 16, Image.SCALE_SMOOTH), title, popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> Platform.runLater(onShowApp));

            tray.add(trayIcon);
            System.out.println("\u2705 System Tray Setup Complete");

        } catch (Exception e) {
            System.err.println("\u274C Failed to setup System Tray: " + e.getMessage());
        }
    }

    public void showMessage(String title, String message, TrayIcon.MessageType type) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, type);
        }
    }

    /**
     * Windows Registry-ə qeyd əlavə edərək proqramın Windows açılarkən
     * avtomatik başlamasını təmin edir.
     */
    public void enableAutoStart(boolean enable) {
        String appName = "HEMsoftTerminal";
        // Proqramın işlək .exe faylının yolunu tapmaq lazımdır.
        // Native build-də bu System.getProperty("user.dir") + "\\TerminalJX.exe"
        // olacaq.
        String jarPath = System.getProperty("user.dir") + "\\TerminalJX.exe --hidden";

        try {
            String command;
            if (enable) {
                command = "reg add \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \""
                        + appName + "\" /t REG_SZ /d \"" + jarPath + "\" /f";
            } else {
                command = "reg delete \"HKEY_CURRENT_USER\\Software\\Microsoft\\Windows\\CurrentVersion\\Run\" /v \""
                        + appName + "\" /f";
            }
            Runtime.getRuntime().exec(command);
            System.out.println("✅ Auto-start updated: " + enable);
        } catch (Exception e) {
            System.err.println("❌ Failed to update Auto-start: " + e.getMessage());
        }
    }
}
