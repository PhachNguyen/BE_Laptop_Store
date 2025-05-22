package vn.hoidanit.jobhunter.service;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PathVariable;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.domain.dto.meta;
import vn.hoidanit.jobhunter.domain.dto.resultPaginationDTO;
import vn.hoidanit.jobhunter.domain.response.ProductResponse;
import vn.hoidanit.jobhunter.repository.ProductRepo;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

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
//     Mobile fetch all product
public Page<Product> getAllProducts(Pageable pageable) {
    Page<Product> productPage = productRepo.findAll(pageable);

    String baseUrl = "http://localhost:8080/storage/";

    productPage.getContent().forEach(product -> {
        List<String> fullUrls = product.getImages().stream()
                .map(fileName -> baseUrl + "product-" + product.getId() + "/" + fileName)
                .collect(Collectors.toList());
        product.setImages(fullUrls);
    });

    return productPage;
}

    // Tìm kiếm sản phẩm theo id
    public ProductResponse getProductResponseById(Long id) {
        Product product = productRepo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        String baseUrl = "http://localhost:8080/storage/product-" + product.getId() + "/";
        List<String> fullImageUrls = product.getImages().stream()
                .map(fileName -> baseUrl + fileName)
                .collect(Collectors.toList());

        ProductResponse res = new ProductResponse();
        res.setId(product.getId());
        res.setName(product.getName());
        res.setDescription(product.getDescription());
        res.setPrice(product.getPrice());
        res.setImages(fullImageUrls);
        return res;
    }

    // Hàm update Product
    public Product handleUpdateProduct(Product reqProduct) {
        Product currentProduct = this.productRepo.findById(reqProduct.getId()).orElse(null);
        if (currentProduct != null) {
            currentProduct.setName(reqProduct.getName());
            currentProduct.setDescription(reqProduct.getDescription());
            currentProduct.setPrice(reqProduct.getPrice());
            currentProduct.setStockQuantity(reqProduct.getStockQuantity());
            currentProduct.setBrand(reqProduct.getBrand());
            currentProduct.setWarranty(reqProduct.getWarranty());
            currentProduct.setCpu(reqProduct.getCpu()); // mới
            currentProduct.setRam(reqProduct.getRam()); // mới
            currentProduct.setSsd(reqProduct.getSsd()); // mới
            currentProduct.setCard(reqProduct.getCard()); // mới
            currentProduct.setStatus(reqProduct.getStatus()); // mới
            currentProduct.setImages(reqProduct.getImages());
            // Gọi save để update
            currentProduct = this.productRepo.save(currentProduct);
        }
        return currentProduct;
    }

    // Hàm Delete:
    public void deleteProduct(Long id) {
        productRepo.deleteById(id);
    }
// Phân trang cho Product, Spec: thuộc Spring data JPA
public resultPaginationDTO fetchProducts(Specification<Product> spec, Pageable pageable) {
    // Tìm sản phẩm theo Specification và Pageable
    Page<Product> page = this.productRepo.findAll(spec, pageable);

    // Tạo đối tượng DTO để trả về kết quả
    resultPaginationDTO res = new resultPaginationDTO();

    // Tạo đối tượng meta để chứa thông tin phân trang
    meta meta = new meta();

    // Cập nhật thông tin phân trang trong meta
    meta.setPage(pageable.getPageNumber() + 1); // Trang hiện tại (thêm 1 vì Pageable bắt đầu từ 0)
    meta.setPageSize(pageable.getPageSize()); // Kích thước trang
    meta.setPages(page.getTotalPages()); // Tổng số trang
    meta.setTotal(page.getTotalElements()); // Tổng số phần tử

    // Đặt metadata vào DTO
    res.setMeta(meta);

    // Đặt kết quả (danh sách sản phẩm) vào DTO
    res.setResult(page.getContent());

    // Trả về DTO chứa thông tin phân trang và kết quả
    return res;
}

    public resultPaginationDTO fetchAllProductByBrand(String brand, Pageable pageable) {
        Page<Product> pageProduct = this.productRepo.findByBrandIgnoreCase(brand, pageable);
        resultPaginationDTO rs = new resultPaginationDTO();
        meta mt = new meta();

        mt.setPage(pageable.getPageNumber() + 1); // Trang bắt đầu từ 0, FE thường muốn bắt đầu từ 1
        mt.setPageSize(pageable.getPageSize());
        mt.setPages(pageProduct.getTotalPages());
        mt.setTotal(pageProduct.getTotalElements());

        rs.setMeta(mt);
        rs.setResult(pageProduct.getContent());
        return rs;
    }

}
