package az.hemsoft.terminaljx.business.warehouse.model;

import az.hemsoft.terminaljx.business.core.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table("products")
public class Product {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false, unique = true)

    private String name;

    @Column(name = "code", unique = true)
    private String code;

    @ManyToOne(targetEntity = ProductGroup.class, fetch = FetchType.LAZY)
    @JoinColumn(name = "group_id")
    private ProductGroup group;

    @Column(name = "price")
    @Index
    private Double price;

    @Column(name = "measurement")
    private String measurement;

    @Column(name = "active")
    @Index
    @Builder.Default
    private Boolean active = true;
}
