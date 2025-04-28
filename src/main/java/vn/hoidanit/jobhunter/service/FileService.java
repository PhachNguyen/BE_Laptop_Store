package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    @Value("${phachnguyen.upload-file.base-uri}")
    private String baseURI;

    public void createDirectory(String folder) {
        File tmpDir = new File(folder);
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectories(tmpDir.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    // ======== HÀM LƯU FILE CHO USER/AVATAR ========
    public String store(MultipartFile file, String folder) throws IOException {
        // Tạo thư mục nếu chưa có
        createDirectory(baseURI + folder);

        String original = file.getOriginalFilename();
        String safeFilename = System.currentTimeMillis() + "-" + (original == null ? "unknown" : sanitizeFilename(original));
        Path path = Paths.get(baseURI + folder, safeFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return safeFilename;
    }

    // ======== HÀM LẤY KÍCH THƯỚC FILE ========
    public long getFileLength(String fileName, String folder) {
        Path path = Paths.get(baseURI + folder, fileName);
        File file = new File(path.toString());
        if (!file.exists() || file.isDirectory()) return 0;
        return file.length();
    }

    // ======== HÀM GET RESOURCE ĐỂ TẢI FILE ========
    public InputStreamResource getResource(String fileName, String folder) throws FileNotFoundException {
        Path path = Paths.get(baseURI + folder, fileName);
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }

    // ======== LƯU FILE CHO PRODUCT (ĐÃ SẴN) ========
    public String storeProDuct(Long productId, MultipartFile file) throws IOException {
        String folder = baseURI + "product-" + productId;
        createDirectory(folder);

        String original = file.getOriginalFilename();
        String safeFilename = System.currentTimeMillis() + "-" + (original == null ? "unknown" : sanitizeFilename(original));
        Path path = Paths.get(folder, safeFilename);

        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return safeFilename;
    }

    private String sanitizeFilename(String filename) {
        return filename.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_");
    }
}


