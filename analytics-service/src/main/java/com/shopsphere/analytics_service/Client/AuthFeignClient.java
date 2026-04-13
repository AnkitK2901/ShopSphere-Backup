package com.shopsphere.analytics_service.Client;

import com.shopsphere.analytics_service.Config.FeignConfig;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

@FeignClient(name = "auth-service", configuration = FeignConfig.class)
public interface AuthFeignClient {

    record UserResponse(
            Long id,
            String userName,
            String email
    ) {}

    // Calls the new GET /api/users/id/{id} endpoint added to auth-service's UserController
    @GetMapping("/api/users/id/{userId}")
    UserResponse getUserById(@PathVariable("userId") Long userId);
}
