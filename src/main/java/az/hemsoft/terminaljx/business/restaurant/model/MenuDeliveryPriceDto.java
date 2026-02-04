package az.hemsoft.terminaljx.business.restaurant.model;

public class MenuDeliveryPriceDto {
    private String companyId;
    private double price;

    public MenuDeliveryPriceDto() {
    }

    public String getCompanyId() {
        return companyId;
    }

    public void setCompanyId(String companyId) {
        this.companyId = companyId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

