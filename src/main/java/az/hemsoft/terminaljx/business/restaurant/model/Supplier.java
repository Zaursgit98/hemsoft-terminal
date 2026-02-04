package az.hemsoft.terminaljx.business.restaurant.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class Supplier {
    private String supplierId;
    private String supplierName;
    private String contactInfo; // Phone or Email
    private double balance; // Amount we owe them (positive) or they owe us (negative)
    private String companyMail;
}
