package az.hemsoft.terminaljx.business.warehouse.repository;

import az.hemsoft.terminaljx.business.core.annotation.Repository;
import az.hemsoft.terminaljx.business.core.repository.BaseRepository;
import az.hemsoft.terminaljx.business.warehouse.model.ProductVariant;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Repository
public class ProductVariantRepsoitory extends BaseRepository<ProductVariant, Long> {

    public List<ProductVariant> findAllByProductIdAndCompanyId(Long productId, Long companyId) {
        return new ArrayList<ProductVariant>();}

    public List<ProductVariant> findBySkuAndCompanyId(String sku, Long companyId) {
        return new ArrayList<ProductVariant>();}

    public List<ProductVariant> findByBarcodeAndCompanyId(String barcode, Long companyId) {
        return new ArrayList<ProductVariant>();}}
