package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.ProductTypeGroup;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductTypeGroupRepository;

import java.util.List;
import java.util.LongSummaryStatistics;

@Service
public class ProductTypeGroupService {

    private ProductTypeGroupRepository groupRepository;

    public ProductTypeGroupService( ){
        this.groupRepository=new ProductTypeGroupRepository();
    }

    public List<ProductTypeGroup>getByCompany(Long companyId){
        return groupRepository.findByCompanyId(companyId);
    }

    public List<ProductTypeGroup>getByCompanyOrderName(Long companyId){
        return groupRepository.findByCompanyIdOrderByNameAsc(companyId);
    }

    public List<ProductTypeGroup>getByCompanyAndBranch(Long companyId,Long branchId){
        return groupRepository.findAllByCompanyIdAndBranchId(companyId, branchId);
    }
    public ProductTypeGroup create(ProductTypeGroup group) {
        return groupRepository.save(group);
    }

    public ProductTypeGroup update(Long id,ProductTypeGroup groupDetail){
        ProductTypeGroup productTypeGroup=groupRepository.findById(id).get();
        productTypeGroup.setName(groupDetail.getName());
        productTypeGroup.setDescription(groupDetail.getDescription());
        productTypeGroup.setCompanyId(groupDetail.getCompanyId());
        productTypeGroup.setBranchIds(groupDetail.getBranchIds());
        return groupRepository.save(productTypeGroup);
    }


    public void delete(Long id) {
        groupRepository.delete(id);
    }


}
