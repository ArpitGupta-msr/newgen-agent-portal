package com.newgen.login.service;

import com.newgen.login.client.AgentServiceClient;
import com.newgen.login.dto.LoginResponseDTO;
import com.newgen.login.dto.MpinLoginRequestDTO;
import com.newgen.login.dto.PasswordLoginRequestDTO;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class LoginServiceImplTest {

    @Mock
    private AgentServiceClient agentServiceClient;

    @InjectMocks
    private LoginServiceImpl loginService;

    @Test
    void testLoginWithPassword_Success() {
        PasswordLoginRequestDTO request = new PasswordLoginRequestDTO("AG001", "Password@1");
        LoginResponseDTO expected = LoginResponseDTO.builder()
                .success(true)
                .agentName("Rajesh Kumar")
                .message("Welcome, Rajesh Kumar!")
                .build();
        when(agentServiceClient.verifyPassword("AG001", "Password@1")).thenReturn(expected);

        LoginResponseDTO response = loginService.loginWithPassword(request);

        assertTrue(response.isSuccess());
        assertEquals("Rajesh Kumar", response.getAgentName());
        assertEquals("Welcome, Rajesh Kumar!", response.getMessage());
    }

    @Test
    void testLoginWithPassword_Incorrect() {
        PasswordLoginRequestDTO request = new PasswordLoginRequestDTO("AG001", "WrongPassword");
        LoginResponseDTO expected = LoginResponseDTO.builder()
                .success(false)
                .message("Incorrect password. Please try again")
                .build();
        when(agentServiceClient.verifyPassword("AG001", "WrongPassword")).thenReturn(expected);

        LoginResponseDTO response = loginService.loginWithPassword(request);

        assertFalse(response.isSuccess());
        assertEquals("Incorrect password. Please try again", response.getMessage());
    }

    @Test
    void testLoginWithMpin_Success() {
        MpinLoginRequestDTO request = new MpinLoginRequestDTO("AG001", "1234");
        LoginResponseDTO expected = LoginResponseDTO.builder()
                .success(true)
                .agentName("Rajesh Kumar")
                .message("Welcome, Rajesh Kumar!")
                .build();
        when(agentServiceClient.verifyMpin("AG001", "1234")).thenReturn(expected);

        LoginResponseDTO response = loginService.loginWithMpin(request);

        assertTrue(response.isSuccess());
        assertEquals("Rajesh Kumar", response.getAgentName());
    }

    @Test
    void testLoginWithMpin_Incorrect() {
        MpinLoginRequestDTO request = new MpinLoginRequestDTO("AG001", "0000");
        LoginResponseDTO expected = LoginResponseDTO.builder()
                .success(false)
                .message("Incorrect MPIN. Please try again.")
                .build();
        when(agentServiceClient.verifyMpin("AG001", "0000")).thenReturn(expected);

        LoginResponseDTO response = loginService.loginWithMpin(request);

        assertFalse(response.isSuccess());
        assertEquals("Incorrect MPIN. Please try again.", response.getMessage());
    }

    @Test
    void testLoginWithPassword_ServiceUnavailable() {
        PasswordLoginRequestDTO request = new PasswordLoginRequestDTO("AG001", "Password@1");
        LoginResponseDTO fallback = LoginResponseDTO.builder()
                .success(false)
                .message("Service is temporarily unavailable. Please try again later.")
                .build();
        when(agentServiceClient.verifyPassword("AG001", "Password@1")).thenReturn(fallback);

        LoginResponseDTO response = loginService.loginWithPassword(request);

        assertFalse(response.isSuccess());
        assertEquals("Service is temporarily unavailable. Please try again later.", response.getMessage());
    }

    @Test
    void testLoginWithMpin_ServiceUnavailable() {
        MpinLoginRequestDTO request = new MpinLoginRequestDTO("AG001", "1234");
        LoginResponseDTO fallback = LoginResponseDTO.builder()
                .success(false)
                .message("Service is temporarily unavailable. Please try again later.")
                .build();
        when(agentServiceClient.verifyMpin("AG001", "1234")).thenReturn(fallback);

        LoginResponseDTO response = loginService.loginWithMpin(request);

        assertFalse(response.isSuccess());
        assertEquals("Service is temporarily unavailable. Please try again later.", response.getMessage());
    }
}
