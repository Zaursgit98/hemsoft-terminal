package az.hemsoft.terminaljx.business.restaurant.service;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Hybrid Database Manager
 * SQLite: Local assets (Logos, Settings)
 * MariaDB Server: Transactional data (Printers, Menus, etc.)
 */
public class DatabaseManager {
    private static DatabaseManager instance;
    private Connection sqliteConnection;
    private Connection mariadbConnection;

    // SQLite config
    private static final String DEFAULT_SQLITE_DB_NAME = "terminal_local.db";

    // Tables
    private static final String TABLE_LOGOS = "logos";
    private static final String TABLE_PRINTERS = "printers";
    private static final String TABLE_MENU_MEASURES = "menu_measures";
    private static final String TABLE_MENU_GROUPS = "menu_groups";
    private static final String TABLE_MENUS = "menus";
    private static final String TABLE_AREAS = "areas";
    private static final String TABLE_SUPPLIERS = "suppliers";
    private static final String TABLE_CUSTOMERS = "customers";
    private static final String TABLE_PRODUCTS = "products";
    private static final String TABLE_TRANSACTIONS = "transactions";
    private static final String TABLE_TRANSACTION_DETAILS = "transaction_details";

    private com.google.gson.Gson gson;

    private DatabaseManager() {
        gson = new com.google.gson.Gson();
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
            az.hemsoft.terminaljx.business.config.AppConfig config = ConfigService.getInstance().getConfig();

            // 1. Initialize SQLite
            String sqliteName = (config != null && config.getDatabase() != null
                    && config.getDatabase().getSqlite() != null)
                            ? config.getDatabase().getSqlite().getName()
                            : DEFAULT_SQLITE_DB_NAME;

            Class.forName("org.sqlite.JDBC");
            sqliteConnection = DriverManager.getConnection("jdbc:sqlite:" + sqliteName);
            System.out.println("\u2705 SQLite Connected: " + sqliteName);
            createSQLiteTables();

            // 2. Initialize MariaDB
            if (config != null && config.getDatabase() != null && config.getDatabase().getMariadb() != null) {
                var mariaConfig = config.getDatabase().getMariadb();
                Class.forName("org.mariadb.jdbc.Driver");

                String mariaUrl = "jdbc:mariadb://" + mariaConfig.getHost() + ":" + mariaConfig.getPort() + "/"
                        + mariaConfig.getName()
                        + "?createDatabaseIfNotExist=true";

                mariadbConnection = DriverManager.getConnection(mariaUrl, mariaConfig.getUser(),
                        mariaConfig.getPassword());
                System.out.println("\u2705 MariaDB Connected: " + mariaUrl);
                createMariaDBTables();
            } else {
                System.err.println("\u274C MariaDB Config Missing!");
            }

            // Register shutdown hook
            Runtime.getRuntime().addShutdownHook(new Thread(() -> {
                try {
                    if (sqliteConnection != null && !sqliteConnection.isClosed())
                        sqliteConnection.close();
                    if (mariadbConnection != null && !mariadbConnection.isClosed())
                        mariadbConnection.close();
                    System.out.println("\uD83D\uDED1 Databases closed");
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }));

        } catch (Exception e) {
            System.err.println("\u274C Database initialization error: " + e.getMessage());
            e.printStackTrace();
        }
    }

    private void createSQLiteTables() throws SQLException {
        try (Statement stmt = sqliteConnection.createStatement()) {
            String createLogosTable = "CREATE TABLE IF NOT EXISTS " + TABLE_LOGOS + " (" +
                    "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    "logo_name TEXT UNIQUE NOT NULL, " +
                    "logo_data BLOB NOT NULL, " +
                    "created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP" +
                    ")";
            stmt.execute(createLogosTable);

            // Settings table for dynamic config
            stmt.execute("CREATE TABLE IF NOT EXISTS settings (" +
                    "setting_key TEXT PRIMARY KEY, " +
                    "setting_value TEXT)");

            // Default settings if not exists
            stmt.execute("INSERT OR IGNORE INTO settings (setting_key, setting_value) VALUES ('server_port', '8080')");
            stmt.execute(
                    "INSERT OR IGNORE INTO settings (setting_key, setting_value) VALUES ('server_enabled', 'true')");
            stmt.execute("INSERT OR IGNORE INTO settings (setting_key, setting_value) VALUES ('app_mode', 'SERVER')");

            System.out.println("✅ SQLite tables verified");
        } catch (Exception e) {
            System.err.println("❌ SQLite table creation error: " + e.getMessage());
        }
    }

    public String getSetting(String key, String defaultValue) {
        String sql = "SELECT setting_value FROM settings WHERE setting_key = ?";
        try (PreparedStatement pstmt = sqliteConnection.prepareStatement(sql)) {
            pstmt.setString(1, key);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getString("setting_value");
                }
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

    private void createMariaDBTables() throws SQLException {
        try (Statement stmt = mariadbConnection.createStatement()) {
            // Printers table
            String createPrintersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PRINTERS + " (" +
                    "printer_id VARCHAR(255) PRIMARY KEY, " +
                    "printer_name VARCHAR(255), " +
                    "printer_code VARCHAR(255), " +
                    "document VARCHAR(255), " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createPrintersTable);

            // Menu Measures table
            String createMenuMeasuresTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MENU_MEASURES + " (" +
                    "id VARCHAR(255) PRIMARY KEY, " +
                    "measure_name VARCHAR(255), " +
                    "price DOUBLE, " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createMenuMeasuresTable);

            // Menu Groups table
            String createMenuGroupsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MENU_GROUPS + " (" +
                    "mg_id VARCHAR(255) PRIMARY KEY, " +
                    "mg_name VARCHAR(255), " +
                    "document VARCHAR(255), " +
                    "image_id VARCHAR(255), " +
                    "image_url LONGTEXT, " +
                    "is_selected TINYINT(1), " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createMenuGroupsTable);

            // Menus table
            String createMenusTable = "CREATE TABLE IF NOT EXISTS " + TABLE_MENUS + " (" +
                    "menu_id VARCHAR(255) PRIMARY KEY, " +
                    "menu_name VARCHAR(255), " +
                    "document VARCHAR(255), " +
                    "image_id VARCHAR(255), " +
                    "image_url LONGTEXT, " +
                    "created_date BIGINT, " +
                    "updated_date BIGINT, " +
                    "group_id VARCHAR(255), " +
                    "printer_id VARCHAR(255), " +
                    "price_list_json LONGTEXT, " +
                    "menu_measure_price_json LONGTEXT, " +
                    "set_menus_json LONGTEXT, " +
                    "delivery_company_prices_json LONGTEXT, " +
                    "standard_price DOUBLE, " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createMenusTable);

            // Areas table
            String createAreasTable = "CREATE TABLE IF NOT EXISTS " + TABLE_AREAS + " (" +
                    "area_id VARCHAR(255) PRIMARY KEY, " +
                    "area_name VARCHAR(255), " +
                    "document VARCHAR(255), " +
                    "branch_json LONGTEXT, " +
                    "is_selected TINYINT(1)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createAreasTable);

            System.out.println("Ã¢Å“â€¦ MariaDB tables verified");

            // Suppliers table
            String createSuppliersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_SUPPLIERS + " (" +
                    "supplier_id VARCHAR(255) PRIMARY KEY, " +
                    "supplier_name VARCHAR(255), " +
                    "contact_info VARCHAR(255), " +
                    "balance DOUBLE, " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createSuppliersTable);

            // Customers table
            String createCustomersTable = "CREATE TABLE IF NOT EXISTS " + TABLE_CUSTOMERS + " (" +
                    "customer_id VARCHAR(255) PRIMARY KEY, " +
                    "full_name VARCHAR(255), " +
                    "phone_number VARCHAR(255), " +
                    "card_number VARCHAR(255), " +
                    "bonus_balance DOUBLE, " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createCustomersTable);

            // Products table
            String createProductsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_PRODUCTS + " (" +
                    "product_id VARCHAR(255) PRIMARY KEY, " +
                    "product_name VARCHAR(255), " +
                    "barcode VARCHAR(255), " +
                    "group_id VARCHAR(255), " +
                    "purchase_price DOUBLE, " +
                    "sale_price DOUBLE, " +
                    "image_path VARCHAR(255), " +
                    "stock_quantity DOUBLE, " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createProductsTable);

            // Transactions table
            String createTransactionsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTIONS + " (" +
                    "transaction_id VARCHAR(255) PRIMARY KEY, " +
                    "type VARCHAR(50), " +
                    "date BIGINT, " +
                    "entity_id VARCHAR(255), " +
                    "total_amount DOUBLE, " +
                    "discount DOUBLE, " +
                    "final_amount DOUBLE, " +
                    "company_mail VARCHAR(255)" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createTransactionsTable);

            // Transaction Details table
            String createTransactionDetailsTable = "CREATE TABLE IF NOT EXISTS " + TABLE_TRANSACTION_DETAILS + " (" +
                    "detail_id VARCHAR(255) PRIMARY KEY, " +
                    "transaction_id VARCHAR(255), " +
                    "product_id VARCHAR(255), " +
                    "quantity DOUBLE, " +
                    "price DOUBLE, " +
                    "total DOUBLE, " +
                    "FOREIGN KEY (transaction_id) REFERENCES " + TABLE_TRANSACTIONS
                    + "(transaction_id) ON DELETE CASCADE" +
                    ") CHARACTER SET utf8mb4 COLLATE utf8mb4_unicode_ci";
            stmt.execute(createTransactionDetailsTable);
        }
    }

    // --- Logo Methods (SQLite) ---

    public void saveLogo(String logoName, byte[] logoData) {
        try {
            String sql = "INSERT OR REPLACE INTO " + TABLE_LOGOS + " (logo_name, logo_data) VALUES (?, ?)";
            try (PreparedStatement pstmt = sqliteConnection.prepareStatement(sql)) {
                pstmt.setString(1, logoName);
                pstmt.setBytes(2, logoData);
                pstmt.executeUpdate();
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public byte[] getLogo(String logoName) {
        try {
            String sql = "SELECT logo_data FROM " + TABLE_LOGOS + " WHERE logo_name = ?";
            try (PreparedStatement pstmt = sqliteConnection.prepareStatement(sql)) {
                pstmt.setString(1, logoName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next())
                    return rs.getBytes("logo_data");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    public boolean logoExists(String logoName) {
        try {
            String sql = "SELECT COUNT(*) FROM " + TABLE_LOGOS + " WHERE logo_name = ?";
            try (PreparedStatement pstmt = sqliteConnection.prepareStatement(sql)) {
                pstmt.setString(1, logoName);
                ResultSet rs = pstmt.executeQuery();
                if (rs.next())
                    return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return false;
    }

    // --- Printer Methods (MariaDB) ---

    public void savePrinter(az.hemsoft.terminaljx.business.restaurant.model.Printer printer) {
        String sql = "INSERT INTO " + TABLE_PRINTERS +
                " (printer_id, printer_name, printer_code, document, company_mail) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE printer_name=VALUES(printer_name), printer_code=VALUES(printer_code), document=VALUES(document), company_mail=VALUES(company_mail)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, printer.getPrinterId());
            pstmt.setString(2, printer.getPrinterName());
            pstmt.setString(3, printer.getPrinterCode());
            pstmt.setString(4, printer.getDocument());
            pstmt.setString(5, printer.getCompanyMail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<az.hemsoft.terminaljx.business.restaurant.model.Printer> getAllPrinters() {
        List<az.hemsoft.terminaljx.business.restaurant.model.Printer> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_PRINTERS;
        try (Statement stmt = mariadbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                az.hemsoft.terminaljx.business.restaurant.model.Printer p = new az.hemsoft.terminaljx.business.restaurant.model.Printer();
                p.setPrinterId(rs.getString("printer_id"));
                p.setPrinterName(rs.getString("printer_name"));
                p.setPrinterCode(rs.getString("printer_code"));
                p.setDocument(rs.getString("document"));
                p.setCompanyMail(rs.getString("company_mail"));
                list.add(p);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- MenuMeasure Methods (MariaDB) ---

    public void saveMenuMeasure(az.hemsoft.terminaljx.business.restaurant.model.MenuMeasure mm) {
        String sql = "INSERT INTO " + TABLE_MENU_MEASURES +
                " (id, measure_name, price, company_mail) VALUES (?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE measure_name=VALUES(measure_name), price=VALUES(price), company_mail=VALUES(company_mail)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, mm.getId());
            pstmt.setString(2, mm.getMeasureName());
            pstmt.setDouble(3, mm.getPrice());
            pstmt.setString(4, mm.getCompanyMail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<az.hemsoft.terminaljx.business.restaurant.model.MenuMeasure> getAllMenuMeasures() {
        List<az.hemsoft.terminaljx.business.restaurant.model.MenuMeasure> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_MENU_MEASURES;
        try (Statement stmt = mariadbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                az.hemsoft.terminaljx.business.restaurant.model.MenuMeasure mm = new az.hemsoft.terminaljx.business.restaurant.model.MenuMeasure();
                mm.setId(rs.getString("id"));
                mm.setMeasureName(rs.getString("measure_name"));
                mm.setPrice(rs.getDouble("price"));
                mm.setCompanyMail(rs.getString("company_mail"));
                list.add(mm);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- MenuGroup Methods (MariaDB) ---

    public void saveMenuGroup(az.hemsoft.terminaljx.business.restaurant.model.MenuGroup mg) {
        String sql = "INSERT INTO " + TABLE_MENU_GROUPS +
                " (mg_id, mg_name, document, image_id, image_url, is_selected, company_mail) VALUES (?, ?, ?, ?, ?, ?, ?) "
                +
                "ON DUPLICATE KEY UPDATE mg_name=VALUES(mg_name), document=VALUES(document), image_id=VALUES(image_id), image_url=VALUES(image_url), is_selected=VALUES(is_selected), company_mail=VALUES(company_mail)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, mg.getMgId());
            pstmt.setString(2, mg.getMgName());
            pstmt.setString(3, mg.getDocument());
            pstmt.setString(4, mg.getImageId());
            pstmt.setString(5, mg.getImageUrl());
            pstmt.setInt(6, (mg.getIsSelected() != null && mg.getIsSelected()) ? 1 : 0);
            pstmt.setString(7, mg.getCompanyMail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<az.hemsoft.terminaljx.business.restaurant.model.MenuGroup> getAllMenuGroups() {
        List<az.hemsoft.terminaljx.business.restaurant.model.MenuGroup> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_MENU_GROUPS;
        try (Statement stmt = mariadbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                az.hemsoft.terminaljx.business.restaurant.model.MenuGroup mg = new az.hemsoft.terminaljx.business.restaurant.model.MenuGroup();
                mg.setMgId(rs.getString("mg_id"));
                mg.setMgName(rs.getString("mg_name"));
                mg.setDocument(rs.getString("document"));
                mg.setImageId(rs.getString("image_id"));
                mg.setImageUrl(rs.getString("image_url"));
                mg.setIsSelected(rs.getInt("is_selected") == 1);
                mg.setCompanyMail(rs.getString("company_mail"));
                list.add(mg);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Menu Methods (MariaDB) ---

    public void saveMenu(az.hemsoft.terminaljx.business.restaurant.model.Menu menu) {
        String sql = "INSERT INTO " + TABLE_MENUS +
                " (menu_id, menu_name, document, image_id, image_url, created_date, updated_date, group_id, printer_id, "
                + "price_list_json, menu_measure_price_json, set_menus_json, delivery_company_prices_json, standard_price, company_mail) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?, ?) "
                + "ON DUPLICATE KEY UPDATE menu_name=VALUES(menu_name), document=VALUES(document), image_id=VALUES(image_id), image_url=VALUES(image_url), "
                + "created_date=VALUES(created_date), updated_date=VALUES(updated_date), group_id=VALUES(group_id), printer_id=VALUES(printer_id), "
                + "price_list_json=VALUES(price_list_json), menu_measure_price_json=VALUES(menu_measure_price_json), set_menus_json=VALUES(set_menus_json), "
                + "delivery_company_prices_json=VALUES(delivery_company_prices_json), standard_price=VALUES(standard_price), company_mail=VALUES(company_mail)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, menu.getMenuId());
            pstmt.setString(2, menu.getMenuName());
            pstmt.setString(3, menu.getDocument());
            pstmt.setString(4, menu.getImageId());
            pstmt.setString(5, menu.getImageUrl());
            pstmt.setLong(6, menu.getCreatedDate() != null ? menu.getCreatedDate().getTime() : 0);
            pstmt.setLong(7, menu.getUpdatedDate() != null ? menu.getUpdatedDate().getTime() : 0);

            pstmt.setString(8, menu.getGroup() != null ? menu.getGroup().getMgId() : null);
            pstmt.setString(9, menu.getPrinter() != null ? menu.getPrinter().getPrinterId() : null);

            pstmt.setString(10, menu.getPriceList() != null ? gson.toJson(menu.getPriceList()) : null);
            pstmt.setString(11, menu.getMenuMeasurePrice() != null ? gson.toJson(menu.getMenuMeasurePrice()) : null);
            pstmt.setString(12, menu.getSetMenus() != null ? gson.toJson(menu.getSetMenus()) : null);
            pstmt.setString(13,
                    menu.getDeliveryCompanyPrices() != null ? gson.toJson(menu.getDeliveryCompanyPrices()) : null);
            pstmt.setDouble(14, menu.getStandardPrice());
            pstmt.setString(15, menu.getCompanyMail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<az.hemsoft.terminaljx.business.restaurant.model.Menu> getAllMenus() {
        List<az.hemsoft.terminaljx.business.restaurant.model.Menu> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_MENUS;
        try (Statement stmt = mariadbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {

            java.lang.reflect.Type priceListType = new com.google.gson.reflect.TypeToken<List<az.hemsoft.terminaljx.business.restaurant.model.MenuAreaPricesDto>>() {
            }.getType();
            java.lang.reflect.Type measureListType = new com.google.gson.reflect.TypeToken<List<az.hemsoft.terminaljx.business.restaurant.model.MenuMeasure>>() {
            }.getType();
            java.lang.reflect.Type setMenusType = new com.google.gson.reflect.TypeToken<List<az.hemsoft.terminaljx.business.restaurant.model.SetMenus>>() {
            }.getType();
            java.lang.reflect.Type deliveryType = new com.google.gson.reflect.TypeToken<List<az.hemsoft.terminaljx.business.restaurant.model.MenuDeliveryPriceDto>>() {
            }.getType();

            while (rs.next()) {
                az.hemsoft.terminaljx.business.restaurant.model.Menu m = new az.hemsoft.terminaljx.business.restaurant.model.Menu();
                m.setMenuId(rs.getString("menu_id"));
                m.setMenuName(rs.getString("menu_name"));
                m.setDocument(rs.getString("document"));
                m.setImageId(rs.getString("image_id"));
                m.setImageUrl(rs.getString("image_url"));

                long cTime = rs.getLong("created_date");
                if (cTime > 0)
                    m.setCreatedDate(new java.util.Date(cTime));
                long uTime = rs.getLong("updated_date");
                if (uTime > 0)
                    m.setUpdatedDate(new java.util.Date(uTime));

                String groupId = rs.getString("group_id");
                if (groupId != null) {
                    az.hemsoft.terminaljx.business.restaurant.model.MenuGroup mg = new az.hemsoft.terminaljx.business.restaurant.model.MenuGroup();
                    mg.setMgId(groupId);
                    m.setGroup(mg);
                }

                String printerId = rs.getString("printer_id");
                if (printerId != null) {
                    az.hemsoft.terminaljx.business.restaurant.model.Printer p = new az.hemsoft.terminaljx.business.restaurant.model.Printer();
                    p.setPrinterId(printerId);
                    m.setPrinter(p);
                }

                m.setPriceList(gson.fromJson(rs.getString("price_list_json"), priceListType));
                m.setMenuMeasurePrice(gson.fromJson(rs.getString("menu_measure_price_json"), measureListType));
                m.setSetMenus(gson.fromJson(rs.getString("set_menus_json"), setMenusType));
                m.setDeliveryCompanyPrices(gson.fromJson(rs.getString("delivery_company_prices_json"), deliveryType));

                m.setStandardPrice(rs.getDouble("standard_price"));
                m.setCompanyMail(rs.getString("company_mail"));
                list.add(m);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    // --- Area Methods (MariaDB) ---

    public void saveArea(az.hemsoft.terminaljx.business.restaurant.model.Area area) {
        String sql = "INSERT INTO " + TABLE_AREAS +
                " (area_id, area_name, document, branch_json, is_selected) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE area_name=VALUES(area_name), document=VALUES(document), branch_json=VALUES(branch_json), is_selected=VALUES(is_selected)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, area.getAreaId());
            pstmt.setString(2, area.getAreaName());
            pstmt.setString(3, area.getDocument());
            pstmt.setString(4, area.getBranch() != null ? gson.toJson(area.getBranch()) : null);
            pstmt.setInt(5, area.isSelected() ? 1 : 0);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<az.hemsoft.terminaljx.business.restaurant.model.Area> getAllAreas() {
        List<az.hemsoft.terminaljx.business.restaurant.model.Area> list = new ArrayList<>();
        String sql = "SELECT * FROM " + TABLE_AREAS;
        try (Statement stmt = mariadbConnection.createStatement();
                ResultSet rs = stmt.executeQuery(sql)) {
            while (rs.next()) {
                az.hemsoft.terminaljx.business.restaurant.model.Area area = new az.hemsoft.terminaljx.business.restaurant.model.Area();
                area.setAreaId(rs.getString("area_id"));
                area.setAreaName(rs.getString("area_name"));
                area.setDocument(rs.getString("document"));

                String branchJson = rs.getString("branch_json");
                if (branchJson != null) {
                    area.setBranch(gson.fromJson(branchJson, az.hemsoft.terminaljx.business.restaurant.model.Branch.class));
                }

                area.setSelected(rs.getInt("is_selected") == 1);
                list.add(area);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }

    public Connection getConnection() {
        return mariadbConnection;
    }

    // --- Supplier Methods ---
    public void saveSupplier(az.hemsoft.terminaljx.business.restaurant.model.Supplier supplier) {
        String sql = "INSERT INTO " + TABLE_SUPPLIERS
                + " (supplier_id, supplier_name, contact_info, balance, company_mail) VALUES (?, ?, ?, ?, ?) " +
                "ON DUPLICATE KEY UPDATE supplier_name=VALUES(supplier_name), contact_info=VALUES(contact_info), balance=VALUES(balance), company_mail=VALUES(company_mail)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, supplier.getSupplierId());
            pstmt.setString(2, supplier.getSupplierName());
            pstmt.setString(3, supplier.getContactInfo());
            pstmt.setDouble(4, supplier.getBalance());
            pstmt.setString(5, supplier.getCompanyMail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Customer Methods ---
    public void saveCustomer(az.hemsoft.terminaljx.business.restaurant.model.Customer customer) {
        String sql = "INSERT INTO " + TABLE_CUSTOMERS
                + " (customer_id, full_name, phone_number, card_number, bonus_balance, company_mail) VALUES (?, ?, ?, ?, ?, ?) "
                +
                "ON DUPLICATE KEY UPDATE full_name=VALUES(full_name), phone_number=VALUES(phone_number), card_number=VALUES(card_number), bonus_balance=VALUES(bonus_balance), company_mail=VALUES(company_mail)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, customer.getCustomerId());
            pstmt.setString(2, customer.getFullName());
            pstmt.setString(3, customer.getPhoneNumber());
            pstmt.setString(4, customer.getCardNumber());
            pstmt.setDouble(5, customer.getBonusBalance());
            pstmt.setString(6, customer.getCompanyMail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // --- Product Methods ---
    public void saveProduct(az.hemsoft.terminaljx.business.restaurant.model.Product product) {
        String sql = "INSERT INTO " + TABLE_PRODUCTS
                + " (product_id, product_name, barcode, group_id, purchase_price, sale_price, image_path, stock_quantity, company_mail) VALUES (?, ?, ?, ?, ?, ?, ?, ?, ?) "
                +
                "ON DUPLICATE KEY UPDATE product_name=VALUES(product_name), barcode=VALUES(barcode), group_id=VALUES(group_id), purchase_price=VALUES(purchase_price), sale_price=VALUES(sale_price), image_path=VALUES(image_path), stock_quantity=VALUES(stock_quantity), company_mail=VALUES(company_mail)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, product.getProductId());
            pstmt.setString(2, product.getProductName());
            pstmt.setString(3, product.getBarcode());
            pstmt.setString(4, product.getGroupId());
            pstmt.setDouble(5, product.getPurchasePrice());
            pstmt.setDouble(6, product.getSalePrice());
            pstmt.setString(7, product.getImagePath());
            pstmt.setDouble(8, product.getStockQuantity());
            pstmt.setString(9, product.getCompanyMail());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public az.hemsoft.terminaljx.business.restaurant.model.Product getProduct(String productId) {
        String sql = "SELECT * FROM " + TABLE_PRODUCTS + " WHERE product_id = ?";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, productId);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                az.hemsoft.terminaljx.business.restaurant.model.Product p = new az.hemsoft.terminaljx.business.restaurant.model.Product();
                p.setProductId(rs.getString("product_id"));
                p.setProductName(rs.getString("product_name"));
                p.setBarcode(rs.getString("barcode"));
                p.setGroupId(rs.getString("group_id"));
                p.setPurchasePrice(rs.getDouble("purchase_price"));
                p.setSalePrice(rs.getDouble("sale_price"));
                p.setImagePath(rs.getString("image_path"));
                p.setStockQuantity(rs.getDouble("stock_quantity"));
                p.setCompanyMail(rs.getString("company_mail"));
                return p;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null;
    }

    // --- Transaction Methods ---
    public void saveTransaction(az.hemsoft.terminaljx.business.restaurant.model.Transaction transaction) {
        String sql = "INSERT INTO " + TABLE_TRANSACTIONS
                + " (transaction_id, type, date, entity_id, total_amount, discount, final_amount, company_mail) VALUES (?, ?, ?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, transaction.getTransactionId());
            pstmt.setString(2, transaction.getType().name());
            pstmt.setLong(3, transaction.getDate().getTime());
            pstmt.setString(4, transaction.getEntityId());
            pstmt.setDouble(5, transaction.getTotalAmount());
            pstmt.setDouble(6, transaction.getDiscount());
            pstmt.setDouble(7, transaction.getFinalAmount());
            pstmt.setString(8, transaction.getCompanyMail());
            pstmt.executeUpdate();

            // Save details if any
            if (transaction.getDetails() != null) {
                for (az.hemsoft.terminaljx.business.restaurant.model.TransactionDetail detail : transaction.getDetails()) {
                    saveTransactionDetail(detail);
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void saveTransactionDetail(az.hemsoft.terminaljx.business.restaurant.model.TransactionDetail detail) {
        String sql = "INSERT INTO " + TABLE_TRANSACTION_DETAILS
                + " (detail_id, transaction_id, product_id, quantity, price, total) VALUES (?, ?, ?, ?, ?, ?)";
        try (PreparedStatement pstmt = mariadbConnection.prepareStatement(sql)) {
            pstmt.setString(1, detail.getDetailId());
            pstmt.setString(2, detail.getTransactionId());
            pstmt.setString(3, detail.getProductId());
            pstmt.setDouble(4, detail.getQuantity());
            pstmt.setDouble(5, detail.getPrice());
            pstmt.setDouble(6, detail.getTotal());
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
