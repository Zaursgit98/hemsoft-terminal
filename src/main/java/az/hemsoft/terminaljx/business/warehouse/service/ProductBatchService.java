package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.Product;
import az.hemsoft.terminaljx.business.warehouse.model.ProductBatch;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductBatchRepository;

import java.time.LocalDate;
import java.util.List;

@Service
public class ProductBatchService {

    private final ProductBatchRepository batchRepository;

    public ProductBatchService() {
        this.batchRepository = new ProductBatchRepository();
    }

    public List<ProductBatch> getAllByProductId(Long productId, Long companyId) {
        return batchRepository.findAllByProductIdAndCompanyId(productId, companyId);
    }
    public List<ProductBatch> getAllByBranchId(Long branchId, Long companyId) {
        return batchRepository.findAllByBranchIdAndCompanyId(branchId, companyId);
    }

    public List<ProductBatch> getExpiryBatches(LocalDate date,Long companyId){
           return  batchRepository.findExpiringBatches(date,companyId);
    }

    public List<ProductBatch>getExpiryBetween(LocalDate startDate,LocalDate endDate,Long companyId){
        return batchRepository.findBatchesExpiringBetween(startDate,endDate,companyId);
    }

    public ProductBatch create(ProductBatch batch) {
        return batchRepository.save(batch);
    }

    public ProductBatch update(Long id,ProductBatch batchDetail){
        ProductBatch productBatch = batchRepository.findById(id).get();
        productBatch.setBatchNumber(batchDetail.getBatchNumber());
        productBatch.setExpiryDate(batchDetail.getExpiryDate());
        productBatch.setPurchaseDate(batchDetail.getPurchaseDate());
        productBatch.setActive(batchDetail.getActive());
        productBatch.setBranchId(batchDetail.getBranchId());
        productBatch.setCompanyId(batchDetail.getCompanyId());
        productBatch.setProductionDate(batchDetail.getProductionDate());
        productBatch.setProductVariant(batchDetail.getProductVariant());
        productBatch.setQuantity(batchDetail.getQuantity());
        productBatch.setRemainingQuantity(batchDetail.getRemainingQuantity());

        return batchRepository.save(productBatch);
    }


    public void delete(Long id) {batchRepository.delete(id);}

}
