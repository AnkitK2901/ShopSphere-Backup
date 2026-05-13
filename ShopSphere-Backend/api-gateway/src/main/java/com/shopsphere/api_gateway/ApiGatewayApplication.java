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
        
        // 1. THE FIX: AllowedOriginPattern allows credentials with wildcards
        corsConfig.addAllowedOriginPattern("*"); 
        
        // 2. Allow ALL headers (Authorization, Content-Type, X-User-Role, etc.)
        corsConfig.addAllowedHeader("*");
        
        // 3. Allow ALL methods (GET, POST, PUT, DELETE, PATCH, OPTIONS)
        corsConfig.addAllowedMethod("*");
        
        // 4. Required to allow JWT Bearer tokens to pass through CORS
        corsConfig.setAllowCredentials(true);
        corsConfig.setMaxAge(3600L);

        UrlBasedCorsConfigurationSource source = new UrlBasedCorsConfigurationSource();
        source.registerCorsConfiguration("/**", corsConfig);

        return new CorsWebFilter(source);
    }
}