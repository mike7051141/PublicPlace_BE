package com.springboot.publicplace.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;
import org.springframework.web.filter.CorsFilter;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.Arrays;

@Configuration
public class WebConfig implements WebMvcConfigurer {

    @Bean
    public CorsFilter corsFilter() {
        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        CorsConfiguration config = new CorsConfiguration();
        config.setAllowCredentials(true);
        config.setAllowedOrigins(Arrays.asList("http://localhost:3000")); // 허용할 도메인 설정
        config.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS")); // 허용할 HTTP 메서드 설정
        config.setAllowedHeaders(Arrays.asList("*")); // 허용할 헤더 설정

        // Swagger 관련 경로에 대해 CORS 설정을 별도로 추가
        CorsConfiguration swaggerConfig = new CorsConfiguration();
        swaggerConfig.setAllowCredentials(true);
        swaggerConfig.setAllowedOrigins(Arrays.asList("http://localhost:3000"));
        swaggerConfig.setAllowedMethods(Arrays.asList("GET", "POST", "PUT", "DELETE", "OPTIONS"));
        swaggerConfig.setAllowedHeaders(Arrays.asList("*"));

        source.registerCorsConfiguration("/**", config); // 모든 경로에 대한 기본 CORS 설정
        source.registerCorsConfiguration("/swagger-ui/**", swaggerConfig); // Swagger UI 관련 경로에 대한 CORS 설정

        return new CorsFilter(source);
    }
}
