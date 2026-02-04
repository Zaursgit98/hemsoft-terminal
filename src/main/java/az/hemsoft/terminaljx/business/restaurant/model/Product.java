package az.hemsoft.terminaljx.business.restaurant.model;

public class Product {
    private String productId;
    private String productName;
    private String barcode;
    private String groupId;
    private double purchasePrice; // AlÄ±ÅŸ qiymÉ™ti
    private double salePrice; // SatÄ±ÅŸ qiymÉ™ti
    private String imagePath; // Local path to image
    private double stockQuantity; // Current stock
    private String companyMail;

    public Product() {
    }

    public Product(String productId, String productName, String barcode, String groupId, double purchasePrice,
            double salePrice, String imagePath, double stockQuantity, String companyMail) {
        this.productId = productId;
        this.productName = productName;
        this.barcode = barcode;
        this.groupId = groupId;
        this.purchasePrice = purchasePrice;
        this.salePrice = salePrice;
        this.imagePath = imagePath;
        this.stockQuantity = stockQuantity;
        this.companyMail = companyMail;
    }

    public String getProductId() {
        return productId;
    }

    public void setProductId(String productId) {
        this.productId = productId;
    }

    public String getProductName() {
        return productName;
    }

    public void setProductName(String productName) {
        this.productName = productName;
    }

    public String getBarcode() {
        return barcode;
    }

    public void setBarcode(String barcode) {
        this.barcode = barcode;
    }

    public String getGroupId() {
        return groupId;
    }

    public void setGroupId(String groupId) {
        this.groupId = groupId;
    }

    public double getPurchasePrice() {
        return purchasePrice;
    }

    public void setPurchasePrice(double purchasePrice) {
        this.purchasePrice = purchasePrice;
    }

    public double getSalePrice() {
        return salePrice;
    }

    public void setSalePrice(double salePrice) {
        this.salePrice = salePrice;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }

    public double getStockQuantity() {
        return stockQuantity;
    }

    public void setStockQuantity(double stockQuantity) {
        this.stockQuantity = stockQuantity;
    }

    public String getCompanyMail() {
        return companyMail;
    }

    public void setCompanyMail(String companyMail) {
        this.companyMail = companyMail;
    }
}

