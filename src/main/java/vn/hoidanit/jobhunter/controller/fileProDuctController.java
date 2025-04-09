package vn.hoidanit.jobhunter.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import vn.hoidanit.jobhunter.service.FileService;
import vn.hoidanit.jobhunter.util.annotation.ApiMessage;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.FileSystem;
import java.util.List;

@RestController
@RequestMapping("/api/v1")
public class fileProDuctController {
    private  final FileService fileService;
    public fileProDuctController(FileService fileService) {
        this.fileService = fileService;
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


}
