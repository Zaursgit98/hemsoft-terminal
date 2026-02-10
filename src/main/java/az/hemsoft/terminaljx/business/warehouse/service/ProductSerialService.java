package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.enums.SerialStatus;
import az.hemsoft.terminaljx.business.warehouse.model.ProductSerial;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductSerialRepository;

import java.util.ArrayList;
import java.util.List;

@Service
public class ProductSerialService {
    private final ProductSerialRepository serialRepository;

    public ProductSerialService() {
        this.serialRepository = new ProductSerialRepository();
    }

    public List<ProductSerial> getByProduct(Long productId, Long companyId) {
        return serialRepository.findAllByProductIdAndCompanyId(productId, companyId);
    }

    public List<ProductSerial> getByProductAnStatus(Long productId, SerialStatus status, Long companyid) {
        return serialRepository.findAllByProductIdAndStatusAndCompanyId(productId, status, companyid);
    }

    public List<ProductSerial> getByBranchAndStatus(Long branchId, SerialStatus status, Long companyId) {
        return serialRepository.findAllByBranchIdAndStatusAndCompanyId(branchId, status, companyId);
    }

   public List<ProductSerial> findBySerial(String serialNumber, Long companyId){
        return serialRepository.findBySerialNumberAndCompanyId(serialNumber, companyId);
    }


    public ProductSerial create(ProductSerial serial) {
        return serialRepository.save(serial);
    }

    public ProductSerial update(Long id, ProductSerial serialDetail) {
        ProductSerial productSerial = serialRepository.findById(id).get();
        productSerial.setBranchId(serialDetail.getBranchId());
        productSerial.setProduct(serialDetail.getProduct());
        productSerial.setPurchaseDate(serialDetail.getPurchaseDate());
        productSerial.setSoldDate(serialDetail.getSoldDate());
        productSerial.setUnitPrice(serialDetail.getUnitPrice());
        productSerial.setTotalPrice(serialDetail.getTotalPrice());
        productSerial.setCompanyId(serialDetail.getCompanyId());
        productSerial.setCustomerId(serialDetail.getCustomerId());
        productSerial.setSerialNumber(serialDetail.getSerialNumber());
        productSerial.setSerialStatus(serialDetail.getSerialStatus());
        productSerial.setVariant(serialDetail.getVariant());
        productSerial.setWeight(serialDetail.getWeight());
        return serialRepository.save(productSerial);
    }

    public void delete(Long id) {
        serialRepository.delete(id);
    }


}
