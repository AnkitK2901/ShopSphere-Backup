package com.shopsphere.api_gateway;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.Bean;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.reactive.CorsWebFilter;
import org.springframework.web.cors.reactive.UrlBasedCorsConfigurationSource;

@SpringBootApplication
@EnableDiscoveryClient
public class ApiGatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(ApiGatewayApplication.class, args);
    }

    @Bean
    public CorsWebFilter corsWebFilter() {
        CorsConfiguration corsConfig = new CorsConfiguration();
        
        // 1. THE FIX: Use OriginPattern("*") instead of specific "localhost"
        // This flawlessly handles 127.0.0.1, localhost, and trailing slashes.
        corsConfig.addAllowedOriginPattern("*"); 
        
        // 2. Allow ALL headers
        corsConfig.addAllowedHeader("*");
        
        // 3. Allow ALL methods (GET, POST, OPTIONS, etc.)
        corsConfig.addAllowedMethod("*");
        
        // 4. Must be true to pass JWT tokens safely
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}