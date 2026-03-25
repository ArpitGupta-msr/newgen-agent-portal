package com.newgen.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AdminAgentResponseDTO {
    private Long id;
    private String agencyCode;
    private String name;
    private String role;
    private Boolean consentGiven;
    private Boolean isRegistered;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
