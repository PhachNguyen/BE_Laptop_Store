package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.dto.meta;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.repository.ProductRepo;
import vn.hoidanit.jobhunter.service.ProductService;

import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private  final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }
    @GetMapping("/products")
    public ResponseEntity<resultPaginationDTO> getAllProducts(
            @Filter Specification<Product> spec,
            Pageable pageable
            ) {
        return  ResponseEntity.status(HttpStatus.OK).body(this.productService.fetchProducts(spec,pageable));

    }
    @PostMapping("/products")
    public ResponseEntity<Product> createProduct(@RequestBody Product product) {
        Product created = productService.createProduct(product);
        return ResponseEntity.ok(created);
    }
    // hàm update
    @PutMapping("products")
    public ResponseEntity<Product> updateProduct(@RequestBody Product reqProduct) {
        Product updated = productService.handleUpdateProduct(reqProduct);
        if (updated != null) {
            return ResponseEntity.ok(updated);
        }
        return ResponseEntity.notFound().build();
    }
    // Hàm Delete
    @DeleteMapping("/products/{id}")
    public ResponseEntity<Void> deleteProduct(@PathVariable Long id) {
        productService.deleteProduct(id);
        return ResponseEntity.noContent().build();
    }
// Hàm fetch theo sản phẩm

@GetMapping("/products/{id}")
public ResponseEntity<Product> getProductById(@PathVariable Long id) {
    Product product = productService.getProductById(id);
    if (product != null) {
        return ResponseEntity.ok(product);
    } else {
        return ResponseEntity.notFound().build();
    }
}
    @GetMapping("/products/brand/{brand}")
    public ResponseEntity<resultPaginationDTO> getProductsByBrand(
            @PathVariable String brand,
            @Filter Specification<Product> spec, // Filter
            Pageable pageable
    ) {
        // Thêm điều kiện brand vào spec nếu chưa có
        Specification<Product> brandSpec = (root, query, cb) -> cb.equal(root.get("brand"), brand);
        Specification<Product> finalSpec = spec == null ? brandSpec : spec.and(brandSpec);

        return ResponseEntity
                .status(HttpStatus.OK)
                .body(productService.fetchProducts(finalSpec, pageable));
    }

//     Test API
//    @GetMapping("/products/brand")
//    public Page<?> getProductsByBrand(Pageable pageable) {
//        // In ra thông tin để kiểm tra
//        System.out.println("Page number: " + pageable.getPageNumber());
//        System.out.println("Page size: " + pageable.getPageSize());
//        return null; // hoặc return Page.empty();
//    }






}
