package vn.hoidanit.jobhunter.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

// Cấu hình cho phép truy cập ảnh và file tĩnh từ thư mục ngoài thông qua URL
@Configuration
public class StaticResourcesWebConfiguration  implements WebMvcConfigurer {
    // WebMvc: Mở rộng các thíết lập cho Web ( Ex: Ánh xạ tài nguyên tĩnh )
    @Value("${phachnguyen.upload-file.base-uri}") // Biến môi trường
    
    private String baseUrl;
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = "file:///" + baseUrl.replace("\\", "/");
        if (!resourceLocation.endsWith("/")) resourceLocation += "/";
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(resourceLocation);
    }

}
