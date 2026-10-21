package com.example.emp.service.impl;

import com.example.emp.client.ExternalApiClient;
import com.example.emp.dto.ExternalApiResponse;
import com.example.emp.service.ExternalService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class ExternalServiceImpl implements ExternalService {

    private final ExternalApiClient externalApiClient;

    @Override
    public ExternalApiResponse checkGoogle() {
        return externalApiClient.fetchHomePage();
    }
}
