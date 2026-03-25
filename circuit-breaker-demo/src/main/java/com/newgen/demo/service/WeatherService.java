package com.newgen.demo.service;

import com.newgen.demo.dto.WeatherResponse;
import io.github.resilience4j.circuitbreaker.annotation.CircuitBreaker;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.atomic.AtomicBoolean;

/**
 * Simulates an external weather-API client wrapped with a Resilience4j Circuit Breaker.
 *
 * <h2>Simulation flags</h2>
 * <p>Two {@link AtomicBoolean} flags let you toggle failure and slow-call modes at
 * runtime through the {@link com.newgen.demo.controller.WeatherController} endpoints,
 * without restarting the application:
 * <ul>
 *   <li><b>simulateFailure</b> — makes every call throw a {@link RuntimeException},
 *       accumulating failures in the sliding window until the circuit opens.</li>
 *   <li><b>simulateSlow</b> — makes every call sleep for 4 seconds (above the 3-second
 *       slow-call threshold), accumulating slow calls in the sliding window.</li>
 * </ul>
 *
 * <h2>Circuit Breaker configuration (from application.yml)</h2>
 * <ul>
 *   <li>Sliding window: COUNT_BASED, size 10 calls</li>
 *   <li>Minimum calls before evaluation: 10 (the CB ignores the first 9 calls)</li>
 *   <li>Failure rate threshold: 50% → opens after 5 failures in a window of 10</li>
 *   <li>Slow-call threshold: calls taking &gt;3 s count as slow</li>
 *   <li>Wait in OPEN state: 60 seconds before transitioning to HALF_OPEN</li>
 *   <li>Fallback: returns a stub {@link WeatherResponse} — never re-throws</li>
 * </ul>
 */
@Slf4j
@Service
public class WeatherService {

    // -----------------------------------------------------------------------
    // Simulation control flags
    // AtomicBoolean is used instead of a plain boolean to ensure thread-safe
    // reads and writes without synchronisation blocks.
    // -----------------------------------------------------------------------

    /**
     * When {@code true}, {@link #getWeather(String)} throws a RuntimeException
     * on every invocation, simulating a completely unavailable external service.
     */
    private final AtomicBoolean simulateFailure = new AtomicBoolean(false);

    /**
     * When {@code true}, {@link #getWeather(String)} sleeps for 4 seconds before
     * returning, exceeding the 3-second slow-call threshold configured in
     * application.yml and causing those calls to be recorded as "slow".
     */
    private final AtomicBoolean simulateSlow = new AtomicBoolean(false);

    // -----------------------------------------------------------------------
    // Simulation setters (called by the controller)
    // -----------------------------------------------------------------------

    /**
     * Enables or disables the failure simulation.
     *
     * @param enabled {@code true} to make all subsequent calls throw an exception
     */
    public void setSimulateFailure(boolean enabled) {
        simulateFailure.set(enabled);
        log.info("Failure simulation set to: {}", enabled);
    }

    /**
     * Enables or disables the slow-call simulation.
     *
     * @param enabled {@code true} to make all subsequent calls sleep for 4 seconds
     */
    public void setSimulateSlow(boolean enabled) {
        simulateSlow.set(enabled);
        log.info("Slow-call simulation set to: {}", enabled);
    }

    // -----------------------------------------------------------------------
    // Core method — wrapped by the Circuit Breaker
    // -----------------------------------------------------------------------

    /**
     * Fetches weather data for the given city from a (simulated) external API.
     *
     * <p>The {@code @CircuitBreaker} annotation tells Resilience4j to:
     * <ol>
     *   <li>Proxy this method call through the "weatherService" circuit breaker.</li>
     *   <li>Record the outcome (success / failure / slow) in the sliding window.</li>
     *   <li>If the circuit is OPEN, skip this method entirely and invoke the
     *       {@code fallbackMethod} directly.</li>
     *   <li>If this method throws <em>any</em> exception, record it as a failure
     *       and invoke the {@code fallbackMethod}.</li>
     * </ol>
     *
     * <p><b>Important</b>: The fallback method must have the <em>same parameters</em>
     * as this method plus an extra {@link Throwable} parameter as the last argument.
     *
     * @param city name of the city to query
     * @return a {@link WeatherResponse} — either live data or a fallback stub
     */
    @CircuitBreaker(name = "weatherService", fallbackMethod = "getWeatherFallback")
    public WeatherResponse getWeather(String city) {

        log.info("Calling external weather API for city: {}", city);

        // --- Failure simulation ---
        // Throws an exception; Resilience4j catches it, records a failure in the
        // sliding window, and immediately routes to getWeatherFallback().
        if (simulateFailure.get()) {
            log.warn("Simulating failure for city: {}", city);
            throw new RuntimeException("External weather API is down!");
        }

        // --- Slow-call simulation ---
        // Sleeps beyond the 3-second slow-call threshold. Resilience4j records the
        // call as "slow" once the method returns. After enough slow calls the
        // slow-call rate threshold may also trip the circuit open.
        if (simulateSlow.get()) {
            log.warn("Simulating slow call for city: {} (sleeping 4 s)", city);
            try {
                Thread.sleep(4_000); // 4 seconds > 3-second slow-call threshold
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt(); // restore interrupt flag
                log.error("Slow-call sleep interrupted", e);
            }
            // After the sleep, still return a response (counted as a slow call, not a failure)
        }

        // --- Normal (happy-path) response ---
        // Returns realistic dummy data. In a real application this would be an
        // HTTP call to an external service (e.g., OpenWeatherMap API).
        log.info("Returning live weather data for city: {}", city);
        return WeatherResponse.builder()
                .city(city)
                .temperature("28°C")
                .condition("Sunny")
                .humidity(65)
                .source("live")
                .build();
    }

    // -----------------------------------------------------------------------
    // Fallback method
    // -----------------------------------------------------------------------

    /**
     * Fallback invoked by Resilience4j when:
     * <ul>
     *   <li>The circuit is OPEN (calls are short-circuited without executing
     *       {@link #getWeather(String)}).</li>
     *   <li>{@link #getWeather(String)} threw any {@link Throwable}.</li>
     * </ul>
     *
     * <p><b>Key design rule</b>: The fallback must NEVER re-throw the exception.
     * Its job is to return a safe, degraded response so the caller receives
     * a valid object instead of an error. This is what makes the circuit-breaker
     * pattern transparent to consumers.
     *
     * <p>The method signature must match {@link #getWeather(String)} exactly,
     * with an additional {@link Throwable} as the last parameter. Resilience4j
     * uses reflection to find this method by name and signature.
     *
     * @param city name of the city originally requested
     * @param t    the exception that triggered the fallback (may be a
     *             {@code CallNotPermittedException} when the circuit is OPEN)
     * @return a stub {@link WeatherResponse} with {@code source = "fallback"}
     */
    public WeatherResponse getWeatherFallback(String city, Throwable t) {
        // Log the reason for the fallback so it is observable in production logs.
        log.warn("Circuit breaker fallback triggered for city: {}. Reason: {}",
                city, t.getMessage());

        // Return a safe, degraded response — do NOT throw or re-throw.
        return WeatherResponse.builder()
                .city(city)
                .temperature("N/A")
                .condition("Service temporarily unavailable — please try again later")
                .humidity(0)
                .source("fallback")
                .build();
    }
}
