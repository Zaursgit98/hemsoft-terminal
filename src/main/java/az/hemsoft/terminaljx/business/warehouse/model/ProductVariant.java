package az.hemsoft.terminaljx.business.warehouse.model;


import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.core.annotation.Table;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;


@Table(name = "product_variant", schema = "warehouse")
@Data
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@NoArgsConstructor
@AllArgsConstructor
public class ProductVariant {

    @Id
    private Long id;

    @Column(name = "sku",unique = true)
    private String sku;

    @Column(name = "barcode")
    private String barcode;

    @Column(name = "companyId",nullable = false)
    private Long companyId;

    @ManyToOne
    @JoinColumn(name = "product_id", nullable = false)
    @JsonIgnore
    private Product product;

    @ManyToMany
    @JoinTable(name = "variant_attribute",schema = "warehouse",
            joinColumns =@JoinColumn(name = "variant_id"),
            inverseJoinColumns =@JoinColumn(name = "attribute_id") )
    @Builder.Default
    private List<ProductAttribute>productAttributes=new ArrayList<>();


    @Column(name = "active")
    @Builder.Default
    private Boolean active=true;
}
