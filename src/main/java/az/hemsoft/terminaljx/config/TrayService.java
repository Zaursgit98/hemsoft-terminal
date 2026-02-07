package az.hemsoft.terminaljx.config;

import java.awt.*;

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

    public void setupTray(String tooltip, Runnable onShowUI) {
        if (!SystemTray.isSupported())
            return;

        try {
            SystemTray tray = SystemTray.getSystemTray();
            Image image = Toolkit.getDefaultToolkit().createImage(getClass().getResource("/assets/hemsoft-logo.png"));

            PopupMenu popup = new PopupMenu();
            MenuItem showItem = new MenuItem("Göstər");
            showItem.addActionListener(e -> onShowUI.run());
            MenuItem exitItem = new MenuItem("Çıxış");
            exitItem.addActionListener(e -> System.exit(0));

            popup.add(showItem);
            popup.addSeparator();
            popup.add(exitItem);

            trayIcon = new TrayIcon(image, tooltip, popup);
            trayIcon.setImageAutoSize(true);
            trayIcon.addActionListener(e -> onShowUI.run());

            tray.add(trayIcon);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void showMessage(String title, String message, TrayIcon.MessageType type) {
        if (trayIcon != null) {
            trayIcon.displayMessage(title, message, type);
        }
    }
}
