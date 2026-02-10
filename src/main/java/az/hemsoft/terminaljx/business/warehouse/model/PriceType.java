package az.hemsoft.terminaljx.business.warehouse.model;

import az.hemsoft.terminaljx.business.core.annotation.Column;
import az.hemsoft.terminaljx.business.core.annotation.ElementCollection;
import az.hemsoft.terminaljx.business.core.annotation.Id;
import az.hemsoft.terminaljx.business.core.annotation.Table;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;



@Table(name = "price_type", schema = "warehouse")
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class PriceType {


    @Id
    private Long id;

    @Column(name = "name",nullable = false)
    private String name;

    @Column(name = "description")
    private String description;

    @Column(name = "company_id", nullable = false)
    private Long companyId;

    @ElementCollection
    @Column(name = "branchId")
    @Builder.Default
    private List<Long>branchId=new ArrayList<>();
}
