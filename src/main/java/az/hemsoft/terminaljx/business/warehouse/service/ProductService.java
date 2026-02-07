package az.hemsoft.terminaljx.business.warehouse.service;

import az.hemsoft.terminaljx.business.core.annotation.Service;
import az.hemsoft.terminaljx.business.warehouse.model.Product;
import az.hemsoft.terminaljx.business.warehouse.model.ProductGroup;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductGroupRepository;
import az.hemsoft.terminaljx.business.warehouse.repository.ProductRepository;
import java.util.List;

@Service
public class ProductService {
    private final ProductRepository productRepo;
    private final ProductGroupRepository groupRepo;

    public ProductService() {
        this.productRepo = new ProductRepository();
        this.groupRepo = new ProductGroupRepository();
    }

    // --- Product Methods ---
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }

    public Product getProductById(Integer id) {
        return productRepo.findById(id).orElse(null);
    }

    public Product saveProduct(Product product) {
        return productRepo.save(product);
    }

    public void updateProduct(Product product) {
        productRepo.update(product);
    }

    public void deleteProduct(Integer id) {
        productRepo.delete(id);
    }

    // --- Group Methods ---
    public List<ProductGroup> getAllGroups() {
        return groupRepo.findAll();
    }

    public ProductGroup getGroupById(Integer id) {
        return groupRepo.findById(id).orElse(null);
    }

    public ProductGroup saveGroup(ProductGroup group) {
        return groupRepo.save(group);
    }

    public void updateGroup(ProductGroup group) {
        groupRepo.update(group);
    }

    public void deleteGroup(Integer id) {
        groupRepo.delete(id);
    }
}
