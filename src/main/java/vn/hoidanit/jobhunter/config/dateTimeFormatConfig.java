package vn.hoidanit.jobhunter.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.format.datetime.standard.DateTimeFormatterRegistrar;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

@Configuration
public class dateTimeFormatConfig implements WebMvcConfigurer {
    @Override
    public void addFormatters(FormatterRegistry registry) {
        DateTimeFormatterRegistrar registrar = new DateTimeFormatterRegistrar();
        registrar.setUseIsoFormat(true);
        registrar.registerFormatters(registry);
    }

    // Hàm chuyển Instant sang LocalDateTime với múi giờ Việt Nam
    public LocalDateTime convertInstantToLocalDateTime(Instant instant) {
        ZonedDateTime vietnamTime = instant.atZone(ZoneId.of("Asia/Ho_Chi_Minh")); // Chuyển sang múi giờ Việt Nam
        return vietnamTime.toLocalDateTime(); // Chuyển thành LocalDateTime
    }
}
