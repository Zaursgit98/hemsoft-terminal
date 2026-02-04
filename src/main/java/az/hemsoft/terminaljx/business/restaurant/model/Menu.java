package az.hemsoft.terminaljx.business.restaurant.model;

import java.util.Date;
import java.util.List;

public class Menu {
    private String menuId;
    private String menuName;
    private String document;
    private String imageId;
    private String imageUrl;
    private Date createdDate;
    private Date updatedDate;
    private MenuGroup group;
    private Printer printer;
    private List<MenuAreaPricesDto> priceList;
    private List<MenuMeasure> menuMeasurePrice;
    private List<SetMenus> setMenus;
    private List<MenuDeliveryPriceDto> deliveryCompanyPrices;
    private double standardPrice;
    private String companyMail;

    public Menu() {
    }

    public String getMenuId() {
        return menuId;
    }

    public void setMenuId(String menuId) {
        this.menuId = menuId;
    }

    public String getMenuName() {
        return menuName;
    }

    public void setMenuName(String menuName) {
        this.menuName = menuName;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getImageId() {
        return imageId;
    }

    public void setImageId(String imageId) {
        this.imageId = imageId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }

    public Date getCreatedDate() {
        return createdDate;
    }

    public void setCreatedDate(Date createdDate) {
        this.createdDate = createdDate;
    }

    public Date getUpdatedDate() {
        return updatedDate;
    }

    public void setUpdatedDate(Date updatedDate) {
        this.updatedDate = updatedDate;
    }

    public MenuGroup getGroup() {
        return group;
    }

    public void setGroup(MenuGroup group) {
        this.group = group;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public List<MenuAreaPricesDto> getPriceList() {
        return priceList;
    }

    public void setPriceList(List<MenuAreaPricesDto> priceList) {
        this.priceList = priceList;
    }

    public List<MenuMeasure> getMenuMeasurePrice() {
        return menuMeasurePrice;
    }

    public void setMenuMeasurePrice(List<MenuMeasure> menuMeasurePrice) {
        this.menuMeasurePrice = menuMeasurePrice;
    }

    public List<SetMenus> getSetMenus() {
        return setMenus;
    }

    public void setSetMenus(List<SetMenus> setMenus) {
        this.setMenus = setMenus;
    }

    public List<MenuDeliveryPriceDto> getDeliveryCompanyPrices() {
        return deliveryCompanyPrices;
    }

    public void setDeliveryCompanyPrices(List<MenuDeliveryPriceDto> deliveryCompanyPrices) {
        this.deliveryCompanyPrices = deliveryCompanyPrices;
    }

    public double getStandardPrice() {
        return standardPrice;
    }

    public void setStandardPrice(double standardPrice) {
        this.standardPrice = standardPrice;
    }

    public String getCompanyMail() {
        return companyMail;
    }

    public void setCompanyMail(String companyMail) {
        this.companyMail = companyMail;
    }
}

