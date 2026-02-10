package az.hemsoft.terminaljx.business.warehouse.model;


import
        az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.core.annotation.Table;
import az.hemsoft.terminaljx.business.core.enums.EnumType;
import az.hemsoft.terminaljx.business.warehouse.enums.SerialStatus;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;

@Table(name = "product_serial", schema = "warehouse")
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ProductSerial {

    @Id
    private Long id;

    @Column(name = "serial_number", nullable = false, unique = true)
    private String serialNumber;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    @Builder.Default
    private SerialStatus serialStatus = SerialStatus.AVALIABLE;

    @Column(name = "weight")
    private BigDecimal weight;

    @Column(name = "unit_price")
    private BigDecimal unitPrice;

    @Column(name = "total_price")
    private BigDecimal totalPrice;

    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "variant_id", nullable = false)
    @JsonIgnore
    private ProductVariant variant;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @Column(name = "purchase_date")
    private LocalDateTime purchaseDate;

    @Column(name = "sold_date")
    private LocalDateTime soldDate;

    @Column(name = "customer_id")
    private Long customerId;
}
