package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.PriceType;
import az.hemsoft.terminaljx.business.warehouse.service.PriceTypeService;

import java.util.List;

@RestController
public class PriceTypeController {

    private final PriceTypeService priceTypeService;

    public PriceTypeController(PriceTypeService priceTypeService) {
        this.priceTypeService = priceTypeService;
    }


    @GetMapping("/company/{companyId}")
    public List<PriceType> getAll(@PathVariable Long companyId) {
        return priceTypeService.getAll(companyId);
    }


    @GetMapping("/branch/{branchId}/company/{companyId}")
    public List<PriceType> getAllByBranch(@PathVariable Long branchId,
                                          @RequestParam Long companyId) {
        return priceTypeService.getAllByBranch(branchId, companyId);
    }

    @PostMapping
    public PriceType create(@RequestBody PriceType priceType) {
        return priceTypeService.create(priceType);
    }


    @PutMapping("/{id}")
    public PriceType update(@PathVariable Long id,
                            @RequestBody PriceType typeDetail) {
        return priceTypeService.update(id, typeDetail);
    }


    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        priceTypeService.delete(id);
    }
}
