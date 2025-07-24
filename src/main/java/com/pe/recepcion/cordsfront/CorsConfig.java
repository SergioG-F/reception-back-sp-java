package com.pe.recepcion.cordsfront;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class CorsConfig {
    @Value("${frontend.url}")
    private String frontendUrl;
    @Bean
    public WebMvcConfigurer corsConfigurer() {
        return new WebMvcConfigurer() {
            @Override
            public void addCorsMappings(CorsRegistry registry) {
                // 🟢 Públicos (sin sesión)
                registry.addMapping("/api/public/**")
                        .allowedOrigins(frontendUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);

                registry.addMapping("/api/auth/**")
                        .allowedOrigins(frontendUrl)
                        .allowedMethods("POST", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(false);// 🔥 importante si usas cookies o auth con withCredentials
// true solo si usas cookies/login

                // Rutas protegidas para el ADMIN
                registry.addMapping("/api/admin/**")
                        .allowedOrigins(frontendUrl)
                        .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                // Rutas protegidas para el RECEPCIONISTA
                registry.addMapping("/api/recepcionista/**")
                        .allowedOrigins(frontendUrl)
                        .allowedMethods("GET", "POST", "PUT", "OPTIONS")
                        .allowedHeaders("*")
                        .allowCredentials(true);

                // 🟡 Render y UptimeRobot (solo GET para evitar conflicto)
                registry.addMapping("/ping")
                        .allowedOrigins("*")
                        .allowedMethods("GET")
                        .allowedHeaders("*")
                        .allowCredentials(false);

                // 🟣 Fallback general solo GET (para pings, no afecta POST)
                registry.addMapping("/**")
                        .allowedOrigins("*")
                        .allowedMethods("GET")
                        .allowedHeaders("*")
                        .allowCredentials(false);
            }

        };
    }
}