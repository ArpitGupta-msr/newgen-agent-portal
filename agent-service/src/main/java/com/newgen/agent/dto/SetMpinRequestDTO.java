package com.newgen.agent.dto;

import com.newgen.agent.validator.ValidMpin;
import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SetMpinRequestDTO {

    @NotBlank(message = "Please provide a valid agency code")
    private String agencyCode;

    @NotBlank(message = "Please provide a valid mpin")
    @ValidMpin
    private String mpin;

    @NotBlank(message = "Please provide a valid confirm mpin")
    private String confirmMpin;
}
