package az.hemsoft.terminaljx.business.warehouse.repository;


import az.hemsoft.terminaljx.business.core.annotation.Repository;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.Product;
import az.hemsoft.terminaljx.business.warehouse.model.ProductAttribute;
import org.jetbrains.annotations.NotNull;

import java.util.*;

@Repository
public class ProductAttributeRepository extends BaseRepository<ProductAttribute, Long> {

    public List<ProductAttribute> findAllByCompanyId(Long companyId){return new ArrayList<ProductAttribute>(); }

    public List<ProductAttribute>findAllByNameAndCompanyId(String name,Long companyId){return new ArrayList<ProductAttribute>();};

}
