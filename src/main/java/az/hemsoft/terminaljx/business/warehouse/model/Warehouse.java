package az.hemsoft.terminaljx.business.warehouse.model;

import az.hemsoft.terminaljx.business.core.annotation.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import com.fasterxml.jackson.annotation.JsonInclude;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
@Table("warehouses")
public class Warehouse {
    @Id
    @Column(name = "id")
    private Integer id;

    @Column(name = "name", nullable = false)
    private String name;

    @Column(name = "measurement")
    private String measurement;

    @Column(name = "address")
    private String address;

    @Column(name = "active")
    @Builder.Default
    private Boolean active = true;
}
