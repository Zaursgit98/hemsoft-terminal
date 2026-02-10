package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.ProductType;
import az.hemsoft.terminaljx.business.warehouse.service.ProductTypeService;

import java.util.List;

@RestController
public class ProductTypeController {

    private final ProductTypeService typeService;

    public ProductTypeController(ProductTypeService typeService) {
        this.typeService = typeService;
    }

    @GetMapping
    public List<ProductType> getByCompany(
            @PathVariable Long companyId) {
        return typeService.getByCompany(companyId);
    }

    @GetMapping("/ordered")
    public List<ProductType> getByCompanyOrderName(
            @PathVariable Long companyId) {
        return typeService.getByCompanyOrderName(companyId);
    }

    @GetMapping("/group/{groupId}")
    public List<ProductType> getByGroup(
            @PathVariable Long groupId,
            @RequestParam Long companyId) {

        return typeService.getByGroup(groupId, companyId);
    }

    @GetMapping("/branch/{branchId}")
    public List<ProductType> getByBranch(
            @PathVariable Long branchId,
            @RequestParam Long companyId) {

        return typeService.getByBranch(branchId, companyId);
    }

    @PostMapping
    public ProductType create(@RequestBody ProductType type) {
        return typeService.create(type);
    }

    @PutMapping("/{id}")
    public ProductType update(
            @PathVariable Long id,
            @RequestBody ProductType typeDetail) {

        return typeService.update(id, typeDetail);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        typeService.delete(id);
    }
}
