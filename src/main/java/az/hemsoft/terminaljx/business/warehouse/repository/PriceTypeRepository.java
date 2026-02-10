package az.hemsoft.terminaljx.business.warehouse.repository;

import az.hemsoft.terminaljx.business.core.annotation.Param;
import az.hemsoft.terminaljx.business.core.annotation.Query;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.PriceType;

import java.util.ArrayList;
import java.util.List;

public class PriceTypeRepository extends BaseRepository<PriceType, Long> {
    public List<PriceType> findAllByCompanyId(Long id) {
        return new ArrayList<>();
    }

    @Query("Select p from PriceType p join p.branchId b where b=:branchId and p.companyId=:companyId")
    public List<PriceType> findAllByBranchIdAndCompanyId(@Param("branchId") Long branchId,
                                                         @Param("companyId") Long companyId) {
        return new ArrayList<PriceType>();
    }

}
