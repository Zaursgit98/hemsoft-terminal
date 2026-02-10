package az.hemsoft.terminaljx.business.warehouse.model;


import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.core.annotation.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;


@Table(name = "product_price", schema = "warehouse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductPrice {

    @Id
    private Long id;

    @Column(name = "price", nullable = false)
    private BigDecimal price;

    @ManyToOne
    @JoinColumn(name = "price_type_id", nullable = false)
    private PriceType priceType;


    @Column(name = "branch_id")
    private Long branchId;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;


}
