package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.ProductMeasure;
import az.hemsoft.terminaljx.business.warehouse.service.ProductMeasureService;

import java.util.List;

@RestController
public class ProductMeasureController {

    private final ProductMeasureService measureService;

    public ProductMeasureController(ProductMeasureService measureService) {
        this.measureService = measureService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductMeasure> getAllByProduct(
            @PathVariable Long productId,
            @RequestParam Long companyId) {

        return measureService.getAllByProduct(productId, companyId);
    }

    @GetMapping("/branch/{branchId}")
    public List<ProductMeasure> getByBranch(
            @PathVariable Long branchId,
            @RequestParam Long companyId) {

        return measureService.getByBranch(branchId, companyId);
    }

    @PostMapping
    public ProductMeasure create(@RequestBody ProductMeasure measure) {
        return measureService.create(measure);
    }

    @PutMapping("/{id}")
    public ProductMeasure update(
            @PathVariable Long id,
            @RequestBody ProductMeasure measure) {

        return measureService.update(id, measure);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        measureService.delete(id);
    }
}
