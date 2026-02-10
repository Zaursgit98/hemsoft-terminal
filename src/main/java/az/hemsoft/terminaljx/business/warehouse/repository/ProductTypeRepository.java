package az.hemsoft.terminaljx.business.warehouse.repository;


import az.hemsoft.terminaljx.business.core.annotation.Param;
import az.hemsoft.terminaljx.business.core.annotation.Query;
import az.hemsoft.terminaljx.business.core.annotation.Repository;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.ProductType;

import java.util.ArrayList;
import java.util.List;

@Repository
public class ProductTypeRepository extends BaseRepository<ProductType,Long> {
  public List<ProductType> findByCompanyId(Long companyId){
        return new ArrayList<ProductType>();}

public List<ProductType>findByCompanyIdOrderByNameAsc(Long companyId){
      return new ArrayList<ProductType>();
}

public List<ProductType>findByProductTypeGroupIdAndCompanyId(Long groupId,Long companyId){
      return new ArrayList<ProductType>();
}

@Query("SELECT t FROM ProductType t JOIN t.branchIds b WHERE b = :branchId AND t.companyId = :companyId")
public List<ProductType>findAllByBranchIdAndCompanyId(@Param("branchId") Long branchId,@Param("companyId") Long companyId){
      return new ArrayList<ProductType>();
}
}
