package com.newgen.otp.controller;

import com.newgen.otp.dto.OtpRequestDTO;
import com.newgen.otp.dto.OtpResponseDTO;
import com.newgen.otp.dto.OtpValidateRequestDTO;
import com.newgen.otp.dto.OtpValidateResponseDTO;
import com.newgen.otp.service.OtpService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/newgen/otp")
@RequiredArgsConstructor
public class OtpController {

    private final OtpService otpService;

    @PostMapping("/generate")
    public ResponseEntity<OtpResponseDTO> generateOtp(@Valid @RequestBody OtpRequestDTO request) {
        return new ResponseEntity<>(otpService.generateOtp(request), HttpStatus.CREATED);
    }

    @PostMapping("/validate")
    public ResponseEntity<OtpValidateResponseDTO> validateOtp(
            @Valid @RequestBody OtpValidateRequestDTO request) {
        return ResponseEntity.ok(otpService.validateOtp(request));
    }

    @PostMapping("/resend")
    public ResponseEntity<OtpResponseDTO> resendOtp(@Valid @RequestBody OtpRequestDTO request) {
        return ResponseEntity.ok(otpService.resendOtp(request));
    }
}
