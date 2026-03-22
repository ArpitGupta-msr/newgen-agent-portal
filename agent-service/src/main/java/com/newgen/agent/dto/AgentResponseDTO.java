package com.newgen.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AgentResponseDTO {
    private String agencyCode;
    private String name;
    private String role;
    private Boolean isRegistered;
}
