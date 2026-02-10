package az.hemsoft.terminaljx.business.warehouse.repository;

import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.enums.SerialStatus;
import az.hemsoft.terminaljx.business.warehouse.model.ProductSerial;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class ProductSerialRepository extends BaseRepository<ProductSerial, Long> {
    public List<ProductSerial> findAllByProductIdAndCompanyId(Long productId, Long companyId) {
        return new ArrayList<ProductSerial>();
    }

    public List<ProductSerial> findAllByProductIdAndStatusAndCompanyId(Long productId, SerialStatus status, Long companyId) {
        return new ArrayList<ProductSerial>();
    }

    public List<ProductSerial> findAllByBranchIdAndStatusAndCompanyId(Long branchId, SerialStatus status, Long companyId) {
        return new ArrayList<ProductSerial>();

    }
   public List<ProductSerial> findBySerialNumberAndCompanyId(String serialNumber, Long companyId){
        return new ArrayList<ProductSerial>();
    }



}
