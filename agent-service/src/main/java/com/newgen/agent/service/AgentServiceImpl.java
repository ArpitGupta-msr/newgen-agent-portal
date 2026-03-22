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
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
@Slf4j
public class AgentServiceImpl implements AgentService {

    private final AgentRepository agentRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final ModelMapper modelMapper;

    @Override
    public SignUpResponseDTO signUp(SignUpRequestDTO request) {
        if (agentRepository.existsByAgencyCode(request.getAgencyCode())) {
            throw new AgentAlreadyExistsException("Agent with this agency code already exists.");
        }

        AgentRole role = AgentRole.valueOf(request.getRole().toUpperCase());

        Agent agent = Agent.builder()
                .agencyCode(request.getAgencyCode())
                .name(request.getName())
                .role(role)
                .consentGiven(false)
                .isRegistered(false)
                .build();

        Agent savedAgent = agentRepository.save(agent);

        return SignUpResponseDTO.builder()
                .message("Agent registered successfully. Please proceed with verification.")
                .agentId(savedAgent.getId())
                .build();
    }

    @Override
    public AgencyCodeResponseDTO validateAgencyCode(AgencyCodeRequestDTO request) {
        return agentRepository.findByAgencyCode(request.getAgencyCode())
                .map(agent -> AgencyCodeResponseDTO.builder()
                        .valid(true)
                        .agentName(agent.getName())
                        .message("Agency code is valid.")
                        .isRegistered(Boolean.TRUE.equals(agent.getIsRegistered()))
                        .build())
                .orElseThrow(() -> new InvalidAgencyCodeException(
                        "Incorrect agency code, please enter a valid code."));
    }

    @Override
    public ConsentResponseDTO recordConsent(ConsentRequestDTO request) {
        Agent agent = findAgentByCode(request.getAgencyCode());
        agent.setConsentGiven(request.getConsentGiven());
        agentRepository.save(agent);

        return ConsentResponseDTO.builder()
                .message("Consent recorded successfully.")
                .build();
    }

    @Override
    public CredentialResponseDTO setPassword(SetPasswordRequestDTO request) {
        if (!request.getPassword().equals(request.getConfirmPassword())) {
            throw new PasswordValidationException("Passwords do not match");
        }

        Agent agent = findAgentByCode(request.getAgencyCode());
        agent.setPassword(passwordEncoder.encode(request.getPassword()));
        agent.setIsRegistered(true);
        agentRepository.save(agent);

        return CredentialResponseDTO.builder()
                .success(true)
                .message("Password has been set up successfully.")
                .build();
    }

    @Override
    public CredentialResponseDTO setMpin(SetMpinRequestDTO request) {
        if (!request.getMpin().equals(request.getConfirmMpin())) {
            throw new MpinValidationException("MPINs do not match");
        }

        Agent agent = findAgentByCode(request.getAgencyCode());
        agent.setMpin(passwordEncoder.encode(request.getMpin()));
        agent.setIsRegistered(true);
        agentRepository.save(agent);

        return CredentialResponseDTO.builder()
                .success(true)
                .message("MPIN has been set up successfully.")
                .build();
    }

    @Override
    public AgentResponseDTO getAgent(String agencyCode) {
        Agent agent = findAgentByCode(agencyCode);
        return modelMapper.map(agent, AgentResponseDTO.class);
    }

    @Override
    public VerifyCredentialResponseDTO verifyPassword(VerifyPasswordRequestDTO request) {
        Agent agent = findAgentByCode(request.getAgencyCode());

        if (agent.getPassword() == null || !passwordEncoder.matches(request.getPassword(), agent.getPassword())) {
            return VerifyCredentialResponseDTO.builder()
                    .valid(false)
                    .message("Incorrect password. Please try again")
                    .build();
        }

        return VerifyCredentialResponseDTO.builder()
                .valid(true)
                .agentName(agent.getName())
                .message("Password verified successfully.")
                .build();
    }

    @Override
    public VerifyCredentialResponseDTO verifyMpin(VerifyMpinRequestDTO request) {
        Agent agent = findAgentByCode(request.getAgencyCode());

        if (agent.getMpin() == null || !passwordEncoder.matches(request.getMpin(), agent.getMpin())) {
            return VerifyCredentialResponseDTO.builder()
                    .valid(false)
                    .message("Incorrect MPIN. Please try again.")
                    .build();
        }

        return VerifyCredentialResponseDTO.builder()
                .valid(true)
                .agentName(agent.getName())
                .message("MPIN verified successfully.")
                .build();
    }

    private Agent findAgentByCode(String agencyCode) {
        return agentRepository.findByAgencyCode(agencyCode)
                .orElseThrow(() -> new AgentNotFoundException(
                        "Agent not found with agency code: " + agencyCode));
    }
}
