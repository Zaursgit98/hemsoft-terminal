package az.hemsoft.terminaljx.business.restaurant.model;

public class Printer {
    private String printerId;
    private String printerName;
    private String printerCode;
    private String document;
    private String companyMail;

    public Printer() {
    }

    public Printer(String printerId, String printerName, String printerCode) {
        this.printerId = printerId;
        this.printerName = printerName;
        this.printerCode = printerCode;
    }

    public String getPrinterId() {
        return printerId;
    }

    public void setPrinterId(String printerId) {
        this.printerId = printerId;
    }

    public String getPrinterName() {
        return printerName;
    }

    public void setPrinterName(String printerName) {
        this.printerName = printerName;
    }

    public String getPrinterCode() {
        return printerCode;
    }

    public void setPrinterCode(String printerCode) {
        this.printerCode = printerCode;
    }

    public String getDocument() {
        return document;
    }

    public void setDocument(String document) {
        this.document = document;
    }

    public String getCompanyMail() {
        return companyMail;
    }

    public void setCompanyMail(String companyMail) {
        this.companyMail = companyMail;
    }

    @Override
    public String toString() {
        return "Printer{" +
                "printerId='" + printerId + '\'' +
                ", printerName='" + printerName + '\'' +
                ", printerCode='" + printerCode + '\'' +
                '}';
    }
}

