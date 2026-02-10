package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.ProductVariant;
import az.hemsoft.terminaljx.business.warehouse.service.ProductVariantService;

import java.util.List;

@RestController
public class ProductVariantController {

    private final ProductVariantService variantService;

    public ProductVariantController(ProductVariantService variantService) {
        this.variantService = variantService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductVariant> getByProduct(
            @PathVariable Long productId,
            @RequestParam Long companyId) {

        return variantService.getByProduct(productId, companyId);
    }

    @GetMapping("/sku")
    public List<ProductVariant> getBySku(
            @RequestParam String sku,
            @RequestParam Long companyId) {

        return variantService.getBySku(sku, companyId);
    }

    @GetMapping("/barcode")
    public List<ProductVariant> getByBarcode(
            @RequestParam String barcode,
            @RequestParam Long companyId) {

        return variantService.getByBarcode(barcode, companyId);
    }

    @PostMapping
    public ProductVariant create(@RequestBody ProductVariant productVariant) {
        return variantService.create(productVariant);
    }

    @PutMapping("/{id}")
    public ProductVariant update(
            @PathVariable Long id,
            @RequestBody ProductVariant variantDetail) {

        return variantService.update(id, variantDetail);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        variantService.delete(id);
    }
}
