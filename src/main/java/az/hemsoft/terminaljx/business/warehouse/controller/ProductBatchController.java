package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.ProductBatch;
import az.hemsoft.terminaljx.business.warehouse.service.ProductBatchService;

import java.time.LocalDate;
import java.util.Date;
import java.util.List;

@RestController
public class ProductBatchController {

    private final ProductBatchService batchService;

    public ProductBatchController() {
        this.batchService = new ProductBatchService();
    }

    @GetMapping("/product/{productId}")
    public List<ProductBatch> getAllByProductId(
            @PathVariable Long productId,
            @RequestParam Long companyId) {

        return batchService.getAllByProductId(productId, companyId);
    }

    @GetMapping("/branch/{branchId}")
    public List<ProductBatch> getAllByBranchId(
            @PathVariable Long branchId,
            @RequestParam Long companyId) {

        return batchService.getAllByBranchId(branchId, companyId);
    }

    @GetMapping("/expiry")
    public List<ProductBatch> getExpiryBatches(
            @RequestParam @DateTimeFormat(iso=DateTimeFormat.ISO.DATE) LocalDate date,
            @RequestParam Long companyId) {

        return batchService.getExpiryBatches(date, companyId);
    }

    @GetMapping("/expiry-between")
    public List<ProductBatch> getExpiryBetween(
            @RequestParam @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDate startDate,
            @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) @RequestParam LocalDate endDate,
            @RequestParam Long companyId) {

        return batchService.getExpiryBetween(startDate, endDate, companyId);
    }

    @PostMapping
    public ProductBatch create(@RequestBody ProductBatch batch) {
        return batchService.create(batch);
    }

    @PutMapping("/{id}")
    public ProductBatch update(
            @PathVariable Long id,
            @RequestBody ProductBatch batchDetail) {

        return batchService.update(id, batchDetail);
    }

    @DeleteMapping("/{id}")
    public void delete(@PathVariable Long id) {
        batchService.delete(id);
    }
}
