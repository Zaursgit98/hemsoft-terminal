package az.hemsoft.terminaljx.config;

import lombok.Data;

@Data
public class AppConfig {
    private DatabaseConfig database = new DatabaseConfig();
    private ServerConfig server = new ServerConfig();

    @Data
    public static class ServerConfig {
        private boolean enabled = true;
        private int port = 8080;
    }

    @Data
    public static class DatabaseConfig {
        private MariaDbConfig mariadb;
        private SqliteConfig sqlite = new SqliteConfig();
    }

    @Data
    public static class MariaDbConfig {
        private String host = "localhost";
        private int port = 3306;
        private String name = "terminal_data";
        private String user = "root";
        private String password = "";
    }

    @Data
    public static class SqliteConfig {
        private String name = "terminal_local.db";
    }
}
