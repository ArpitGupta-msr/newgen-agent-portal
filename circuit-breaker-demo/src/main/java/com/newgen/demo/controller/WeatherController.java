package com.newgen.demo.controller;

import com.newgen.demo.dto.WeatherResponse;
import com.newgen.demo.service.WeatherService;
import io.github.resilience4j.circuitbreaker.CircuitBreaker;
import io.github.resilience4j.circuitbreaker.CircuitBreakerRegistry;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.LinkedHashMap;
import java.util.Map;

/**
 * REST controller that exposes endpoints to:
 * <ol>
 *   <li><b>Query weather</b> — demonstrates the circuit-breaker in action.</li>
 *   <li><b>Toggle simulations</b> — lets you induce failures or slow calls at
 *       runtime to drive the circuit breaker through its state transitions.</li>
 *   <li><b>Inspect circuit state</b> — returns live Resilience4j metrics so you
 *       can observe CLOSED → OPEN → HALF_OPEN transitions without needing Actuator.</li>
 * </ol>
 *
 * <h2>Typical demo walkthrough</h2>
 * <pre>
 * # 1. Happy path — circuit is CLOSED, source = "live"
 * GET  /weather/Mumbai
 *
 * # 2. Enable failure simulation
 * POST /weather/simulate/fail?enabled=true
 *
 * # 3. Make 10 failing calls to trip the circuit open
 * GET  /weather/Mumbai   (x10)
 *
 * # 4. Verify circuit is now OPEN — failure rate = 100%
 * GET  /weather/circuit/status
 *
 * # 5. Further calls return fallback instantly (circuit is OPEN)
 * GET  /weather/Mumbai   → source = "fallback"
 *
 * # 6. Disable failures and wait 60 s; circuit moves to HALF_OPEN
 * POST /weather/simulate/fail?enabled=false
 * GET  /weather/Mumbai   (after 60 s) → circuit probes succeed → CLOSED
 * </pre>
 */
@Slf4j
@RestController
@RequestMapping("/weather")
@RequiredArgsConstructor
public class WeatherController {

    private final WeatherService weatherService;

    /**
     * Resilience4j registry — holds all circuit-breaker instances created from
     * application.yml configuration. We inject it here to read live metrics
     * for the status endpoint.
     */
    private final CircuitBreakerRegistry circuitBreakerRegistry;

    // -----------------------------------------------------------------------
    // 1. Weather query endpoint
    // -----------------------------------------------------------------------

    /**
     * Retrieves weather for the given city.
     *
     * <p>Internally calls {@link WeatherService#getWeather(String)}, which is
     * wrapped by the "weatherService" circuit breaker. If the circuit is OPEN
     * or the call fails, Resilience4j invokes the fallback automatically — this
     * endpoint always returns HTTP 200 with either live or fallback data.
     *
     * @param city city name (path variable)
     * @return weather response with {@code source} indicating "live" or "fallback"
     */
    @GetMapping("/{city}")
    public ResponseEntity<WeatherResponse> getWeather(@PathVariable String city) {
        log.info("Received weather request for city: {}", city);
        WeatherResponse response = weatherService.getWeather(city);
        return ResponseEntity.ok(response);
    }

    // -----------------------------------------------------------------------
    // 2. Simulation control endpoints
    // -----------------------------------------------------------------------

    /**
     * Toggles the failure simulation in {@link WeatherService}.
     *
     * <p>When {@code enabled=true}, every call to {@code /weather/{city}} throws
     * a RuntimeException. Making 10 such calls (the sliding-window size) with a
     * ≥50% failure rate will trip the circuit to OPEN.
     *
     * @param enabled {@code true} to start simulating failures, {@code false} to stop
     * @return a plain-text message confirming the new simulation state
     */
    @PostMapping("/simulate/fail")
    public ResponseEntity<String> simulateFailure(@RequestParam boolean enabled) {
        weatherService.setSimulateFailure(enabled);
        String message = "Failure simulation: " + (enabled ? "ON" : "OFF");
        log.info(message);
        return ResponseEntity.ok(message);
    }

    /**
     * Toggles the slow-call simulation in {@link WeatherService}.
     *
     * <p>When {@code enabled=true}, every call sleeps for 4 seconds, exceeding the
     * 3-second {@code slow-call-duration-threshold}. After 10 calls with a slow rate
     * ≥50% the circuit will also open due to excessive slow calls.
     *
     * @param enabled {@code true} to start simulating slow calls, {@code false} to stop
     * @return a plain-text message confirming the new simulation state
     */
    @PostMapping("/simulate/slow")
    public ResponseEntity<String> simulateSlow(@RequestParam boolean enabled) {
        weatherService.setSimulateSlow(enabled);
        String message = "Slow call simulation: " + (enabled ? "ON" : "OFF");
        log.info(message);
        return ResponseEntity.ok(message);
    }

    // -----------------------------------------------------------------------
    // 3. Circuit-breaker status endpoint
    // -----------------------------------------------------------------------

    /**
     * Returns live Resilience4j metrics for the "weatherService" circuit breaker.
     *
     * <p>This is the key observability endpoint for the demo. Use it after making
     * calls to watch the metrics change and understand why the circuit opens/closes.
     *
     * <p>Example response when circuit is OPEN after many failures:
     * <pre>
     * {
     *   "state": "OPEN",
     *   "failureRate": "100.0%",
     *   "slowCallRate": "0.0%",
     *   "numberOfBufferedCalls": 10,
     *   "numberOfFailedCalls": 10
     * }
     * </pre>
     *
     * <p>Notes on metrics:
     * <ul>
     *   <li>{@code failureRate} and {@code slowCallRate} show {@code -1.0%} until
     *       the minimum number of calls (10) have been recorded — this is by design.</li>
     *   <li>{@code numberOfBufferedCalls} is the count of calls in the current
     *       sliding window (0–10 for a window size of 10).</li>
     * </ul>
     *
     * @return HTTP 200 with a JSON map of circuit-breaker metrics
     */
    @GetMapping("/circuit/status")
    public ResponseEntity<Map<String, Object>> getCircuitStatus() {
        // Look up the named circuit-breaker instance from the registry.
        // The name "weatherService" must match the instance name in application.yml.
        CircuitBreaker cb = circuitBreakerRegistry.circuitBreaker("weatherService");
        CircuitBreaker.Metrics metrics = cb.getMetrics();

        // LinkedHashMap preserves insertion order, giving a consistent JSON key order.
        Map<String, Object> status = new LinkedHashMap<>();

        // Current state: CLOSED, OPEN, or HALF_OPEN
        status.put("state", cb.getState().toString());

        // Percentage of failed calls in the sliding window.
        // Returns -1.0 until minimum-number-of-calls is reached.
        status.put("failureRate", metrics.getFailureRate() + "%");

        // Percentage of slow calls (duration > slow-call-duration-threshold).
        // Returns -1.0 until minimum-number-of-calls is reached.
        status.put("slowCallRate", metrics.getSlowCallRate() + "%");

        // Total calls recorded in the current sliding window.
        status.put("numberOfBufferedCalls", metrics.getNumberOfBufferedCalls());

        // How many of the buffered calls resulted in a failure/exception.
        status.put("numberOfFailedCalls", metrics.getNumberOfFailedCalls());

        return ResponseEntity.ok(status);
    }
}
