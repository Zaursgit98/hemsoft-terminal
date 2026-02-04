package az.hemsoft.terminaljx.business.restaurant.model;

public class MenuMeasure {
    private String id;
    private String measureName;
    private double price;
    private String companyMail;

    public MenuMeasure() {
    }

    public MenuMeasure(String id, String measureName, double price) {
        this.id = id;
        this.measureName = measureName;
        this.price = price;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMeasureName() {
        return measureName;
    }

    public void setMeasureName(String measureName) {
        this.measureName = measureName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCompanyMail() {
        return companyMail;
    }

    public void setCompanyMail(String companyMail) {
        this.companyMail = companyMail;
    }

    @Override
    public String toString() {
        return "MenuMeasure{" +
                "id='" + id + '\'' +
                ", measureName='" + measureName + '\'' +
                ", price=" + price +
                '}';
    }
}

