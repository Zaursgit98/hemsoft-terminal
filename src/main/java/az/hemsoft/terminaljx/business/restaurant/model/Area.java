package az.hemsoft.terminaljx.business.restaurant.model;

public class Area {
    private String areaId;
    private String areaName;
    private String document;
    private boolean isSelected;
    private Branch branch;

    public Area() {
    }

    public Area(String areaId, String areaName, String document, Branch branch, boolean isSelected) {
        this.areaId = areaId;
        this.areaName = areaName;
        this.document = document;
        this.branch = branch;
        this.isSelected = isSelected;
    }

    public String getAreaId() {
        return areaId;
    }

    public void setAreaId(String areaId) {
        this.areaId = areaId;
    }

    public String getAreaName() {
        return areaName;
    }

    public void setAreaName(String areaName) {
        this.areaName = areaName;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public boolean isSelected() {
        return isSelected;
    }

    public void setSelected(boolean selected) {
        isSelected = selected;
    }

    public Branch getBranch() {
        return branch;
    }

    public void setBranch(Branch branch) {
        this.branch = branch;
    }
}

