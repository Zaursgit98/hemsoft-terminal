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
import java.util.ArrayList;
import java.util.List;


@Table(name = "product_measure", schema = "warehouse")
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductMeasure {

    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "code", nullable = false)
    private String code;

    @Column(name = "conversion_factor", nullable = false)
    @Builder.Default
    private BigDecimal conversionFactor = BigDecimal.ONE;


    @Column(name = "is_base")
    @Builder.Default
    private Boolean isBase=false;


    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @ElementCollection
    @Column(name = "branch_id")
    @Builder.Default
    private List<Long> branchIds = new ArrayList<>();


}
