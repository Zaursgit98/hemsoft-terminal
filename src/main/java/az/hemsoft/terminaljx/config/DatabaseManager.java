package az.hemsoft.terminaljx.config;

import az.hemsoft.terminaljx.business.core.schema.SchemaGenerator;
import java.sql.*;

/**
 * Hybrid Database Manager
 * SQLite: Local settings
 * MariaDB Server: Business data (Warehouses, etc.)
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection sqliteConnection;
    private Connection mariadbConnection;

    private static final String DEFAULT_SQLITE_DB_NAME = "terminal_local.db";

    private DatabaseManager() {
        initializeDatabases();
    }

    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    private void initializeDatabases() {
        try {
            AppConfig config = ConfigService.getInstance().getConfig();

            // 1. Initialize SQLite
            String sqliteName = (config != null && config.getDatabase() != null
                    && config.getDatabase().getSqlite() != null)
                            ? config.getDatabase().getSqlite().getName()
                            : DEFAULT_SQLITE_DB_NAME;

            Class.forName("org.sqlite.JDBC");
            sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + sqliteName);
            System.out.println("✅ SQLite Connected: " + sqliteName);
            createSQLiteTables();

            // 2. Initialize MariaDB
            if (config != null && config.getDatabase() != null && config.getDatabase().getMariadb() != null) {
                var mariaConfig = config.getDatabase().getMariadb();
                Class.forName("org.mariadb.jdbc.Driver");

                String mariaUrl = "jdbc:mariadb://" + mariaConfig.getHost() + ":" + mariaConfig.getPort() + "/"
                        + mariaConfig.getName()
                        + "?createDatabaseIfNotExist=true&characterEncoding=UTF-8&useUnicode=true";

                mariadbConnection = DriverManager.getConnection(mariaUrl, mariaConfig.getUser(),
                        mariaConfig.getPassword());
                System.out.println("✅ MariaDB Connected: " + mariaUrl);
                createMariaDBTables();
            }

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (sqliteConnection != null && !sqliteConnection.isClosed())
                        sqliteConnection.close();
                    if (mariadbConnection != null && !mariadbConnection.isClosed())
                        mariadbConnection.close();
                    System.out.println("Databases closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

        } catch (Exception e) {
            System.err.println("❌ Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSQLiteTables() throws SQLException {
        try (Statement stmt = sqliteConnection.createStatement()) {
            // Settings table for dynamic config
            stmt.execute("CREATE TABLE IF NOT EXISTS settings (" +
                    "setting_key TEXT PRIMARY KEY, " +
                    "setting_value TEXT)");

            // Default settings
            stmt.execute("INSERT OR IGNORE INTO settings (setting_key, setting_value) VALUES ('server_port', '8080')");
            stmt.execute(
                    "INSERT OR IGNORE INTO settings (setting_key, setting_value) VALUES ('server_enabled', 'true')");
            stmt.execute("INSERT OR IGNORE INTO settings (setting_key, setting_value) VALUES ('app_mode', 'SERVER')");

            System.out.println("✅ SQLite tables verified");
        }
    }

    private void createMariaDBTables() throws SQLException {
        // Automatic Schema Generation using Mini-ORM
        SchemaGenerator.generateTables(mariadbConnection, "az.hemsoft.terminaljx.business");
    }

    public String getSetting(String key, String defaultValue) {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        try (PreparedStatement pstmt = sqliteConnection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next())
                    return rs.getString("setting_value");
            }
        } catch (Exception e) {
            System.err.println("❌ Error fetching setting: " + key);
        }
        return defaultValue;
    }

    public void updateSetting(String key, String value) {
        String sql = "INSERT OR REPLACE INTO settings (setting_key, setting_value) VALUES (?, ?)";
        try (PreparedStatement pstmt = sqliteConnection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            pstmt.setString(2, value);
            pstmt.executeUpdate();
        } catch (Exception e) {
            System.err.println("❌ Error updating setting: " + key);
        }
    }

    public Connection getConnection() {
        return mariadbConnection;
    }
}
