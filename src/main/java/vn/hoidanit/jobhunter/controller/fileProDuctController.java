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
    @PostMapping("/img")
    public ResponseEntity<?> createProductWithImages(
            @RequestParam("product") String productJson,
            @RequestParam("files") List<MultipartFile> files,
            OutputStream outputStream
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            Product product = mapper.readValue(productJson, Product.class);

            // Lưu sản phẩm trước để lấy ID
            Product savedProduct = productRepo.save(product);

            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = fileService.storeProDuct(savedProduct.getId(), file); // chỉ trả về tên file
                fileNames.add(fileName);
            }
            savedProduct.setImages(fileNames);
            productRepo.save(savedProduct);

            // Khi trả về, chỉ trả về file name (chắc chắn)
            savedProduct.setImages(
                    savedProduct.getImages().stream()
                            .map(path -> {
                                int lastSlash = path.lastIndexOf('/');
                                return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
                            })
                            .collect(Collectors.toList())
            );

            return ResponseEntity.ok(savedProduct);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi tạo sản phẩm: " + e.getMessage());
        }
    }

    // Các API khác giữ nguyên, bạn chỉ cần sửa lại phần setImages như trên khi UPDATE ảnh nếu có

    // Update ảnh cho sản phẩm (nếu cần)
    @PutMapping("/products/{productId}/images")
    @ApiMessage("Cập nhật ảnh sản phẩm")
    public ResponseEntity<?> updateProductImages(
            @PathVariable("productId") Long productId,
            @RequestParam(name = "files", required = false) List<MultipartFile> files
    ) {
        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("Không có ảnh nào được gửi lên.");
        }

        try {
            Product product = productRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

            List<String> fileNames = new ArrayList<>();
            for (MultipartFile file : files) {
                String fileName = fileService.storeProDuct(productId, file);
                fileNames.add(fileName);
            }
            product.setImages(fileNames);

            // Đảm bảo trả về chỉ file name
            product.setImages(
                    product.getImages().stream()
                            .map(path -> {
                                int lastSlash = path.lastIndexOf('/');
                                return lastSlash >= 0 ? path.substring(lastSlash + 1) : path;
                            })
                            .collect(Collectors.toList())
            );

            productRepo.save(product);

            return ResponseEntity.ok(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi cập nhật ảnh: " + e.getMessage());
        }
    }

    // ...Các API khác nếu có (giữ nguyên)
}
