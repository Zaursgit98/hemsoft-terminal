package az.hemsoft.terminaljx.business.core.schema;

import az.hemsoft.terminaljx.business.core.annotation.*;
import java.lang.reflect.Field;
import java.sql.*;
import java.util.*;

public class SchemaGenerator {

    public static void generateTables(Connection connection, String basePackage) {
        List<Class<?>> entityClasses = scanForEntities(basePackage);
        System.out.println("\u23f3 Scanning for entities in: " + basePackage);
        for (Class<?> clazz : entityClasses) {
            generateTable(connection, clazz);
        }
        System.out.println("\u2705 Automatic schema generation completed for " + entityClasses.size() + " entities.");
    }

    private static List<Class<?>> scanForEntities(String basePackage) {
        List<Class<?>> classes = new ArrayList<>();
        try {
            String path = basePackage.replace('.', '/');
            java.util.Enumeration<java.net.URL> resources = Thread.currentThread().getContextClassLoader()
                    .getResources(path);
            while (resources.hasMoreElements()) {
                java.net.URL resource = resources.nextElement();
                java.io.File directory = new java.io.File(resource.getFile());
                if (directory.exists() && directory.isDirectory()) {
                    scanDirectory(directory, basePackage, classes);
                }
            }
        } catch (Exception e) {
            System.err.println("\u274C Entity scanning failed: " + e.getMessage());
        }
        return classes;
    }

    private static void scanDirectory(java.io.File directory, String packageName, List<Class<?>> classes) {
        java.io.File[] files = directory.listFiles();
        if (files == null)
            return;

        for (java.io.File file : files) {
            if (file.isDirectory()) {
                scanDirectory(file, packageName + "." + file.getName(), classes);
            } else if (file.getName().endsWith(".class")) {
                String className = packageName + "." + file.getName().substring(0, file.getName().length() - 6);
                try {
                    Class<?> clazz = Class.forName(className);
                    if (clazz.isAnnotationPresent(Table.class)) {
                        classes.add(clazz);
                    }
                } catch (Exception ignored) {
                }
            }
        }
    }

    public static void generateTable(Connection connection, Class<?> clazz) {
        if (!clazz.isAnnotationPresent(Table.class))
            return;

        String tableName = clazz.getAnnotation(Table.class).value();
        try {
            if (tableExists(connection, tableName)) {
                // Ensure existing table uses utf8mb4
                ensureUtf8Encoding(connection, tableName);
                updateTable(connection, clazz, tableName);
            } else {
                createNewTable(connection, clazz, tableName);
            }
        } catch (SQLException e) {
            System.err.println("\u274C Schema generation failed for " + tableName + ": " + e.getMessage());
        }
    }

    private static void ensureUtf8Encoding(Connection connection, String tableName) throws SQLException {
        String sql = "ALTER TABLE " + tableName + " CONVERT TO CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
        }
    }

    private static boolean tableExists(Connection connection, String tableName) throws SQLException {
        try (ResultSet rs = connection.getMetaData().getTables(null, null, tableName, null)) {
            return rs.next();
        }
    }

    private static void createNewTable(Connection connection, Class<?> clazz, String tableName) throws SQLException {
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        Field[] fields = clazz.getDeclaredFields();
        List<String> columnDefs = new ArrayList<>();

        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class))
                continue;

            String colName = getColumnName(field);
            StringBuilder colDef = new StringBuilder(colName + " " + getSqlType(field));

            if (field.isAnnotationPresent(Id.class))
                colDef.append(" PRIMARY KEY AUTO_INCREMENT");
            else {
                if (field.isAnnotationPresent(Column.class)) {
                    Column col = field.getAnnotation(Column.class);
                    if (!col.nullable())
                        colDef.append(" NOT NULL");
                    if (col.unique())
                        colDef.append(" UNIQUE");
                }
            }
            columnDefs.add(colDef.toString());
        }

        // Add Foreign Keys for ManyToOne
        for (Field field : fields) {
            if (field.isAnnotationPresent(ManyToOne.class) && field.isAnnotationPresent(JoinColumn.class)) {
                String fkCol = field.getAnnotation(JoinColumn.class).name();
                Class<?> target = field.getType();
                if (target.isAnnotationPresent(Table.class)) {
                    String targetTable = target.getAnnotation(Table.class).value();
                    columnDefs.add("FOREIGN KEY (" + fkCol + ") REFERENCES " + targetTable + "(id)");
                }
            }
        }

        sql.append(String.join(", ", columnDefs)).append(") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci");

        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql.toString());
            System.out.println("\u2705 Table created: " + tableName);

            // Create Indexes for new table
            for (Field field : fields) {
                if (field.isAnnotationPresent(Index.class)) {
                    addIndex(connection, tableName, getColumnName(field), field.getAnnotation(Index.class).name());
                }
            }
        }
    }

    private static void updateTable(Connection connection, Class<?> clazz, String tableName) throws SQLException {
        Set<String> existingColumns = getExistingColumns(connection, tableName);
        Set<String> uniqueColumns = getUniqueColumns(connection, tableName);
        Set<String> indexedColumns = getExistingIndexes(connection, tableName);
        Field[] fields = clazz.getDeclaredFields();

        for (Field field : fields) {
            if (field.isAnnotationPresent(OneToMany.class) || field.isAnnotationPresent(ManyToMany.class))
                continue;

            String colName = getColumnName(field);
            String sqlType = getSqlType(field);

            if (!existingColumns.contains(colName.toLowerCase())) {
                boolean isUnique = field.isAnnotationPresent(Column.class)
                        && field.getAnnotation(Column.class).unique();
                addColumn(connection, tableName, colName, sqlType, isUnique);

                // Add index if present
                if (field.isAnnotationPresent(Index.class)) {
                    addIndex(connection, tableName, colName, field.getAnnotation(Index.class).name());
                }
            } else {
                // Check for new unique constraint
                if (field.isAnnotationPresent(Column.class) && field.getAnnotation(Column.class).unique()
                        && !uniqueColumns.contains(colName.toLowerCase())) {
                    addUniqueConstraint(connection, tableName, colName);
                }

                // Check for new index
                if (field.isAnnotationPresent(Index.class) && !indexedColumns.contains(colName.toLowerCase())
                        && !uniqueColumns.contains(colName.toLowerCase())) {
                    addIndex(connection, tableName, colName, field.getAnnotation(Index.class).name());
                }
            }
        }
    }

    private static Set<String> getExistingIndexes(Connection connection, String tableName) throws SQLException {
        Set<String> indexedCols = new HashSet<>();
        try (ResultSet rs = connection.getMetaData().getIndexInfo(null, null, tableName, false, false)) {
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                if (colName != null) {
                    indexedCols.add(colName.toLowerCase());
                }
            }
        } catch (SQLException ignored) {
        }
        return indexedCols;
    }

    private static void addIndex(Connection connection, String tableName, String colName, String indexName)
            throws SQLException {
        if (indexName == null || indexName.isEmpty()) {
            indexName = "idx_" + tableName + "_" + colName;
        }
        String sql = String.format("CREATE INDEX %s ON %s (%s)", indexName, tableName, colName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("\u2699\uFE0F Created INDEX '" + indexName + "' on table '" + tableName + "'");
        }
    }

    private static Set<String> getUniqueColumns(Connection connection, String tableName) throws SQLException {
        Set<String> uniqueCols = new HashSet<>();
        try (ResultSet rs = connection.getMetaData().getIndexInfo(null, null, tableName, true, false)) {
            while (rs.next()) {
                String colName = rs.getString("COLUMN_NAME");
                if (colName != null) {
                    uniqueCols.add(colName.toLowerCase());
                }
            }
        } catch (SQLException ignored) {
            // Some drivers might not support this or table might be empty
        }
        return uniqueCols;
    }

    private static void addUniqueConstraint(Connection connection, String tableName, String colName)
            throws SQLException {
        String sql = String.format("ALTER TABLE %s ADD UNIQUE (%s)", tableName, colName);
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out
                    .println("\u2699\uFE0F Added UNIQUE constraint to '" + colName + "' in table '" + tableName + "'");
        }
    }

    private static Set<String> getExistingColumns(Connection connection, String tableName) throws SQLException {
        Set<String> columns = new HashSet<>();
        try (ResultSet rs = connection.getMetaData().getColumns(null, null, tableName, null)) {
            while (rs.next()) {
                columns.add(rs.getString("COLUMN_NAME").toLowerCase());
            }
        }
        return columns;
    }

    private static void addColumn(Connection connection, String tableName, String colName, String sqlType,
            boolean isUnique)
            throws SQLException {
        String sql = String.format("ALTER TABLE %s ADD COLUMN %s %s", tableName, colName, sqlType);
        if (isUnique)
            sql += " UNIQUE";
        try (Statement stmt = connection.createStatement()) {
            stmt.execute(sql);
            System.out.println("\u2699\uFE0F Added missing column '" + colName + "' to table '" + tableName + "'");
        }
    }

    private static String getColumnName(Field field) {
        if (field.isAnnotationPresent(JoinColumn.class))
            return field.getAnnotation(JoinColumn.class).name();
        if (field.isAnnotationPresent(Column.class) && !field.getAnnotation(Column.class).name().isEmpty())
            return field.getAnnotation(Column.class).name();
        return field.getName();
    }

    private static String getSqlType(Field field) {
        Class<?> type = field.getType();
        if (field.isAnnotationPresent(ManyToOne.class))
            return "INT";
        if (type == String.class) {
            int length = field.isAnnotationPresent(Column.class) ? field.getAnnotation(Column.class).length() : 255;
            return "VARCHAR(" + length + ")";
        }
        if (type == Integer.class || type == int.class)
            return "INT";
        if (type == Long.class || type == long.class)
            return "BIGINT";
        if (type == Double.class || type == double.class)
            return "DOUBLE";
        if (type == Boolean.class || type == boolean.class)
            return "BOOLEAN";
        return "TEXT";
    }
}
