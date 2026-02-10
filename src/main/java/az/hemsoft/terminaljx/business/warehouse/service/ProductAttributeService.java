package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.ProductAttribute;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductAttributeRepository;
import org.checkerframework.checker.index.qual.PolyUpperBound;

import java.util.List;

@Service
public class ProductAttributeService {

    private final ProductAttributeRepository attributeRepository;

    public ProductAttributeService() {
        this.attributeRepository = new ProductAttributeRepository();
    }

    public List<ProductAttribute> getAll(Long companyId) {
        return attributeRepository.findAllByCompanyId(companyId);
    }

    public List<ProductAttribute> getByName(String name, Long companyId) {
        return attributeRepository.findAllByNameAndCompanyId(name, companyId);
    }

    public ProductAttribute createAttribute(ProductAttribute productAttribute) {
        return attributeRepository.save(productAttribute);
    }

    public ProductAttribute updateAttribute(Long id, ProductAttribute attribute) {
        ProductAttribute productAttribute = attributeRepository.findById(id).get();
        productAttribute.setName(attribute.getName());
        productAttribute.setValue(attribute.getValue());
        productAttribute.setCompanyId(attribute.getCompanyId());

        return attributeRepository.save(productAttribute);

    }

    public void deleteAttribute(Long id){
        attributeRepository.delete(id);
    }

}
