package az.hemsoft.terminaljx.business.warehouse.model;

import az.hemsoft.terminaljx.business.core.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.fasterxml.jackson.annotation.JsonInclude;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table("product_groups")
public class ProductGroup {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;

    @OneToMany(mappedBy = "group")
    private List<Product> products;
}
