package com.newgen.otp.service;

import com.newgen.otp.dto.OtpRequestDTO;
import com.newgen.otp.dto.OtpResponseDTO;
import com.newgen.otp.dto.OtpValidateRequestDTO;
import com.newgen.otp.dto.OtpValidateResponseDTO;

public interface OtpService {

    OtpResponseDTO generateOtp(OtpRequestDTO request);

    OtpValidateResponseDTO validateOtp(OtpValidateRequestDTO request);

    OtpResponseDTO resendOtp(OtpRequestDTO request);
}
