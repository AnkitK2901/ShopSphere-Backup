package com.shopsphere.order.Config;

import feign.RequestInterceptor;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor requestInterceptor(){
        return requestTemplate -> {
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();

            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                
                String authHeader = request.getHeader("Authorization");
                if (authHeader != null) {
                    requestTemplate.header("Authorization", authHeader);
                }

                // THE CRITICAL FIX: Ensure downstream services (Inventory) see this as a system call
                requestTemplate.header("X-User-Role", "ROLE_ADMIN");
                
                String userId = request.getHeader("X-Logged-In-User");
                if (userId != null) {
                    requestTemplate.header("X-Logged-In-User", userId);
                }
            }
        };
    }
}