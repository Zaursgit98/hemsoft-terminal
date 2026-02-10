package az.hemsoft.terminaljx.business.warehouse.model;


import az.hemsoft.terminaljx.business.core.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;


@Table(name = "product_batch", schema = "warehouse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProductBatch {

    @Id
    private Long id;

    @Column(name = "batch_number")
    private String batchNumber;

    @Column(name = "production_date")
    private LocalDate productionDate;

    @Column(name = "expiry_date")
    private LocalDate expiryDate;

    @Column(name = "quantity")
    private BigDecimal quantity;

    @Column(name = "remaining_quantity")
    private BigDecimal remainingQuantity;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "company_id")
    private Long companyId;


    @ManyToOne
    @JoinColumn(name = "product_variant_id")
    @JsonIgnore
    private ProductVariant productVariant;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "active")
    @Builder.Default
    private Boolean active=true;
}
