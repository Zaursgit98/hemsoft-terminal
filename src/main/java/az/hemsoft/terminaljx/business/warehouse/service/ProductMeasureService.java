package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.ProductMeasure;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductMeasureRepository;

import java.util.List;

@Service
public class ProductMeasureService {
    private final ProductMeasureRepository measureRepository;


    public ProductMeasureService() {
        this.measureRepository = new ProductMeasureRepository();
    }

    public List<ProductMeasure> getAllByProduct(Long productId, Long companyId) {
        return measureRepository.findAllByProductIdAndCompanyId(productId, companyId);
    }

    public List<ProductMeasure> getByBranch(Long branchId, Long companyId) {
        return measureRepository.findAllByBranchIdAndCompanyId(branchId, companyId);
    }

    public ProductMeasure create(ProductMeasure measure) {
        return measureRepository.save(measure);
    }

    public ProductMeasure update(Long id, ProductMeasure measure) {
        ProductMeasure productMeasure = measureRepository.findById(id).get();
        productMeasure.setName(measure.getName());
        productMeasure.setCode(measure.getCode());
        productMeasure.setBranchIds(measure.getBranchIds());
        productMeasure.setProduct(measure.getProduct());
        productMeasure.setIsBase(measure.getIsBase());
        productMeasure.setCompanyId(measure.getCompanyId());
        productMeasure.setConversionFactor(measure.getConversionFactor());
        return measureRepository.save(productMeasure);
    }

    public void delete(Long id) {
        measureRepository.delete(id);
    }


}
















