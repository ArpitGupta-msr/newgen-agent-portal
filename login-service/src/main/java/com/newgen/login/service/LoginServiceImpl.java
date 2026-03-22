package com.newgen.login.service;

import com.newgen.login.client.AgentServiceClient;
import com.newgen.login.dto.LoginResponseDTO;
import com.newgen.login.dto.MpinLoginRequestDTO;
import com.newgen.login.dto.PasswordLoginRequestDTO;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class LoginServiceImpl implements LoginService {

    private final AgentServiceClient agentServiceClient;

    @Override
    public LoginResponseDTO loginWithPassword(PasswordLoginRequestDTO request) {
        log.info("Attempting password login for agency code: {}", request.getAgencyCode());
        return agentServiceClient.verifyPassword(request.getAgencyCode(), request.getPassword());
    }

    @Override
    public LoginResponseDTO loginWithMpin(MpinLoginRequestDTO request) {
        log.info("Attempting MPIN login for agency code: {}", request.getAgencyCode());
        return agentServiceClient.verifyMpin(request.getAgencyCode(), request.getMpin());
    }
}
