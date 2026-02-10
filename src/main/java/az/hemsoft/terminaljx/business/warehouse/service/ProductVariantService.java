package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.ProductVariant;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductVariantRepsoitory;

import java.util.List;

@Service
public class ProductVariantService {

    private final ProductVariantRepsoitory variantRepsoitory;

    public ProductVariantService() {
        this.variantRepsoitory = new ProductVariantRepsoitory();
    }

    public List<ProductVariant> getByProduct(Long productId, Long companyId) {
        return variantRepsoitory.findAllByProductIdAndCompanyId(productId, companyId);
    }

    public List<ProductVariant> getBySku(String sku, Long companyId) {
        return variantRepsoitory.findBySkuAndCompanyId(sku, companyId);
    }

    public List<ProductVariant> getByBarcode(String barcode, Long companyId) {
        return variantRepsoitory.findByBarcodeAndCompanyId(barcode, companyId);
    }

    public ProductVariant create(ProductVariant productVariant) {
        return variantRepsoitory.save(productVariant);}

   public ProductVariant update(Long id,ProductVariant variantDetail){
        ProductVariant productVariant = variantRepsoitory.findById(id).get();
        productVariant.setCompanyId(variantDetail.getCompanyId());
        productVariant.setSku(variantDetail.getSku());
        productVariant.setProductAttributes(variantDetail.getProductAttributes());
        productVariant.setProduct(variantDetail.getProduct());
        productVariant.setBarcode(variantDetail.getBarcode());
        productVariant.setActive(variantDetail.getActive());
        return variantRepsoitory.save(productVariant);
   }

    public void delete(Long id) {
        variantRepsoitory.delete(id);
    }

}
