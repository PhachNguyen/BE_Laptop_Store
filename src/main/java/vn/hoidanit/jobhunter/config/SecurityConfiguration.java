package vn.hoidanit.jobhunter.config;

import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.JwtDecoder;
import org.springframework.security.oauth2.jwt.JwtEncoder;
import org.springframework.security.oauth2.jwt.NimbusJwtDecoder;
import org.springframework.security.oauth2.jwt.NimbusJwtEncoder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;
import org.springframework.security.web.SecurityFilterChain;

import com.nimbusds.jose.jwk.source.ImmutableSecret;
import com.nimbusds.jose.util.Base64;

import vn.hoidanit.jobhunter.util.SecurityUtil;

@Configuration
@EnableMethodSecurity(securedEnabled = true) // Enable method-level security
public class SecurityConfiguration {

    @Value("${hoidanit.jwt.base64-secret}")
    private String jwtKey;

    // Password encoder for user passwords
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder(); // Using BCrypt to hash passwords
    }

    // Configure HTTP security for API endpoints
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                .cors(Customizer.withDefaults()) // Allow CORS requests from front-end
                .authorizeHttpRequests(authz -> authz
                        .requestMatchers("/", "/login", "/api/v1/login", "/api/v1/auth/login",
                                "/api/v1/auth/register", "/api/v1/auth/refresh", "/storage/**",
                                "/api/v1/products/**", "/api/v1/categories/**")
                        .permitAll()
                        .requestMatchers("/admin/**").hasAnyRole("ADMIN", "MANAGER") // Only ADMIN can access /admin
                   //     .requestMatchers("/manager/**").hasAnyRole("ADMIN", "MANAGER") // ADMIN and MANAGER can access /manager
                        .requestMatchers("/dashboard/**").hasAnyRole("ADMIN", "MANAGER", "VIEWER") // All roles can access /dashboard
                        .anyRequest().authenticated() // Any other request requires authentication
                )
                .oauth2ResourceServer(oauth2 -> oauth2.jwt(Customizer.withDefaults())) // Enable JWT authentication
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS)); // Use stateless session management

        return http.build();
    }

    // Decode JWT
    @Bean
    public JwtDecoder jwtDecoder() {
        NimbusJwtDecoder jwtDecoder = NimbusJwtDecoder.withSecretKey(getSecretKey())
                .macAlgorithm(SecurityUtil.JWT_ALGORITHM).build();
        return token -> {
            try {
                return jwtDecoder.decode(token);
            } catch (Exception e) {
                System.out.println(">>> JWT error: " + e.getMessage());
                throw e;
            }
        };
    }

    // Encode JWT
    @Bean
    public JwtEncoder jwtEncoder() {
        return new NimbusJwtEncoder(new ImmutableSecret<>(getSecretKey()));
    }

    // Secret key for encoding/decoding JWT
    private SecretKey getSecretKey() {
        byte[] keyBytes = Base64.from(jwtKey).decode();
        return new SecretKeySpec(keyBytes, 0, keyBytes.length, SecurityUtil.JWT_ALGORITHM.getName());
    }

    // JWT authentication converter to assign roles based on the JWT claims
    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {
        JwtGrantedAuthoritiesConverter grantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();
        grantedAuthoritiesConverter.setAuthorityPrefix(""); // Remove default "ROLE_" prefix
        grantedAuthoritiesConverter.setAuthoritiesClaimName("roles"); // Extract roles from "roles" claim in JWT
        JwtAuthenticationConverter jwtAuthenticationConverter = new JwtAuthenticationConverter();
        jwtAuthenticationConverter.setJwtGrantedAuthoritiesConverter(grantedAuthoritiesConverter);
        return jwtAuthenticationConverter;
    }
}
