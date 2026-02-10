package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.enums.SerialStatus;
import az.hemsoft.terminaljx.business.warehouse.model.ProductSerial;
import az.hemsoft.terminaljx.business.warehouse.service.ProductSerialService;

import java.util.List;

@RestController
public class ProductSerialController {

    private final ProductSerialService serialService;

    public ProductSerialController(ProductSerialService serialService) {
        this.serialService = serialService;
    }

    @GetMapping("/product/{productId}")
    public List<ProductSerial> getByProduct(
            @PathVariable Long productId,
            @RequestParam Long companyId) {

        return serialService.getByProduct(productId, companyId);
    }

    @GetMapping("/product/{productId}/status")
    public List<ProductSerial> getByProductAndStatus(
            @PathVariable Long productId,
            @RequestParam SerialStatus status,
            @RequestParam Long companyId) {

        return serialService.getByProductAnStatus(productId, status, companyId);
    }

    @GetMapping("/serial/{serialnumber}")
   public List<ProductSerial>findBySerial(@PathVariable String serailNumber,@RequestParam Long companyId){
      return   serialService.findBySerial(serailNumber, companyId);
    }

    @GetMapping("/branch/{branchId}/status")
    public List<ProductSerial> getByBranchAndStatus(
            @PathVariable Long branchId,
            @RequestParam SerialStatus status,
            @RequestParam Long companyId) {

        return serialService.getByBranchAndStatus(branchId, status, companyId);
    }

    @PostMapping
    public ProductSerial create(@RequestBody ProductSerial serial) {
        return serialService.create(serial);
    }

    @PutMapping("/{id}")
    public ProductSerial update(
            @PathVariable Long id,
            @RequestBody ProductSerial serialDetail) {

        return serialService.update(id, serialDetail);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        serialService.delete(id);
    }

}

