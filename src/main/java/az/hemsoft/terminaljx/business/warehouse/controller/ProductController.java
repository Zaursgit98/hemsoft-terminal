package az.hemsoft.terminaljx.business.warehouse.controller;

import az.hemsoft.terminaljx.business.core.annotation.*;
import az.hemsoft.terminaljx.business.warehouse.model.Product;
import az.hemsoft.terminaljx.business.warehouse.model.ProductGroup;
import az.hemsoft.terminaljx.business.warehouse.service.ProductService;
import java.util.List;

@RestController("/api")
public class ProductController {
    private final ProductService service = new ProductService();

    // --- Product Endpoints ---
    @GetMapping("/products")
    public List<Product> getAllProducts() {
        return service.getAllProducts();
    }

    @GetMapping("/products/{id}")
    public Product getProductById(@PathVariable("id") Integer id) {
        return service.getProductById(id);
    }

    @PostMapping("/products")
    public Product createProduct(@RequestBody Product product) {
        return service.saveProduct(product);
    }

    @PutMapping("/products/{id}")
    public Product updateProduct(@PathVariable("id") Integer id, @RequestBody Product product) {
        product.setId(id);
        service.updateProduct(product);
        return product;
    }

    @DeleteMapping("/products/{id}")
    public void deleteProduct(@PathVariable("id") Integer id) {
        service.deleteProduct(id);
    }

    // --- Product Group Endpoints ---
    @GetMapping("/product-groups")
    public List<ProductGroup> getAllGroups() {
        return service.getAllGroups();
    }

    @GetMapping("/product-groups/{id}")
    public ProductGroup getGroupById(@PathVariable("id") Integer id) {
        return service.getGroupById(id);
    }

    @PostMapping("/product-groups")
    public ProductGroup createGroup(@RequestBody ProductGroup group) {
        return service.saveGroup(group);
    }

    @PutMapping("/product-groups/{id}")
    public ProductGroup updateGroup(@PathVariable("id") Integer id, @RequestBody ProductGroup group) {
        group.setId(id);
        service.updateGroup(group);
        return group;
    }

    @DeleteMapping("/product-groups/{id}")
    public void deleteGroup(@PathVariable("id") Integer id) {
        service.deleteGroup(id);
    }
}
