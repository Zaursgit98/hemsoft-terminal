package az.hemsoft.terminaljx.business.warehouse.repository;


import az.hemsoft.terminaljx.business.core.annotation.Query;
import az.hemsoft.terminaljx.business.core.annotation.Repository;
import az.hemsoft.terminaljx.business.core.annotation.RestController;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.ProductMeasure;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductMeasureRepository extends BaseRepository<ProductMeasure,Long> {
    public List<ProductMeasure>findAllByProductIdAndCompanyId(Long productId,Long companyId)
    {return new ArrayList<ProductMeasure>();}

    @Query("SELECT m FROM ProductMeasure m JOIN m.branchIds b WHERE b = :branchId AND m.companyId = :companyId")
    public List<ProductMeasure>findAllByBranchIdAndCompanyId(Long branchId,Long companyId){
        return new ArrayList<>();
    }
}
