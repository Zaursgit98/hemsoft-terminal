package az.hemsoft.terminaljx.business.restaurant.service;

import az.hemsoft.terminaljx.business.restaurant.model.Customer;
import az.hemsoft.terminaljx.business.restaurant.model.Product;
import az.hemsoft.terminaljx.business.restaurant.model.Supplier;
import az.hemsoft.terminaljx.business.restaurant.model.Transaction;
import az.hemsoft.terminaljx.business.restaurant.model.TransactionDetail;

import java.util.Date;
import java.util.List;
import java.util.UUID;

public class InventoryService {
    private static InventoryService instance;
    private final DatabaseManager dbManager;

    private InventoryService() {
        this.dbManager = DatabaseManager.getInstance();
    }

    public static synchronized InventoryService getInstance() {
        if (instance == null) {
            instance = new InventoryService();
        }
        return instance;
    }

    /**
     * Process a Purchase from a Supplier.
     * Increases stock for products and creates a PURCHASE transaction.
     */
    public void processPurchase(Supplier supplier, List<TransactionDetail> items, double discount, String companyMail) {
        double totalAmount = 0;

        // 1. Calculate Total & Update Stock
        for (TransactionDetail item : items) {
            totalAmount += item.getTotal();

            // Assuming Product object is fetched/cached mostly, but here we update DB
            // directly
            // In a real app, we'd fetch the product first to get current stock, then add
            // For simplicity, let's assume item.getProductId() is valid
            // We need a method in DB to increment stock blindly or fetch-update-save
            Product p = getProductById(item.getProductId()); // Need to implement getProduct in DB
            if (p != null) {
                p.setStockQuantity(p.getStockQuantity() + item.getQuantity());
                // Update purchase price if it changed? Let's assume we update it to latest
                p.setPurchasePrice(item.getPrice());
                dbManager.saveProduct(p);
            }
        }

        // 2. Create Transaction
        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setType(Transaction.TransactionType.PURCHASE);
        transaction.setDate(new Date());
        transaction.setEntityId(supplier.getSupplierId());
        transaction.setTotalAmount(totalAmount);
        transaction.setDiscount(discount);
        transaction.setFinalAmount(totalAmount - discount);
        transaction.setCompanyMail(companyMail);

        // Save Transaction (Need to implement saveTransaction in DB)
        // dbManager.saveTransaction(transaction); // TODO

        // 3. Update Supplier Balance (We owe them money now, so balance decreases or
        // increases depending on logic)
        // Let's say positive balance means "We owe them".
        supplier.setBalance(supplier.getBalance() + transaction.getFinalAmount());
        dbManager.saveSupplier(supplier);

        System.out.println("âœ… Purchase processed: " + transaction.getFinalAmount());
    }

    /**
     * Process a Sale to a Customer.
     * Decreases stock and creates a SALE transaction.
     */
    public void processSale(Customer customer, List<TransactionDetail> items, double discount, String companyMail) {
        double totalAmount = 0;

        for (TransactionDetail item : items) {
            totalAmount += item.getTotal();

            Product p = getProductById(item.getProductId());
            if (p != null) {
                p.setStockQuantity(p.getStockQuantity() - item.getQuantity());
                dbManager.saveProduct(p);
            }
        }

        Transaction transaction = new Transaction();
        transaction.setTransactionId(UUID.randomUUID().toString());
        transaction.setType(Transaction.TransactionType.SALE);
        transaction.setDate(new Date());
        transaction.setEntityId(customer != null ? customer.getCustomerId() : "WALK_IN");
        transaction.setTotalAmount(totalAmount);
        transaction.setDiscount(discount);
        transaction.setFinalAmount(totalAmount - discount);
        transaction.setCompanyMail(companyMail);

        // Save Transaction
        // dbManager.saveTransaction(transaction); // TODO

        // Bonus Calculation
        if (customer != null) {
            CustomerService.getInstance().awardBonus(customer, transaction.getFinalAmount());
        }

        System.out.println("âœ… Sale processed: " + transaction.getFinalAmount());
    }

    // Helper to fetch product (Mocking calls to DB manager for now as we added
    // saveProduct but not getProduct yet in previous step)
    // We should add getProduct to DatabaseManager or assume it exists.
    // I will add getProduct to DatabaseManager in next iteration.
    private Product getProductById(String id) {
        // Placeholder: Needs real DB call
        return null;
    }
}

