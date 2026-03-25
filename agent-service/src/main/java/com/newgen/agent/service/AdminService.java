package com.newgen.agent.service;

import com.newgen.agent.dto.AdminAgentResponseDTO;
import com.newgen.agent.dto.CreateAgentRequestDTO;

import java.util.List;

public interface AdminService {
    AdminAgentResponseDTO createAgent(CreateAgentRequestDTO request);
    List<AdminAgentResponseDTO> getAllAgents();
    AdminAgentResponseDTO getAgentByCode(String agencyCode);
    void deleteAgent(String agencyCode);
}
