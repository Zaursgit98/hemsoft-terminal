package az.hemsoft.terminaljx.business.restaurant.model;

import java.util.List;

public class SetMenus {
    private String menuId;
    private String menuName;
    private double menuPrice;
    private double count;
    private Boolean isSelectable;
    private Printer printer;
    private int maxSelection;
    private List<SetMenuOption> options;

    public SetMenus() {
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

    public double getMenuPrice() {
        return menuPrice;
    }

    public void setMenuPrice(double menuPrice) {
        this.menuPrice = menuPrice;
    }

    public double getCount() {
        return count;
    }

    public void setCount(double count) {
        this.count = count;
    }

    public Boolean getIsSelectable() {
        return isSelectable;
    }

    public void setIsSelectable(Boolean selectable) {
        isSelectable = selectable;
    }

    public Printer getPrinter() {
        return printer;
    }

    public void setPrinter(Printer printer) {
        this.printer = printer;
    }

    public int getMaxSelection() {
        return maxSelection;
    }

    public void setMaxSelection(int maxSelection) {
        this.maxSelection = maxSelection;
    }

    public List<SetMenuOption> getOptions() {
        return options;
    }

    public void setOptions(List<SetMenuOption> options) {
        this.options = options;
    }
}

