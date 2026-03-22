package com.newgen.agent.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class VerifyCredentialResponseDTO {
    private boolean valid;
    private String agentName;
    private String message;
}
