package com.newgen.otp.client;

import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpMethod;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.Map;

@Component
@RequiredArgsConstructor
@Slf4j
public class AgentServiceClient {

    private final RestTemplate restTemplate;

    @CircuitBreaker(name = "agentService", fallbackMethod = "getAgentNameFallback")
    public String getAgentName(String agencyCode) {
        ResponseEntity<Map<String, Object>> response = restTemplate.exchange(
                "http://agent-service/newgen/agents/{agencyCode}",
                HttpMethod.GET,
                null,
                new ParameterizedTypeReference<>() {},
                agencyCode);

        Map<String, Object> body = response.getBody();
        return body != null ? (String) body.get("name") : "Agent";
    }

    public String getAgentNameFallback(String agencyCode, Throwable throwable) {
        log.error("Circuit breaker fallback for getAgentName. Agency code: {}, Error: {}",
                agencyCode, throwable.getMessage());
        return "Agent";
    }
}
