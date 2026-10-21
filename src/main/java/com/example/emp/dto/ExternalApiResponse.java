package com.example.emp.dto;

public record ExternalApiResponse(int statusCode, int contentLength, long responseTimeMs) {
}
