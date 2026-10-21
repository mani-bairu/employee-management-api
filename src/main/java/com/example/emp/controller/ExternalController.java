package com.example.emp.controller;

import com.example.emp.dto.ExternalApiResponse;
import com.example.emp.service.ExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/external")
@RequiredArgsConstructor
public class ExternalController {

    private final ExternalService externalService;

    @GetMapping("/google")
    public ResponseEntity<ExternalApiResponse> callGoogle() {
        return ResponseEntity.ok(externalService.checkGoogle());
    }
}
