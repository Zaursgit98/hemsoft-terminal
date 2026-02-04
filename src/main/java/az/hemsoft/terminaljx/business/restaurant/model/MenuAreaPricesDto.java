package az.hemsoft.terminaljx.business.restaurant.model;

public class MenuAreaPricesDto {
    private String areaId;
    private double price;

    public MenuAreaPricesDto() {
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }
}

