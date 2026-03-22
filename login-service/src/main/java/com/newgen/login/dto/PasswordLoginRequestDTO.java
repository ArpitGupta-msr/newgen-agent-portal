package com.newgen.login.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PasswordLoginRequestDTO {

    @NotBlank(message = "Please provide a valid agency code")
    private String agencyCode;

    @NotBlank(message = "Please provide a valid password")
    private String password;
}
