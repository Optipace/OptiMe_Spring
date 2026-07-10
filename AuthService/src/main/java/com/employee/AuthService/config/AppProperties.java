package com.employee.AuthService.config;

import jakarta.annotation.PostConstruct;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConfigurationProperties(prefix = "app")
@Data
@Slf4j
public class AppProperties {

    private Otp otp = new Otp();
//    private Login login = new Login();
//    private Jwt jwt = new Jwt();
//    private Video video = new Video();
//    private Image image = new Image();


    @Data
    public static class Otp {
        private int fixed;
        private boolean bypassEnabled;
    }

//    @Data
//    public static class Login {
//        private String fixedPassword;
//    }

//    @Data
//    public static class Jwt {
//        private String secret;
//        private long expirationAccess;
//        private long expirationRefresh;
//    }

//    @Data
//    public static class Video {
//        private String uploadDir;
//    }

//    @Data
//    public static class Image {
//        private String uploadDir;
//    }

    /**
     * Logs property values safely at startup.
     */
    @PostConstruct
    public void logPropertiesOnStartup() {
        log.info("✅ OTP fixed value: {}", otp.getFixed());
        log.info("✅ OTP bypass enabled: {}", otp.isBypassEnabled());
//        log.info("✅ Login fixed password: {}", mask(login.getFixedPassword()));
//        log.info("✅ JWT secret: {}", mask(jwt.getSecret()));
//        log.info("✅ JWT access expiration (ms): {}", jwt.getExpirationAccess());
//        log.info("✅ JWT refresh expiration (ms): {}", jwt.getExpirationRefresh());
//        log.info("✅ Video upload directory: {}", video.getUploadDir());
//        log.info("✅ Image upload directory: {}", image.getUploadDir());
    }

    /**
     * Masks sensitive values for safe logging.
     */
    private String mask(String value) {
        if (value == null || value.length() < 4) return "****";
        int visible = 4;
        return "*".repeat(value.length() - visible) + value.substring(value.length() - visible);
    }
}

