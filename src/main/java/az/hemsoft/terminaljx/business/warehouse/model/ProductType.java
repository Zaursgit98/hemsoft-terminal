package az.hemsoft.terminaljx.business.warehouse.model;


import az.hemsoft.terminaljx.business.core.annotation.*;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.checkerframework.checker.units.qual.C;

import java.util.ArrayList;
import java.util.List;

@Table(name = "product_type", schema = "warehouse")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
@JsonIgnoreProperties({"hibernateLazyInitializer", "handler"})
public class ProductType {

    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ManyToOne
    @JoinColumn(name = "product_type_group_id")
    private ProductTypeGroup productTypeGroup;

    @ElementCollection
    @CollectionTable(name = "product_type_branch",schema ="warehouse",
            joinColumns = @JoinColumn(name = "product_type_id"))
    @Column(name = "branch_id")
    @Builder.Default
    private List<Long> branchIds = new ArrayList<>();
}
