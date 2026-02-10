package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.PriceType;
import az.hemsoft.terminaljx.business.warehouse.repository.PriceTypeRepository;

import java.util.List;

@Service
public class PriceTypeService {

    private final PriceTypeRepository typeRepository;


    public PriceTypeService() {
        this.typeRepository = new PriceTypeRepository();
    }

    public List<PriceType> getAll(Long companyId) {
        return typeRepository.findAllByCompanyId(companyId);
    }

    public List<PriceType> getAllByBranch(Long branchId, Long companyId) {
        return typeRepository.findAllByBranchIdAndCompanyId(branchId, companyId);
    }

    public PriceType create(PriceType priceType) {
        return typeRepository.save(priceType);
    }

    public PriceType update(Long id, PriceType typeDetail) {
        PriceType priceType = typeRepository.findById(id).get();
        priceType.setName(priceType.getName());
        priceType.setDescription(typeDetail.getDescription());
        priceType.setCompanyId(typeDetail.getCompanyId());
        priceType.setBranchId(typeDetail.getBranchId());
        return typeRepository.save(priceType);
    }

    public void delete(Long id) {
        typeRepository.delete(id);
    }


}
