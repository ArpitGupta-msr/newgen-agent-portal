package com.newgen.agent.service;

import com.newgen.agent.dto.AdminAgentResponseDTO;
import com.newgen.agent.dto.CreateAgentRequestDTO;
import com.newgen.agent.entity.Agent;
import com.newgen.agent.entity.AgentRole;
import com.newgen.agent.exception.AgentAlreadyExistsException;
import com.newgen.agent.exception.AgentNotFoundException;
import com.newgen.agent.repository.AgentRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.modelmapper.ModelMapper;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class AdminServiceImpl implements AdminService {

    private final AgentRepository agentRepository;
    private final ModelMapper modelMapper;

    @Override
    public AdminAgentResponseDTO createAgent(CreateAgentRequestDTO request) {
        if (agentRepository.existsByAgencyCode(request.getAgencyCode())) {
            throw new AgentAlreadyExistsException("Agent with agency code '" + request.getAgencyCode() + "' already exists.");
        }

        AgentRole role = AgentRole.valueOf(request.getRole().toUpperCase());

        Agent agent = Agent.builder()
                .agencyCode(request.getAgencyCode())
                .name(request.getName())
                .role(role)
                .consentGiven(false)
                .isRegistered(false)
                .build();

        Agent saved = agentRepository.save(agent);
        return modelMapper.map(saved, AdminAgentResponseDTO.class);
    }

    @Override
    public List<AdminAgentResponseDTO> getAllAgents() {
        return agentRepository.findAll().stream()
                .map(agent -> modelMapper.map(agent, AdminAgentResponseDTO.class))
                .collect(Collectors.toList());
    }

    @Override
    public AdminAgentResponseDTO getAgentByCode(String agencyCode) {
        Agent agent = agentRepository.findByAgencyCode(agencyCode)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with agency code: " + agencyCode));
        return modelMapper.map(agent, AdminAgentResponseDTO.class);
    }

    @Override
    public void deleteAgent(String agencyCode) {
        Agent agent = agentRepository.findByAgencyCode(agencyCode)
                .orElseThrow(() -> new AgentNotFoundException("Agent not found with agency code: " + agencyCode));
        agentRepository.delete(agent);
    }
}
