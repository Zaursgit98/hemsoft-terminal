package az.hemsoft.terminaljx.business.restaurant.service;

import az.hemsoft.terminaljx.business.restaurant.model.Transaction;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class ReportService {
    private static ReportService instance;
    private final DatabaseManager dbManager;

    private ReportService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized ReportService getInstance() {
        if (instance == null) {
            instance = new ReportService();
        }
        return instance;
    }

    // Example Report: Get Total Sales for a date range
    public double getTotalSales(long startDate, long endDate) {
        double total = 0;
        String sql = "SELECT SUM(final_amount) FROM transactions WHERE type = 'SALE' AND date BETWEEN ? AND ?";
        try (Connection conn = dbManager.getConnection();
                PreparedStatement pstmt = conn.prepareStatement(sql)) {
            pstmt.setLong(1, startDate);
            pstmt.setLong(2, endDate);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                total = rs.getDouble(1);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return total;
    }

    // Example Query: Get transactions for a customer
    public List<Transaction> getCustomerHistory(String customerId) {
        List<Transaction> list = new ArrayList<>();
        String sql = "SELECT * FROM transactions WHERE entity_id = ? ORDER BY date DESC";
        // Logic to populate list... (Simplified for this snippet)
        return list;
    }
}

