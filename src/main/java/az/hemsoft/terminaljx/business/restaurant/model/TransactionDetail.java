package az.hemsoft.terminaljx.business.restaurant.model;

public class TransactionDetail {
    private String detailId;
    private String transactionId;
    private String productId;
    private double quantity;
    private double price; // Unit price at the time of transaction
    private double total;

    public TransactionDetail() {
    }

    public TransactionDetail(String detailId, String transactionId, String productId, double quantity, double price,
            double total) {
        this.detailId = detailId;
        this.transactionId = transactionId;
        this.productId = productId;
        this.quantity = quantity;
        this.price = price;
        this.total = total;
    }

    public String getDetailId() {
        return detailId;
    }

    public void setDetailId(String detailId) {
        this.detailId = detailId;
    }

    public String getTransactionId() {
        return transactionId;
    }

    public void setTransactionId(String transactionId) {
        this.transactionId = transactionId;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public double getQuantity() {
        return quantity;
    }

    public void setQuantity(double quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }
}

