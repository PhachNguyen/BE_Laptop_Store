package vn.hoidanit.jobhunter.service;

import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.repository.ProductRepo;

import java.util.List;
import java.util.Optional;

@Service
public class ProductService {
    private  final ProductRepo productRepo;
    public ProductService(ProductRepo productRepo) {
        this.productRepo = productRepo;
    }
    public List<Product> getAllProducts() {
        return productRepo.findAll();
    }
    public Product createProduct(Product product) {
        return productRepo.save(product);
    }
    // Tìm kiếm sản phẩm theo id
    public Optional<Product> getProductById(Long id) {
        return productRepo.findById(id);
    }
    // Hàm update Product
    public Product handleUpdateProduct(Product reqProduct) {
        Product currentProduct = this.productRepo.findById(reqProduct.getId()).orElse(null);

        if (currentProduct != null) {
            currentProduct.setName(reqProduct.getName());
            currentProduct.setDescription(reqProduct.getDescription());
            currentProduct.setPrice(reqProduct.getPrice());
            currentProduct.setStockQuantity(reqProduct.getStockQuantity());
            // Gọi save để update
            currentProduct = this.productRepo.save(currentProduct);
        }
        return currentProduct;
    }
    // Hàm Delete:
    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }

}
