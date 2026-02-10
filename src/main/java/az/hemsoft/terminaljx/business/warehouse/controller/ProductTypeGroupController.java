package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.ProductTypeGroup;
import az.hemsoft.terminaljx.business.warehouse.service.ProductTypeGroupService;

import java.util.List;

@RestController
public class ProductTypeGroupController {

    private final ProductTypeGroupService groupService;

    public ProductTypeGroupController(ProductTypeGroupService groupService) {
        this.groupService = groupService;
    }

    @GetMapping
    public List<ProductTypeGroup> getByCompany(
            @PathVariable Long companyId) {

        return groupService.getByCompany(companyId);
    }

    @GetMapping("/ordered")
    public List<ProductTypeGroup> getByCompanyOrderName(
            @PathVariable Long companyId) {

        return groupService.getByCompanyOrderName(companyId);
    }

    @GetMapping("/branch/{branchId}")
    public List<ProductTypeGroup> getByCompanyAndBranch(
            @PathVariable Long branchId,
            @RequestParam Long companyId) {

        return groupService.getByCompanyAndBranch(companyId, branchId);
    }

    @PostMapping
    public ProductTypeGroup create(@RequestBody ProductTypeGroup group) {
        return groupService.create(group);
    }

    @PutMapping("/{id}")
    public ProductTypeGroup update(
            @PathVariable Long id,
            @RequestBody ProductTypeGroup groupDetail) {

        return groupService.update(id, groupDetail);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        groupService.delete(id);
    }
}
