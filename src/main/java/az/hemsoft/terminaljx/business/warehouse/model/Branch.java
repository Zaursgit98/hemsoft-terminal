package az.hemsoft.terminaljx.business.warehouse.model;


import az.hemsoft.terminaljx.business.core.annotation.Column;
import az.hemsoft.terminaljx.business.core.annotation.Id;
import az.hemsoft.terminaljx.business.core.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Table(name = "branch", schema = "warehouse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Branch {

    @Id
    private Long id;

    @Column(name = "branch_name")
    private String branchName;

    @Column(name = "company_id")
    private Long companyId;


}
