package com.newgen.login.client;

import com.newgen.login.dto.LoginResponseDTO;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgentServiceClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "agentService", fallbackMethod = "verifyPasswordFallback")
    public LoginResponseDTO verifyPassword(String agencyCode, String password) {
        Map<String, String> requestBody = Map.of("agencyCode", agencyCode, "password", password);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://agent-service/newgen/agents/verify-password",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {});

        Map<String, Object> body = response.getBody();
        if (body != null && Boolean.TRUE.equals(body.get("valid"))) {
            return LoginResponseDTO.builder()
                    .success(true)
                    .agentName((String) body.get("agentName"))
                    .message("Welcome, " + body.get("agentName") + "!")
                    .build();
        }

        return LoginResponseDTO.builder()
                .success(false)
                .message((String) body.get("message"))
                .build();
    }

    @CircuitBreaker(name = "agentService", fallbackMethod = "verifyMpinFallback")
    public LoginResponseDTO verifyMpin(String agencyCode, String mpin) {
        Map<String, String> requestBody = Map.of("agencyCode", agencyCode, "mpin", mpin);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<Map<String, String>> entity = new HttpEntity<>(requestBody, headers);

        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://agent-service/newgen/agents/verify-mpin",
                HttpMethod.POST,
                entity,
                new ParameterizedTypeReference<>() {});

        Map<String, Object> body = response.getBody();
        if (body != null && Boolean.TRUE.equals(body.get("valid"))) {
            return LoginResponseDTO.builder()
                    .success(true)
                    .agentName((String) body.get("agentName"))
                    .message("Welcome, " + body.get("agentName") + "!")
                    .build();
        }

        return LoginResponseDTO.builder()
                .success(false)
                .message((String) body.get("message"))
                .build();
    }

    public LoginResponseDTO verifyPasswordFallback(String agencyCode, String password, Throwable throwable) {
        log.error("Circuit breaker fallback for verifyPassword. Agency code: {}, Error: {}",
                agencyCode, throwable.getMessage());
        return LoginResponseDTO.builder()
                .success(false)
                .message("Service is temporarily unavailable. Please try again later.")
                .build();
    }

    public LoginResponseDTO verifyMpinFallback(String agencyCode, String mpin, Throwable throwable) {
        log.error("Circuit breaker fallback for verifyMpin. Agency code: {}, Error: {}",
                agencyCode, throwable.getMessage());
        return LoginResponseDTO.builder()
                .success(false)
                .message("Service is temporarily unavailable. Please try again later.")
                .build();
    }
}
