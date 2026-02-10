package az.hemsoft.terminaljx.business.warehouse.repository;


import az.hemsoft.terminaljx.business.core.annotation.Query;
import az.hemsoft.terminaljx.business.core.annotation.Repository;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.ProductTypeGroup;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductTypeGroupRepository extends BaseRepository<ProductTypeGroup,Long> {

   public List<ProductTypeGroup> findByCompanyId(Long companyId) {
        return new ArrayList<ProductTypeGroup>();
    }

   public List<ProductTypeGroup> findByCompanyIdOrderByNameAsc(Long companyId){
        return new ArrayList<ProductTypeGroup>();
    }

    @Query("SELECT g FROM ProductTypeGroup g JOIN g.branchIds b WHERE b = :branchId AND g.companyId = :companyId")
    public List<ProductTypeGroup>findAllByCompanyIdAndBranchId(Long companyId,Long branchId){
        return new ArrayList<ProductTypeGroup>();
    }

}
