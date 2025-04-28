package vn.hoidanit.jobhunter.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.InputStreamResource;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileService {
    @Value("${phachnguyen.upload-file.base-uri}")
    private String baseURI;

    // Tạo file
    public void createDirectory(String folder) {
        File tmpDir = new File(folder);
        if (!tmpDir.isDirectory()) {
            try {
                Files.createDirectories(tmpDir.toPath());
                System.out.println(">>> CREATE NEW DIRECTORY SUCCESSFUL, PATH = " + tmpDir.toPath());
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            System.out.println(">>> SKIP MAKING DIRECTORY, ALREADY EXISTS");
        }
    }

    // Lưu trữ file User:
    public String store(MultipartFile file, String folder) throws IOException {
        String finalName = System.currentTimeMillis() + "-" + file.getOriginalFilename();
        Path path = Paths.get(baseURI + folder, finalName);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }
        return finalName;
    }

    // Lưu trữ file Product
    public String storeProDuct(Long productId, MultipartFile file) throws IOException {
        String folder = baseURI + "product-" + productId;
        createDirectory(folder);

        String original = file.getOriginalFilename();
        String safeFilename = System.currentTimeMillis() + "-" +
                (original == null ? "unknown" : original.replaceAll("[^a-zA-Z0-9\\.\\-_]", "_"));

        Path path = Paths.get(folder, safeFilename);
        try (InputStream inputStream = file.getInputStream()) {
            Files.copy(inputStream, path, StandardCopyOption.REPLACE_EXISTING);
        }

        return safeFilename;
    }

    // Check dung lượng file
    public long getFileLength(String fileName, String folder) {
        Path path = Paths.get(baseURI + folder, fileName);
        File tmpDir = new File(path.toString());
        if (!tmpDir.exists() || tmpDir.isDirectory())
            return 0;
        return tmpDir.length();
    }

    // Truy xuất file cho FE
    public InputStreamResource getResource(String fileName, String folder)
            throws FileNotFoundException {
        Path path = Paths.get(baseURI + folder, fileName);
        File file = new File(path.toString());
        return new InputStreamResource(new FileInputStream(file));
    }
}
