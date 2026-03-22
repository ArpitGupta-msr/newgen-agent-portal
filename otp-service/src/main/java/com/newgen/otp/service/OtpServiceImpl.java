package com.newgen.otp.service;

import com.newgen.otp.client.AgentServiceClient;
import com.newgen.otp.dto.OtpRequestDTO;
import com.newgen.otp.dto.OtpResponseDTO;
import com.newgen.otp.dto.OtpValidateRequestDTO;
import com.newgen.otp.dto.OtpValidateResponseDTO;
import com.newgen.otp.entity.OtpRecord;
import com.newgen.otp.exception.InvalidOtpException;
import com.newgen.otp.exception.OtpExpiredException;
import com.newgen.otp.exception.OtpNotFoundException;
import com.newgen.otp.exception.OtpResendLimitExceededException;
import com.newgen.otp.repository.OtpRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class OtpServiceImpl implements OtpService {

    private static final int OTP_LENGTH = 6;
    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final int MAX_RESEND_COUNT = 2;
    private static final int RESEND_INTERVAL_MINUTES = 5;

    private final OtpRepository otpRepository;
    private final AgentServiceClient agentServiceClient;
    private final SecureRandom secureRandom = new SecureRandom();

    @Override
    public OtpResponseDTO generateOtp(OtpRequestDTO request) {
        String otpCode = generateRandomOtp();

        OtpRecord otpRecord = OtpRecord.builder()
                .agencyCode(request.getAgencyCode())
                .otpCode(otpCode)
                .resendCount(0)
                .isVerified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        otpRepository.save(otpRecord);
        log.info("OTP generated for agency code: {}. OTP: {}", request.getAgencyCode(), otpCode);

        return OtpResponseDTO.builder()
                .message("OTP has been sent successfully. OTP: " + otpCode)
                .otpSent(true)
                .build();
    }

    @Override
    public OtpValidateResponseDTO validateOtp(OtpValidateRequestDTO request) {
        OtpRecord otpRecord = otpRepository
                .findTopByAgencyCodeOrderByCreatedAtDesc(request.getAgencyCode())
                .orElseThrow(() -> new OtpNotFoundException(
                        "No OTP found for agency code: " + request.getAgencyCode()));

        if (otpRecord.getExpiresAt().isBefore(LocalDateTime.now())) {
            throw new OtpExpiredException("OTP has expired. Please request a new one.");
        }

        if (!otpRecord.getOtpCode().equals(request.getOtpCode())) {
            throw new InvalidOtpException("Incorrect OTP, please try again");
        }

        otpRecord.setIsVerified(true);
        otpRepository.save(otpRecord);

        String agentName = agentServiceClient.getAgentName(request.getAgencyCode());

        return OtpValidateResponseDTO.builder()
                .valid(true)
                .agentName(agentName)
                .message("Hi " + agentName + ". Your verification has been done successfully.")
                .build();
    }

    @Override
    public OtpResponseDTO resendOtp(OtpRequestDTO request) {
        OtpRecord latestOtp = otpRepository
                .findTopByAgencyCodeOrderByCreatedAtDesc(request.getAgencyCode())
                .orElseThrow(() -> new OtpNotFoundException(
                        "No OTP found to resend for agency code: " + request.getAgencyCode()));

        if (latestOtp.getResendCount() >= MAX_RESEND_COUNT) {
            throw new OtpResendLimitExceededException(
                    "Maximum OTP resend limit reached. Please request a new OTP.");
        }

        if (latestOtp.getCreatedAt() != null &&
                latestOtp.getCreatedAt().plusMinutes(RESEND_INTERVAL_MINUTES).isAfter(LocalDateTime.now())) {
            throw new OtpResendLimitExceededException(
                    "Please wait before requesting another OTP resend.");
        }

        String otpCode = generateRandomOtp();

        OtpRecord newOtpRecord = OtpRecord.builder()
                .agencyCode(request.getAgencyCode())
                .otpCode(otpCode)
                .resendCount(latestOtp.getResendCount() + 1)
                .isVerified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .build();

        otpRepository.save(newOtpRecord);
        log.info("OTP resent for agency code: {}. OTP: {}. Resend count: {}",
                request.getAgencyCode(), otpCode, newOtpRecord.getResendCount());

        return OtpResponseDTO.builder()
                .message("OTP has been resent successfully. OTP: " + otpCode)
                .otpSent(true)
                .build();
    }

    private String generateRandomOtp() {
        int otp = 100000 + secureRandom.nextInt(900000);
        return String.valueOf(otp);
    }
}
