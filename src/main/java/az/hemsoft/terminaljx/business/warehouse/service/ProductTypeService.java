package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.ProductType;
import az.hemsoft.terminaljx.business.warehouse.model.ProductVariant;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductTypeRepository;

import javax.swing.plaf.PanelUI;
import java.util.List;

@Service
public class ProductTypeService {

    private ProductTypeRepository typeRepository;

    public ProductTypeService() {
        this.typeRepository = new ProductTypeRepository();
    }

    public List<ProductType> getByCompany(Long companyId) {
        return typeRepository.findByCompanyId(companyId);
    }

    public List<ProductType> getByCompanyOrderName(Long companyId) {
        return typeRepository.findByCompanyIdOrderByNameAsc(companyId);
    }

    public List<ProductType> getByGroup(Long groupId, Long companyId) {
        return typeRepository.findByProductTypeGroupIdAndCompanyId(groupId, companyId);
    }

    public List<ProductType> getByBranch(Long branchId, Long companyId) {
        return typeRepository.findAllByBranchIdAndCompanyId(branchId, companyId);}

    public ProductType create(ProductType type) {
        return typeRepository.save(type);}

   public ProductType update(Long id,ProductType typeDetail){
       ProductType productType=typeRepository.findById(id).get();
       productType.setName(typeDetail.getName());
       productType.setDescription(typeDetail.getDescription());
       productType.setBranchIds(typeDetail.getBranchIds());
       productType.setCompanyId(typeDetail.getCompanyId());
       productType.setProductTypeGroup(typeDetail.getProductTypeGroup());
       return typeRepository.save(productType);}

    public void delete(Long id) {
        typeRepository.delete(id);
    }


}
