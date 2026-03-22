package com.newgen.agent.service;

import com.newgen.agent.dto.*;

public interface AgentService {

    SignUpResponseDTO signUp(SignUpRequestDTO request);

    AgencyCodeResponseDTO validateAgencyCode(AgencyCodeRequestDTO request);

    ConsentResponseDTO recordConsent(ConsentRequestDTO request);

    CredentialResponseDTO setPassword(SetPasswordRequestDTO request);

    CredentialResponseDTO setMpin(SetMpinRequestDTO request);

    AgentResponseDTO getAgent(String agencyCode);

    VerifyCredentialResponseDTO verifyPassword(VerifyPasswordRequestDTO request);

    VerifyCredentialResponseDTO verifyMpin(VerifyMpinRequestDTO request);
}
