package com.shopsphere.catalog.Controller;

import com.shopsphere.catalog.Entity.CustomOption;
import com.shopsphere.catalog.Service.CustomOptionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/options")
public class CustomOptionController {

    @Autowired
    private CustomOptionService optionService;

    @PostMapping
    public ResponseEntity<CustomOption> createOption(@RequestBody CustomOption option) {
        return ResponseEntity.ok(optionService.saveOption(option));
    }

    @GetMapping
    public ResponseEntity<List<CustomOption>> getAllOptions() {
        return ResponseEntity.ok(optionService.getAllOptions());
    }
}