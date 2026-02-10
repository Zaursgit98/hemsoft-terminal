package az.hemsoft.terminaljx.business.warehouse.model;


import az.hemsoft.terminaljx.business.core.annotation.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Table(name = "product_type_group", schema = "warehouse")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductTypeGroup {

    @Id
    private Long id;

    @Column(name = "name")
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "company_id")
    private Long companyId;

    @ElementCollection
    @CollectionTable(name = "product_type_group_branch",schema = "warehouse",
            joinColumns = @JoinColumn(name = "group_id"))
    @Column(name = "branch_id")
    @Builder.Default
    private List<Long> branchIds = new ArrayList<>();

}
