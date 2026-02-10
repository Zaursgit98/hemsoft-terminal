package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.ProductPrice;
import az.hemsoft.terminaljx.business.warehouse.service.ProductPriceService;

import java.util.List;

@RestController
public class ProductPriceController {

    private final ProductPriceService priceService;

    public ProductPriceController(ProductPriceService priceService) {
        this.priceService = priceService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductPrice> getByProduct(
            @PathVariable Long productId,
            @RequestParam Long companyId) {

        return priceService.getByProduct(productId, companyId);
    }

    @GetMapping("/product/{productId}/branch/{branchId}")
    public List<ProductPrice> getByProductAndBranch(
            @PathVariable Long productId,
            @PathVariable Long branchId,
            @RequestParam Long companyId) {

        return priceService.getByProductAndBranch(productId, branchId, companyId);
    }

    @PostMapping
    public ProductPrice create(@RequestBody ProductPrice price) {
        return priceService.create(price);
    }

    @PutMapping("/{id}")
    public ProductPrice update(
            @PathVariable Long id,
            @RequestBody ProductPrice price) {

        return priceService.update(id, price);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        priceService.delete(id);
    }
}
