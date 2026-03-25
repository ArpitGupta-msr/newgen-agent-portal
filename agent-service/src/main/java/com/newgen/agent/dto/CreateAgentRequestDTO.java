package com.newgen.agent.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreateAgentRequestDTO {

    @NotBlank(message = "Please provide a valid agency code")
    private String agencyCode;

    @NotBlank(message = "Please provide a valid name")
    private String name;

    @NotNull(message = "Please provide a valid role")
    private String role;
}
