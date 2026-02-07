package az.hemsoft.terminaljx.config;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;

public class ConfigService {
    private static ConfigService instance;
    private AppConfig config;

    private ConfigService() {
        loadConfig();
    }

    public static synchronized ConfigService getInstance() {
        if (instance == null) {
            instance = new ConfigService();
        }
        return instance;
    }

    private void loadConfig() {
        try {
            Yaml yaml = new Yaml(new Constructor(AppConfig.class, new org.yaml.snakeyaml.LoaderOptions()));

            // 1. Try external config first (allows overriding)
            File externalConfig = new File("application.yaml");
            InputStream inputStream;

            if (externalConfig.exists()) {
                System.out.println("\u2699 Loading config from external file: " + externalConfig.getAbsolutePath());
                inputStream = new FileInputStream(externalConfig);
            } else {
                // 2. Fallback to embedded resource
                System.out.println("\u2699 Loading internal default config");
                inputStream = getClass().getClassLoader().getResourceAsStream("application.yaml");
            }

            if (inputStream != null) {
                config = yaml.load(inputStream);
                if (config == null) {
                    config = new AppConfig();
                }
            } else {
                config = new AppConfig();
                System.err.println("\u26A0 application.yaml not found! Using internal defaults.");
            }

        } catch (Exception e) {
            System.err.println("\u274C Failed to load config: " + e.getMessage());
            config = new AppConfig(); // Ensure config is never null
        }
    }

    public AppConfig getConfig() {
        return config;
    }

    // Dynamic settings getters (Database priority)
    public int getServerPort() {
        try {
            String dbPort = DatabaseManager.getInstance().getSetting("server_port", null);
            if (dbPort != null)
                return Integer.parseInt(dbPort);
        } catch (Exception ignored) {
        }

        return (config != null && config.getServer() != null) ? config.getServer().getPort() : 8080;
    }

    public boolean isServerEnabled() {
        try {
            String dbEnabled = DatabaseManager.getInstance().getSetting("server_enabled", null);
            if (dbEnabled != null)
                return Boolean.parseBoolean(dbEnabled);
        } catch (Exception ignored) {
        }

        return (config != null && config.getServer() != null) ? config.getServer().isEnabled() : true;
    }

    public void updateServerPort(int port) {
        DatabaseManager.getInstance().updateSetting("server_port", String.valueOf(port));
    }

    public void updateServerEnabled(boolean enabled) {
        DatabaseManager.getInstance().updateSetting("server_enabled", String.valueOf(enabled));
    }

    public String getAppMode() {
        return DatabaseManager.getInstance().getSetting("app_mode", "SERVER");
    }

    public boolean isServerMode() {
        return "SERVER".equalsIgnoreCase(getAppMode());
    }

    public void updateAppMode(String mode) {
        DatabaseManager.getInstance().updateSetting("app_mode", mode);
    }
}
