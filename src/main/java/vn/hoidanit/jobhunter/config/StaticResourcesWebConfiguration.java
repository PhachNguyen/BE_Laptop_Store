package vn.hoidanit.jobhunter.config;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.config.annotation.CorsRegistry;

// Cấu hình cho phép truy cập ảnh và file tĩnh từ thư mục ngoài thông qua URL
@Configuration
public class StaticResourcesWebConfiguration implements WebMvcConfigurer {

    @Value("${phachnguyen.upload-file.base-uri}")
    private String baseUrl;

    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        String resourceLocation = "file:///" + baseUrl.replace("\\", "/");
        if (!resourceLocation.endsWith("/")) resourceLocation += "/";
        registry.addResourceHandler("/storage/**")
                .addResourceLocations(resourceLocation);
    }

    @Override
    public void addCorsMappings(CorsRegistry registry) {
        registry.addMapping("/storage/**")
//                .allowedOrigins("http://localhost:5173")
                .allowedOrigins("*") // Cho phép mọi client (mobile, browser)
                .allowedMethods("GET")
                .allowedHeaders("*")
                .allowCredentials(false);  // true nếu bạn dùng cookie
    }
}
