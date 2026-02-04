package az.hemsoft.terminaljx.business.config;

import lombok.Data;

@Data
public class AppConfig {
    private DatabaseConfig database;
    private ServerConfig server;

    @Data
    public static class ServerConfig {
        private boolean enabled;
        private int port;
    }

    @Data
    public static class DatabaseConfig {
        private MariaDbConfig mariadb;
        private SqliteConfig sqlite;
    }

    @Data
    public static class MariaDbConfig {
        private String host;
        private int port;
        private String name;
        private String user;
        private String password;
    }

    @Data
    public static class SqliteConfig {
        private String name;
    }

}
