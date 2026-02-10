package az.hemsoft.terminaljx.business.warehouse.model;


import az.hemsoft.terminaljx.business.core.annotation.Column;
import az.hemsoft.terminaljx.business.core.annotation.Id;
import az.hemsoft.terminaljx.business.core.annotation.Table;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;



@Table(name = "product_attribute", schema = "warehouse")
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ProductAttribute {


    @Id
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "value", nullable = false)
    private String value;

    @Column(name = "companyId", nullable = false)
    private Long companyId;


}
