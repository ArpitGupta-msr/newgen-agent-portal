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
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDateTime;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class OtpServiceImplTest {

    @Mock
    private OtpRepository otpRepository;

    @Mock
    private AgentServiceClient agentServiceClient;

    @InjectMocks
    private OtpServiceImpl otpService;

    @Test
    void testGenerateOtp_Success() {
        OtpRequestDTO request = new OtpRequestDTO("AG001");
        when(otpRepository.save(any(OtpRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OtpResponseDTO response = otpService.generateOtp(request);

        assertTrue(response.isOtpSent());
        assertNotNull(response.getMessage());
        verify(otpRepository).save(any(OtpRecord.class));
    }

    @Test
    void testValidateOtp_Success() {
        OtpRecord otpRecord = OtpRecord.builder()
                .agencyCode("AG001")
                .otpCode("123456")
                .isVerified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        OtpValidateRequestDTO request = new OtpValidateRequestDTO("AG001", "123456");
        when(otpRepository.findTopByAgencyCodeOrderByCreatedAtDesc("AG001"))
                .thenReturn(Optional.of(otpRecord));
        when(otpRepository.save(any(OtpRecord.class))).thenReturn(otpRecord);
        when(agentServiceClient.getAgentName("AG001")).thenReturn("Rajesh Kumar");

        OtpValidateResponseDTO response = otpService.validateOtp(request);

        assertTrue(response.isValid());
        assertEquals("Rajesh Kumar", response.getAgentName());
        assertEquals("Hi Rajesh Kumar. Your verification has been done successfully.", response.getMessage());
    }

    @Test
    void testValidateOtp_Incorrect() {
        OtpRecord otpRecord = OtpRecord.builder()
                .agencyCode("AG001")
                .otpCode("123456")
                .isVerified(false)
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        OtpValidateRequestDTO request = new OtpValidateRequestDTO("AG001", "000000");
        when(otpRepository.findTopByAgencyCodeOrderByCreatedAtDesc("AG001"))
                .thenReturn(Optional.of(otpRecord));

        assertThrows(InvalidOtpException.class, () -> otpService.validateOtp(request));
    }

    @Test
    void testValidateOtp_Expired() {
        OtpRecord otpRecord = OtpRecord.builder()
                .agencyCode("AG001")
                .otpCode("123456")
                .isVerified(false)
                .expiresAt(LocalDateTime.now().minusMinutes(1))
                .build();

        OtpValidateRequestDTO request = new OtpValidateRequestDTO("AG001", "123456");
        when(otpRepository.findTopByAgencyCodeOrderByCreatedAtDesc("AG001"))
                .thenReturn(Optional.of(otpRecord));

        assertThrows(OtpExpiredException.class, () -> otpService.validateOtp(request));
    }

    @Test
    void testValidateOtp_NotFound() {
        OtpValidateRequestDTO request = new OtpValidateRequestDTO("AG001", "123456");
        when(otpRepository.findTopByAgencyCodeOrderByCreatedAtDesc("AG001"))
                .thenReturn(Optional.empty());

        assertThrows(OtpNotFoundException.class, () -> otpService.validateOtp(request));
    }

    @Test
    void testResendOtp_Success() {
        OtpRecord existingOtp = OtpRecord.builder()
                .agencyCode("AG001")
                .otpCode("123456")
                .resendCount(0)
                .isVerified(false)
                .createdAt(LocalDateTime.now().minusMinutes(6))
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        OtpRequestDTO request = new OtpRequestDTO("AG001");
        when(otpRepository.findTopByAgencyCodeOrderByCreatedAtDesc("AG001"))
                .thenReturn(Optional.of(existingOtp));
        when(otpRepository.save(any(OtpRecord.class))).thenAnswer(invocation -> invocation.getArgument(0));

        OtpResponseDTO response = otpService.resendOtp(request);

        assertTrue(response.isOtpSent());
        verify(otpRepository).save(any(OtpRecord.class));
    }

    @Test
    void testResendOtp_LimitExceeded() {
        OtpRecord existingOtp = OtpRecord.builder()
                .agencyCode("AG001")
                .otpCode("123456")
                .resendCount(2)
                .isVerified(false)
                .createdAt(LocalDateTime.now().minusMinutes(6))
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        OtpRequestDTO request = new OtpRequestDTO("AG001");
        when(otpRepository.findTopByAgencyCodeOrderByCreatedAtDesc("AG001"))
                .thenReturn(Optional.of(existingOtp));

        assertThrows(OtpResendLimitExceededException.class, () -> otpService.resendOtp(request));
    }

    @Test
    void testResendOtp_IntervalNotElapsed() {
        OtpRecord existingOtp = OtpRecord.builder()
                .agencyCode("AG001")
                .otpCode("123456")
                .resendCount(1)
                .isVerified(false)
                .createdAt(LocalDateTime.now().minusMinutes(1))
                .expiresAt(LocalDateTime.now().plusMinutes(5))
                .build();

        OtpRequestDTO request = new OtpRequestDTO("AG001");
        when(otpRepository.findTopByAgencyCodeOrderByCreatedAtDesc("AG001"))
                .thenReturn(Optional.of(existingOtp));

        assertThrows(OtpResendLimitExceededException.class, () -> otpService.resendOtp(request));
    }
}
