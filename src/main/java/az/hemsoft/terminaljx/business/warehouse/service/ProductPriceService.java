package az.hemsoft.terminaljx.business.warehouse.service;


import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.ProductPrice;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductPriceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ProductPriceService {

   private final ProductPriceRepository priceRepository;

   public ProductPriceService(){
       this.priceRepository=new ProductPriceRepository();}

    public List<ProductPrice>getByProduct(Long productId,Long companyId){
       return priceRepository.findAllByProductIdAndCompanyId(productId,companyId);
    }
  public List<ProductPrice>getByProductAndBranch(Long productId,Long branchId,Long compnayId){
       return priceRepository.findAllByProductIdAndBranchIdAndCompanyId(productId, branchId, compnayId);
  }

    public ProductPrice create(ProductPrice price) {
        return priceRepository.save(price);
    }

    public ProductPrice update(Long id,ProductPrice price){
        ProductPrice productPrice = priceRepository.findById(id).get();
        productPrice.setPrice(price.getPrice());
        productPrice.setPriceType(price.getPriceType());
        productPrice.setBranchId(price.getBranchId());
        productPrice.setCompanyId(price.getCompanyId());
        productPrice.setProduct(price.getProduct());
        return priceRepository.save(productPrice);
    }


    public void delete(Long id) {
        priceRepository.delete(id);
    }

}
