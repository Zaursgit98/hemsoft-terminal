package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.ProductAttribute;
import az.hemsoft.terminaljx.business.warehouse.service.ProductAttributeService;
import java.util.List;

@RestController

public class ProductAttributeController {

    private final ProductAttributeService attributeService;

    public ProductAttributeController(ProductAttributeService attributeService) {
        this.attributeService = attributeService;
    }

    @GetMapping("/all/{id}")
    public List<ProductAttribute> getAll(@PathVariable Long companyId) {
        return attributeService.getAll(companyId);
    }

    @GetMapping("/byname/{name}")
    public List<ProductAttribute> getByName(@PathVariable String name,
                                            @RequestParam Long companyId) {
        return attributeService.getByName(name, companyId);
    }

    @PostMapping("/save")
    public ProductAttribute create(@RequestBody ProductAttribute attribute) {
        return attributeService.createAttribute(attribute);
    }

    @PutMapping("update/{id}")
    public ProductAttribute update(@PathVariable Long id,
                                   @RequestBody ProductAttribute attribute) {
        return attributeService.updateAttribute(id, attribute);
    }

    @DeleteMapping("delete/{id}")
    public void delete(@PathVariable Long id) {
        attributeService.deleteAttribute(id);
    }
}
