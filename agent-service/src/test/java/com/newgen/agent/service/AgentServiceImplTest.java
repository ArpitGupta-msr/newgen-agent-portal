package com.newgen.agent.service;

import com.newgen.agent.dto.*;
import com.newgen.agent.entity.Agent;
import com.newgen.agent.entity.AgentRole;
import com.newgen.agent.exception.AgentAlreadyExistsException;
import com.newgen.agent.exception.AgentNotFoundException;
import com.newgen.agent.exception.InvalidAgencyCodeException;
import com.newgen.agent.exception.MpinValidationException;
import com.newgen.agent.exception.PasswordValidationException;
import com.newgen.agent.repository.AgentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AgentServiceImplTest {

    @Mock
    private AgentRepository agentRepository;

    @Mock
    private BCryptPasswordEncoder passwordEncoder;

    @Mock
    private ModelMapper modelMapper;

    @InjectMocks
    private AgentServiceImpl agentService;

    private Agent testAgent;

    @BeforeEach
    void setUp() {
        testAgent = Agent.builder()
                .id(1L)
                .agencyCode("AG001")
                .name("Rajesh Kumar")
                .role(AgentRole.AGENT)
                .consentGiven(false)
                .isRegistered(false)
                .build();
    }

    @Test
    void testSignUp_Success() {
        SignUpRequestDTO request = new SignUpRequestDTO("AG005", "New Agent", "AGENT");
        when(agentRepository.existsByAgencyCode("AG005")).thenReturn(false);
        when(agentRepository.save(any(Agent.class))).thenReturn(testAgent);

        SignUpResponseDTO response = agentService.signUp(request);

        assertNotNull(response);
        assertEquals("Agent registered successfully. Please proceed with verification.", response.getMessage());
        verify(agentRepository).save(any(Agent.class));
    }

    @Test
    void testSignUp_AgentAlreadyExists() {
        SignUpRequestDTO request = new SignUpRequestDTO("AG001", "Rajesh Kumar", "AGENT");
        when(agentRepository.existsByAgencyCode("AG001")).thenReturn(true);

        assertThrows(AgentAlreadyExistsException.class, () -> agentService.signUp(request));
    }

    @Test
    void testSignUp_InvalidRole() {
        SignUpRequestDTO request = new SignUpRequestDTO("AG006", "Test Agent", "INVALID");
        when(agentRepository.existsByAgencyCode("AG006")).thenReturn(false);

        assertThrows(IllegalArgumentException.class, () -> agentService.signUp(request));
    }

    @Test
    void testValidateAgencyCode_Valid() {
        AgencyCodeRequestDTO request = new AgencyCodeRequestDTO("AG001");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));

        AgencyCodeResponseDTO response = agentService.validateAgencyCode(request);

        assertTrue(response.isValid());
        assertEquals("Rajesh Kumar", response.getAgentName());
    }

    @Test
    void testValidateAgencyCode_Invalid() {
        AgencyCodeRequestDTO request = new AgencyCodeRequestDTO("INVALID");
        when(agentRepository.findByAgencyCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(InvalidAgencyCodeException.class, () -> agentService.validateAgencyCode(request));
    }

    @Test
    void testRecordConsent_Success() {
        ConsentRequestDTO request = new ConsentRequestDTO("AG001", true);
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        when(agentRepository.save(any(Agent.class))).thenReturn(testAgent);

        ConsentResponseDTO response = agentService.recordConsent(request);

        assertEquals("Consent recorded successfully.", response.getMessage());
        assertTrue(testAgent.getConsentGiven());
    }

    @Test
    void testSetPassword_Success() {
        SetPasswordRequestDTO request = new SetPasswordRequestDTO("AG001", "Password@1", "Password@1");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        when(passwordEncoder.encode("Password@1")).thenReturn("$2a$10$hashedPassword");
        when(agentRepository.save(any(Agent.class))).thenReturn(testAgent);

        CredentialResponseDTO response = agentService.setPassword(request);

        assertTrue(response.isSuccess());
        assertEquals("Password has been set up successfully.", response.getMessage());
        assertTrue(testAgent.getIsRegistered());
    }

    @Test
    void testSetPassword_DoNotMatch() {
        SetPasswordRequestDTO request = new SetPasswordRequestDTO("AG001", "Password@1", "Password@2");

        assertThrows(PasswordValidationException.class, () -> agentService.setPassword(request));
    }

    @Test
    void testSetPassword_AgentNotFound() {
        SetPasswordRequestDTO request = new SetPasswordRequestDTO("INVALID", "Password@1", "Password@1");
        when(agentRepository.findByAgencyCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(AgentNotFoundException.class, () -> agentService.setPassword(request));
    }

    @Test
    void testSetMpin_Success() {
        SetMpinRequestDTO request = new SetMpinRequestDTO("AG001", "1234", "1234");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        when(passwordEncoder.encode("1234")).thenReturn("$2a$10$hashedMpin");
        when(agentRepository.save(any(Agent.class))).thenReturn(testAgent);

        CredentialResponseDTO response = agentService.setMpin(request);

        assertTrue(response.isSuccess());
        assertEquals("MPIN has been set up successfully.", response.getMessage());
    }

    @Test
    void testSetMpin_DoNotMatch() {
        SetMpinRequestDTO request = new SetMpinRequestDTO("AG001", "1234", "5678");

        assertThrows(MpinValidationException.class, () -> agentService.setMpin(request));
    }

    @Test
    void testGetAgent_Success() {
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        AgentResponseDTO expectedDTO = AgentResponseDTO.builder()
                .agencyCode("AG001").name("Rajesh Kumar").role("AGENT").isRegistered(false).build();
        when(modelMapper.map(testAgent, AgentResponseDTO.class)).thenReturn(expectedDTO);

        AgentResponseDTO response = agentService.getAgent("AG001");

        assertEquals("AG001", response.getAgencyCode());
        assertEquals("Rajesh Kumar", response.getName());
    }

    @Test
    void testGetAgent_NotFound() {
        when(agentRepository.findByAgencyCode("INVALID")).thenReturn(Optional.empty());

        assertThrows(AgentNotFoundException.class, () -> agentService.getAgent("INVALID"));
    }

    @Test
    void testVerifyPassword_Correct() {
        testAgent.setPassword("$2a$10$hashedPassword");
        VerifyPasswordRequestDTO request = new VerifyPasswordRequestDTO("AG001", "Password@1");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        when(passwordEncoder.matches("Password@1", "$2a$10$hashedPassword")).thenReturn(true);

        VerifyCredentialResponseDTO response = agentService.verifyPassword(request);

        assertTrue(response.isValid());
        assertEquals("Rajesh Kumar", response.getAgentName());
    }

    @Test
    void testVerifyPassword_Incorrect() {
        testAgent.setPassword("$2a$10$hashedPassword");
        VerifyPasswordRequestDTO request = new VerifyPasswordRequestDTO("AG001", "WrongPassword");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        when(passwordEncoder.matches("WrongPassword", "$2a$10$hashedPassword")).thenReturn(false);

        VerifyCredentialResponseDTO response = agentService.verifyPassword(request);

        assertFalse(response.isValid());
        assertEquals("Incorrect password. Please try again", response.getMessage());
    }

    @Test
    void testVerifyPassword_NoPasswordSet() {
        VerifyPasswordRequestDTO request = new VerifyPasswordRequestDTO("AG001", "Password@1");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));

        VerifyCredentialResponseDTO response = agentService.verifyPassword(request);

        assertFalse(response.isValid());
    }

    @Test
    void testVerifyMpin_Correct() {
        testAgent.setMpin("$2a$10$hashedMpin");
        VerifyMpinRequestDTO request = new VerifyMpinRequestDTO("AG001", "1234");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        when(passwordEncoder.matches("1234", "$2a$10$hashedMpin")).thenReturn(true);

        VerifyCredentialResponseDTO response = agentService.verifyMpin(request);

        assertTrue(response.isValid());
        assertEquals("Rajesh Kumar", response.getAgentName());
    }

    @Test
    void testVerifyMpin_Incorrect() {
        testAgent.setMpin("$2a$10$hashedMpin");
        VerifyMpinRequestDTO request = new VerifyMpinRequestDTO("AG001", "0000");
        when(agentRepository.findByAgencyCode("AG001")).thenReturn(Optional.of(testAgent));
        when(passwordEncoder.matches("0000", "$2a$10$hashedMpin")).thenReturn(false);

        VerifyCredentialResponseDTO response = agentService.verifyMpin(request);

        assertFalse(response.isValid());
        assertEquals("Incorrect MPIN. Please try again.", response.getMessage());
    }
}
