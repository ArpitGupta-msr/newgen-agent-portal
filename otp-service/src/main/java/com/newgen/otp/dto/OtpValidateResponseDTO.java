package com.newgen.otp.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class OtpValidateResponseDTO {
    private boolean valid;
    private String agentName;
    private String message;
}
