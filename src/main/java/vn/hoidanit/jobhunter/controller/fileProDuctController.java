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
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.util.ArrayList;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class fileProDuctController {
    private  final FileService fileService;
    private final ProductRepo ProductRepo;
    private final CategoryRepository categoryRepository;

    public fileProDuctController(FileService fileService, ProductRepo ProductRepo, CategoryRepository categoryRepository) {
        this.fileService = fileService;
        this.ProductRepo = ProductRepo;
        this.categoryRepository = categoryRepository;
    }
    @PostMapping("/files/{productId}")
    @ApiMessage("Upload multiple images for product")
    public ResponseEntity<?> uploadProductImages(
            @PathVariable("productId") Long productId,
            @RequestParam("files") List<MultipartFile> files) {

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("No files uploaded.");
        }

        for (MultipartFile file : files) {
            // Xử lý từng file, ví dụ lưu vào thư mục hoặc database
            try {
                fileService.storeProDuct(productId, file);
            } catch (URISyntaxException e) {
                throw new RuntimeException(e);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        }

        return ResponseEntity.ok("Upload thành công " + files.size() + " ảnh.");
    }

// Lưu File and create product
@PostMapping("/img")
public ResponseEntity<?> createProductWithImages(
        @RequestParam("product") String productJson,
        @RequestParam("files") List<MultipartFile> files,
        OutputStream outputStream) {
    try {
        System.out.println(" Nhận productJson từ FE: " + productJson); // in chuỗi JSON

        ObjectMapper mapper = new ObjectMapper();
        Product product = mapper.readValue(productJson, Product.class);

        System.out.println("✅ Parse thành công Product: " + product.getName());

        // Save product to DB
        Product savedProduct = ProductRepo.save(product);

        List<String> imageUrls = new ArrayList<>();
        for (MultipartFile file : files) {
            String imageUrl = fileService.storeProDuct(savedProduct.getId(), file);
            imageUrls.add(imageUrl);
        }

        savedProduct.setImages(imageUrls);
        ProductRepo.save(savedProduct);

        return ResponseEntity.ok(savedProduct);

    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(500).body(" Lỗi tạo sản phẩm: " + e.getMessage());
    }
}
// Update file
    @PutMapping("/products/{productId}/images")
    @ApiMessage("Cập nhật ảnh sản phẩm")
    public ResponseEntity<?> updateProductImages(
            @PathVariable("productId") Long productId,
            @RequestParam(name = "files", required = false) List<MultipartFile> files )
    {

        if (files == null || files.isEmpty()) {
            return ResponseEntity.badRequest().body("Không có ảnh nào được gửi lên.");
        }

        try {
            // Kiểm tra sản phẩm tồn tại
            Product product = ProductRepo.findById(productId)
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm với ID: " + productId));

            // Xoá ảnh cũ nếu cần (tuỳ theo logic bạn muốn)
            // fileService.clearImagesOfProduct(productId); // nếu có hỗ trợ

            // Lưu ảnh mới
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = fileService.storeProDuct(productId, file);
                imageUrls.add(imageUrl);
            }

            // Cập nhật ảnh mới vào product
            product.setImages(imageUrls);
            ProductRepo.save(product);

            return ResponseEntity.ok(product);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi cập nhật ảnh: " + e.getMessage());
        }
    }
    @PostMapping("/products/images")
    public ResponseEntity<?> updateProductWithImages(
            @RequestParam("product") String productJson,
            @RequestParam(value = "files", required = false) List<MultipartFile> files
    ) {
        try {
            ObjectMapper mapper = new ObjectMapper().findAndRegisterModules();
            Product updateData = mapper.readValue(productJson, Product.class);

            Product existing = ProductRepo.findById(updateData.getId())
                    .orElseThrow(() -> new RuntimeException("Không tìm thấy sản phẩm"));

            // Cập nhật các trường cơ bản
            existing.setName(updateData.getName());
            existing.setPrice(updateData.getPrice());
            existing.setDescription(updateData.getDescription());
            existing.setStockQuantity(updateData.getStockQuantity());
            existing.setBrand(updateData.getBrand());
            existing.setWarranty(updateData.getWarranty());

            // Gán lại category nếu có
            if (updateData.getCategory() != null) {
                Category category = categoryRepository.findById(updateData.getCategory().getId())
                        .orElseThrow(() -> new RuntimeException("Không tìm thấy danh mục"));
                existing.setCategory(category);
            }

            // ✅ Nếu có ảnh mới → ghi đè ảnh
            if (files != null && !files.isEmpty()) {
                List<String> imageUrls = new ArrayList<>();
                for (MultipartFile file : files) {
                    String imageUrl = fileService.storeProDuct(existing.getId(), file);
                    imageUrls.add(imageUrl);
                }
                existing.setImages(imageUrls); // ghi đè
            }
            // ❌ Không có ảnh mới → giữ nguyên ảnh cũ (không làm gì)

            ProductRepo.save(existing);
            return ResponseEntity.ok(existing);
        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body("Lỗi khi cập nhật sản phẩm: " + e.getMessage());
        }
    }



}
