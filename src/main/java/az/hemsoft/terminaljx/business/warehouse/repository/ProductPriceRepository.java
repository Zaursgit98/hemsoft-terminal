package az.hemsoft.terminaljx.business.warehouse.repository;


import az.hemsoft.terminaljx.business.core.annotation.Repository;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.ProductPrice;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductPriceRepository extends BaseRepository<ProductPrice, Long> {

    public List<ProductPrice> findAllByProductIdAndCompanyId(Long productId, Long companyId) {
        return new ArrayList<ProductPrice>();}

    public List<ProductPrice> findAllByProductIdAndBranchIdAndCompanyId(Long productId, Long branchId, Long companyId) {
        return new ArrayList<ProductPrice>();}


}
