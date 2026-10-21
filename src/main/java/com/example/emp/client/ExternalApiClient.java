package com.example.emp.client;

import com.example.emp.dto.ExternalApiResponse;
import com.example.emp.exception.ExternalServiceException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExternalApiClient {

    private final RestClient externalRestClient;

    public ExternalApiResponse fetchHomePage() {
        long start = System.currentTimeMillis();
        try {
            ResponseEntity<String> response = externalRestClient.get()
                    .uri("/")
                    .retrieve()
                    .toEntity(String.class);

            String body = response.getBody();
            return new ExternalApiResponse(
                    response.getStatusCode().value(),
                    body == null ? 0 : body.length(),
                    System.currentTimeMillis() - start);
        } catch (RestClientException ex) {
            log.error("Failed to call external API: {}", ex.getMessage());
            throw new ExternalServiceException("External service is unavailable", ex);
        }
    }
}
