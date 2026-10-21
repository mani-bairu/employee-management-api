package com.example.emp.client;

import com.example.emp.dto.ExternalApiResponse;
import com.example.emp.exception.ExternalServiceException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.RestClient;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.method;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withServerError;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;

class ExternalApiClientTest {

    private MockRestServiceServer server;
    private ExternalApiClient client;

    @BeforeEach
    void setUp() {
        RestClient.Builder builder = RestClient.builder().baseUrl("https://www.google.com");
        server = MockRestServiceServer.bindTo(builder).build();
        client = new ExternalApiClient(builder.build());
    }

    @Test
    void fetchHomePage_returnsStatusAndLength() {
        server.expect(requestTo("https://www.google.com/"))
                .andExpect(method(HttpMethod.GET))
                .andRespond(withSuccess("<html>ok</html>", MediaType.TEXT_HTML));

        ExternalApiResponse response = client.fetchHomePage();

        assertThat(response.statusCode()).isEqualTo(200);
        assertThat(response.contentLength()).isEqualTo("<html>ok</html>".length());
        server.verify();
    }

    @Test
    void fetchHomePage_wrapsFailureInExternalServiceException() {
        server.expect(requestTo("https://www.google.com/")).andRespond(withServerError());

        assertThatThrownBy(() -> client.fetchHomePage())
                .isInstanceOf(ExternalServiceException.class);
    }
}
