package com.newgen.demo;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Entry point for the Circuit Breaker Demo application.
 *
 * <p>This demo illustrates the <strong>Circuit Breaker</strong> pattern using
 * <a href="https://resilience4j.readme.io/">Resilience4j</a>.
 *
 * <h2>What is a Circuit Breaker?</h2>
 * <p>A circuit breaker is a stability pattern that prevents cascading failures in
 * distributed systems. It wraps a remote call and monitors its outcomes. When
 * failures exceed a configured threshold the circuit "opens" — subsequent calls are
 * short-circuited immediately to a fallback rather than hammering the failing
 * downstream service. After a wait period, the circuit moves to "half-open" and
 * allows a limited number of probe calls through; if those succeed it closes again.
 *
 * <h2>States</h2>
 * <pre>
 *   CLOSED ──(failure rate ≥ threshold)──► OPEN ──(wait 60 s)──► HALF_OPEN
 *     ▲                                                               │
 *     └────────────(probe calls succeed)─────────────────────────────┘
 *                         └──(probe calls fail)──► OPEN
 * </pre>
 *
 * <h2>How to test</h2>
 * <ol>
 *   <li>Start the app: {@code mvn spring-boot:run}</li>
 *   <li>Hit the happy path: {@code GET http://localhost:9090/weather/Mumbai}</li>
 *   <li>Enable failures: {@code POST http://localhost:9090/weather/simulate/fail?enabled=true}</li>
 *   <li>Make ≥10 calls to open the circuit.</li>
 *   <li>Check state: {@code GET http://localhost:9090/weather/circuit/status}</li>
 *   <li>Observe fallback responses without errors propagating to the caller.</li>
 * </ol>
 */
@SpringBootApplication
public class CircuitBreakerDemoApplication {

    public static void main(String[] args) {
        SpringApplication.run(CircuitBreakerDemoApplication.class, args);
    }
}
