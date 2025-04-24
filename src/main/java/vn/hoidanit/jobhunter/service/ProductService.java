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
    public Product getProductById(Long id) {
        Product currentProduct = productRepo.findById(id).get();
        return currentProduct;
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


}
