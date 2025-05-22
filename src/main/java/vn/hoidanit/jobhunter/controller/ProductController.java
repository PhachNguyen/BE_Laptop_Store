package vn.hoidanit.jobhunter.controller;

import com.turkraft.springfilter.boot.Filter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.dto.meta;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.ProductResponse;
import vn.hoidanit.jobhunter.repository.ProductRepo;
import vn.hoidanit.jobhunter.service.ProductService;

import java.util.List;
import java.util.Map;

@RestController
@RequestMapping("/api/v1")
public class ProductController {
    private  final ProductService productService;


    public ProductController(ProductService productService) {
        this.productService = productService;
    }
//    @GetMapping("/products")
//    public ResponseEntity<resultPaginationDTO> getAllProducts(
//            @Filter Specification<Product> spec,
//            Pageable pageable
//            ) {
//        return  ResponseEntity.status(HttpStatus.OK).body(this.productService.fetchProducts(spec,pageable));
//
//    }
@GetMapping("/products")
public ResponseEntity<?> getProducts(
        @RequestParam(defaultValue = "0") int page,
        @RequestParam(defaultValue = "4") int size
) {
    Pageable pageable = PageRequest.of(page, size);
    Page<Product> productPage = productService.getAllProducts(pageable);

    Map<String, Object> meta = Map.of(
            "page", productPage.getNumber(),
            "pageSize", productPage.getSize(),
            "pages", productPage.getTotalPages(),
            "total", productPage.getTotalElements()
    );

    Map<String, Object> data = Map.of(
            "result", productPage.getContent(),
            "meta", meta
    );

    return ResponseEntity.ok(Map.of(
            "statusCode", 200,
            "message", "GET API SUCCESS",
            "data", data
    ));
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
    public ResponseEntity<ProductResponse> getProductById(@PathVariable Long id) {
        ProductResponse response = productService.getProductResponseById(id);
        return ResponseEntity.ok(response);
    }

//    @GetMapping("/products/brand/{brand}")
//    public ResponseEntity<resultPaginationDTO> getProductsByBrand(
//            @PathVariable String brand,
//            @Filter Specification<Product> spec, // Filter
//            Pageable pageable
//    ) {
//        // Thêm điều kiện brand vào spec nếu chưa có
//        Specification<Product> brandSpec = (root, query, cb) -> cb.equal(root.get("brand"), brand);
//        Specification<Product> finalSpec = spec == null ? brandSpec : spec.and(brandSpec);
//
//        return ResponseEntity
//                .status(HttpStatus.OK)
//                .body(productService.fetchProducts(finalSpec, pageable));
@GetMapping("/products/brand/{brand}")
public ResponseEntity<?> getProductsByBrand(
        @PathVariable String brand,
        @RequestParam(required = false) String cpu,
        @RequestParam(required = false) Integer ram,
        @RequestParam(required = false) Integer price,
        @RequestParam(required = false) String status,
        Pageable pageable) {

    Specification<Product> spec = Specification.where((root, query, cb) -> cb.equal(root.get("brand"), brand));

    // Filter by CPU
    if (cpu != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("cpu"), cpu));
    }

    // Filter by RAM
    if (ram != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("ram"), ram));
    }

    // Filter by Price
    if (price != null) {
        spec = spec.and((root, query, cb) -> cb.lessThanOrEqualTo(root.get("price"), price));
    }

    // Filter by Status
    if (status != null) {
        spec = spec.and((root, query, cb) -> cb.equal(root.get("status"), status));
    }

    return ResponseEntity.ok(productService.fetchProducts(spec, pageable));
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
