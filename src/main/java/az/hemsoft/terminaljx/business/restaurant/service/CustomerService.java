package az.hemsoft.terminaljx.business.restaurant.service;


public class CustomerService {
    private static CustomerService instance;
    private final az.hemsoft.terminaljx.business.restaurant.service.DatabaseManager dbManager;
    private static final double DEFAULT_BONUS_PERCENTAGE = 0.01; // 1%

    private CustomerService() {
        this.dbManager = az.hemsoft.terminaljx.business.restaurant.service.DatabaseManager.getInstance();
    }

    public static synchronized CustomerService getInstance() {
        if (instance == null) {
            instance = new CustomerService();
        }
        return instance;
    }

    public void awardBonus(az.hemsoft.terminaljx.business.restaurant.model.Customer customer, double saleAmount) {
        double bonusEarned = saleAmount * DEFAULT_BONUS_PERCENTAGE;
        customer.setBonusBalance(customer.getBonusBalance() + bonusEarned);
        dbManager.saveCustomer(customer);
        System.out.println("\uD83C\uDF81 Bonus Awarded: " + bonusEarned + " to " + customer.getFullName());
    }

    // Placeholder for finding customer by card
    public az.hemsoft.terminaljx.business.restaurant.model.Customer findCustomerByCard(String cardNumber) {
        // TODO: Implement dbManager.findCustomerByCard(cardNumber)
        return null;
    }
}

