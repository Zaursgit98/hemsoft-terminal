package az.hemsoft.terminaljx.business.restaurant.model;

public class MenuGroup {
    private String mgId;
    private String mgName;
    private String document;
    private String imageId;
    private String imageUrl;
    private Boolean isSelected;
    private String companyMail;

    public MenuGroup() {
    }

    public String getMgId() {
        return mgId;
    }

    public void setMgId(String mgId) {
        this.mgId = mgId;
    }

    public String getMgName() {
        return mgName;
    }

    public void setMgName(String mgName) {
        this.mgName = mgName;
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

    public Boolean getIsSelected() {
        return isSelected;
    }

    public void setIsSelected(Boolean selected) {
        isSelected = selected;
    }

    public String getCompanyMail() {
        return companyMail;
    }

    public void setCompanyMail(String companyMail) {
        this.companyMail = companyMail;
    }

    @Override
    public String toString() {
        return "MenuGroup{" +
                "mgId='" + mgId + '\'' +
                ", mgName='" + mgName + '\'' +
                '}';
    }
}

