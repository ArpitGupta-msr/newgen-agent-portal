package com.newgen.otp.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OtpRequestDTO {

    @NotBlank(message = "Please provide a valid agency code")
    private String agencyCode;
}
