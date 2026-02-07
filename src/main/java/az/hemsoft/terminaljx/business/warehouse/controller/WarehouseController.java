package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.Warehouse;
import az.hemsoft.terminaljx.business.warehouse.service.WarehouseService;
import java.util.List;

@RestController("/api/warehouses")
public class WarehouseController {
    private final WarehouseService service = new WarehouseService();

    @GetMapping
    public List<Warehouse> getAll() {
        return service.getAll();
    }

    @GetMapping("/{id}")
    public Warehouse getById(@PathVariable("id") Integer id) {
        return service.getById(id);
    }

    @PostMapping
    public Warehouse create(@RequestBody Warehouse warehouse) {
        return service.create(warehouse);
    }

    @PutMapping("/{id}")
    public Warehouse update(@PathVariable("id") Integer id, @RequestBody Warehouse warehouse) {
        warehouse.setId(id);
        service.update(warehouse);
        return warehouse;
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable("id") Integer id) {
        service.delete(id);
    }
}
