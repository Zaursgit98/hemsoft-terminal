package az.hemsoft.terminaljx.business.restaurant.model;

public class Customer {
    private String customerId;
    private String fullName;
    private String phoneNumber;
    private String cardNumber; // For Bonus/Loyalty card
    private double bonusBalance;
    private String companyMail;

    public Customer() {
    }

    public Customer(String customerId, String fullName, String phoneNumber, String cardNumber, double bonusBalance,
            String companyMail) {
        this.customerId = customerId;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.cardNumber = cardNumber;
        this.bonusBalance = bonusBalance;
        this.companyMail = companyMail;
    }

    public String getCustomerId() {
        return customerId;
    }

    public void setCustomerId(String customerId) {
        this.customerId = customerId;
    }

    public String getFullName() {
        return fullName;
    }

    public void setFullName(String fullName) {
        this.fullName = fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getCardNumber() {
        return cardNumber;
    }

    public void setCardNumber(String cardNumber) {
        this.cardNumber = cardNumber;
    }

    public double getBonusBalance() {
        return bonusBalance;
    }

    public void setBonusBalance(double bonusBalance) {
        this.bonusBalance = bonusBalance;
    }

    public String getCompanyMail() {
        return companyMail;
    }

    public void setCompanyMail(String companyMail) {
        this.companyMail = companyMail;
    }
}

