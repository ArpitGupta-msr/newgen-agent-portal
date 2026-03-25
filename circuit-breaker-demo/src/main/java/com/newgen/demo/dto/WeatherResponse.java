package com.newgen.demo.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * DTO representing a weather report for a city.
 *
 * <p>The {@code source} field is intentionally included to make the circuit-breaker
 * behaviour <em>visible</em> in the response body:
 * <ul>
 *   <li>{@code "live"} — the real (simulated) external weather API responded OK.</li>
 *   <li>{@code "fallback"} — the circuit breaker intercepted the call (either because
 *       the circuit is OPEN, or the call threw an exception) and the fallback method
 *       returned this stub response instead.</li>
 * </ul>
 *
 * <p>Lombok annotations:
 * <ul>
 *   <li>{@code @Data} — generates getters, setters, equals, hashCode, toString.</li>
 *   <li>{@code @Builder} — enables the fluent builder pattern used in the service layer.</li>
 *   <li>{@code @AllArgsConstructor} / {@code @NoArgsConstructor} — required by
 *       Jackson for JSON serialisation and by the {@code @Builder} alongside
 *       {@code @NoArgsConstructor}.</li>
 * </ul>
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class WeatherResponse {

    /** Name of the city for which weather data was requested. */
    private String city;

    /**
     * Current temperature, e.g. {@code "28°C"}.
     * Set to {@code "N/A"} in the fallback response.
     */
    private String temperature;

    /**
     * Human-readable weather condition, e.g. {@code "Sunny"}.
     * In fallback this carries a user-friendly unavailability message.
     */
    private String condition;

    /**
     * Relative humidity percentage (0–100).
     * Set to {@code 0} in the fallback response.
     */
    private int humidity;

    /**
     * Indicates where this response originated:
     * <ul>
     *   <li>{@code "live"} — returned by the (simulated) external API.</li>
     *   <li>{@code "fallback"} — returned by the circuit-breaker fallback method.</li>
     * </ul>
     */
    private String source;
}
