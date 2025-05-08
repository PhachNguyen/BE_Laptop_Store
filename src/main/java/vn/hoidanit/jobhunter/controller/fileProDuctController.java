package vn.hoidanit.jobhunter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.jobhunter.domain.Category;
import vn.hoidanit.jobhunter.domain.Product;
import vn.hoidanit.jobhunter.repository.CategoryRepository;
import vn.hoidanit.jobhunter.repository.ProductRepo;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/api/v1")
public class fileProDuctController {
    private final FileService fileService;
    private final ProductRepo productRepo;
    private final CategoryRepository categoryRepository;

    public fileProDuctController(FileService fileService, ProductRepo productRepo, CategoryRepository categoryRepository) {
        this.fileService = fileService;
        this.productRepo = productRepo;
        this.categoryRepository = categoryRepository;
    }

    // Tạo sản phẩm và upload ảnh
    @PostMapping("/products/img")
    public ResponseEntity<?> createProductWithImages(
            @RequestParam("product") String productJson,
            @RequestParam("files") List<MultipartFile> files
    ) {
        try {
            // Deserialize productJson thành đối tượng Product
            ObjectMapper mapper = new ObjectMapper();
            Product product = mapper.readValue(productJson, Product.class);

            // Lưu sản phẩm vào cơ sở dữ liệu
            Product savedProduct = productRepo.save(product);

            // Lưu ảnh vào hệ thống
            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = fileService.storeProDuct(savedProduct.getId(), file); // Chỉ trả về tên file
                fileNames.add(fileName);
            }
            savedProduct.setImages(fileNames);  // Cập nhật ảnh cho sản phẩm
            productRepo.save(savedProduct);  // Lưu lại sản phẩm

            return ResponseEntity.ok(savedProduct);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi tạo sản phẩm: " + e.getMessage());
        }
    }

    // Các API khác giữ nguyên, bạn chỉ cần sửa lại phần setImages như trên khi UPDATE ảnh nếu có

    // Update ảnh cho sản phẩm (nếu cần)
    @PutMapping("/products/{productId}/images")
    public ResponseEntity<?> updateProductImages(
            @PathVariable("productId") Long productId,
            @RequestParam(name = "files", required = false) List<MultipartFile> files,
            @RequestParam("product") String productJson
    ) {
        try {
            // Deserialize productJson thành đối tượng Product
            ObjectMapper mapper = new ObjectMapper();
            Product updatedProduct = mapper.readValue(productJson, Product.class);

            // Tìm sản phẩm cũ trong DB
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

            // Cập nhật thông tin sản phẩm
            product.setName(updatedProduct.getName());
            product.setPrice(updatedProduct.getPrice());
            product.setDescription(updatedProduct.getDescription());
            product.setStockQuantity(updatedProduct.getStockQuantity());
            product.setCategory(updatedProduct.getCategory());  // Cập nhật danh mục

            // Lưu lại sản phẩm mới
            productRepo.save(product);

            // Xử lý ảnh
            if (files != null && !files.isEmpty()) {
                List<String> fileNames = new ArrayList<>();
                for (MultipartFile file : files) {
                    String fileName = fileService.storeProDuct(productId, file);
                    fileNames.add(fileName);
                }
                product.setImages(fileNames);
                productRepo.save(product);
            }

            return ResponseEntity.ok(product);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
    }



}
