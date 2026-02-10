package az.hemsoft.terminaljx.business.warehouse.repository;

import az.hemsoft.terminaljx.business.core.annotation.Param;
import az.hemsoft.terminaljx.business.core.annotation.Query;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.ProductBatch;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

public class ProductBatchRepository extends BaseRepository<ProductBatch,Long> {
   public   List<ProductBatch> findAllByProductIdAndCompanyId(Long productId, Long companyId)
    {return new ArrayList<ProductBatch>();};

   public List<ProductBatch> findAllByBranchIdAndCompanyId(Long branchId, Long companyId){
        return new ArrayList<ProductBatch>();}

    @Query("SELECT b FROM ProductBatch b WHERE b.expiryDate <= :date AND b.active = true AND b.companyId = :companyId")
  public   List<ProductBatch>findExpiringBatches(@Param("date")LocalDate date,@Param("companyId")Long companyId){
     return new ArrayList<ProductBatch>();}

    @Query("SELECT b FROM ProductBatch b WHERE b.expiryDate BETWEEN :startDate AND :endDate AND " +
            "b.active = true AND b.companyId = :companyId")
   public List<ProductBatch> findBatchesExpiringBetween(@Param("startDate") LocalDate startDate,
                                                  @Param("endDate") LocalDate endDate, @Param("companyId") Long companyId)
    {return new ArrayList<ProductBatch>();}
}



