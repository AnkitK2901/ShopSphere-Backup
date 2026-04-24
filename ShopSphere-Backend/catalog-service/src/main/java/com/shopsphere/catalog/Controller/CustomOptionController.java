package com.shopsphere.catalog.Controller;

import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Service.CustomOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/api/options")
@Slf4j
public class CustomOptionController {
    
    @Autowired
    private CustomOptionService optionService;

    @PostMapping
    public ResponseEntity<?> create(
            @RequestBody CustomOption option,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
        
        if (!"ROLE_ADMIN".equals(role) && !"ROLE_SELLER".equals(role)) {
            log.warn("Access Denied for role: {}", role);
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: You do not have permission to create options.");
        }
        
        CustomOption savedOption = optionService.saveOption(option);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedOption);
    }

    @GetMapping
    public ResponseEntity<List<CustomOption>> getAll() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(
            @PathVariable Long id,
            @RequestHeader(value = "X-User-Role", defaultValue = "UNKNOWN") String role) {
            
        if (!"ROLE_ADMIN".equals(role) && !"ROLE_SELLER".equals(role)) {
            return ResponseEntity.status(HttpStatus.FORBIDDEN)
                    .body("Access Denied: Only Admins and Sellers can delete options.");
        }

        optionService.deleteOption(id);
        return ResponseEntity.ok("Option deleted successfully");
    }
}