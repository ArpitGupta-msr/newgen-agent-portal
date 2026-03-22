package com.newgen.agent.dto;

import com.newgen.agent.validator.ValidPassword;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetPasswordRequestDTO {

    @NotBlank(message = "Please provide a valid agency code")
    private String agencyCode;

    @NotBlank(message = "Please provide a valid password")
    @ValidPassword
    private String password;

    @NotBlank(message = "Please provide a valid confirm password")
    private String confirmPassword;
}
