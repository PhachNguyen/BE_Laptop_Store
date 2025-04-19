package vn.hoidanit.jobhunter.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.jobhunter.domain.Product;
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
    public fileProDuctController(FileService fileService, ProductRepo ProductRepo) {
        this.fileService = fileService;
        this.ProductRepo = ProductRepo;
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
    @ApiMessage("Tạo mới sản phẩm kèm ảnh")
//    public String t(){
//        return "Success";
//    }
    public ResponseEntity<?> createProductWithImages(
            @RequestParam("product") String productJson,
            @RequestParam("files") List<MultipartFile> files,
            OutputStream outputStream) {
        try {
            // Parse JSON -> Product
            ObjectMapper mapper = new ObjectMapper();
            Product product = mapper.readValue(productJson, Product.class);
            System.out.println(product);
            // Lưu product ban đầu để lấy ID
            Product savedProduct = ProductRepo.save(product);

            // Lưu từng file vào thư mục product-{id}
            List<String> imageUrls = new ArrayList<>();
            for (MultipartFile file : files) {
                String imageUrl = fileService.storeProDuct(savedProduct.getId(), file);
                imageUrls.add(imageUrl); // Ví dụ: /storage/product-5/abc.jpg
            }

            // Gán ảnh và lưu lại
            savedProduct.setImages(imageUrls);
            ProductRepo.save(savedProduct);

            return ResponseEntity.ok(savedProduct);

        } catch (Exception e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(" Lỗi tạo sản phẩm: " + e.getMessage());
        }
    }
}
