package com.newgen.agent.controller;

import com.newgen.agent.dto.AdminAgentResponseDTO;
import com.newgen.agent.dto.CreateAgentRequestDTO;
import com.newgen.agent.service.AdminService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/newgen/admin/agents")
@RequiredArgsConstructor
public class AdminController {

    private final AdminService adminService;

    @PostMapping
    public ResponseEntity<AdminAgentResponseDTO> createAgent(@Valid @RequestBody CreateAgentRequestDTO request) {
        return new ResponseEntity<>(adminService.createAgent(request), HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AdminAgentResponseDTO>> getAllAgents() {
        return ResponseEntity.ok(adminService.getAllAgents());
    }

    @GetMapping("/{agencyCode}")
    public ResponseEntity<AdminAgentResponseDTO> getAgent(@PathVariable String agencyCode) {
        return ResponseEntity.ok(adminService.getAgentByCode(agencyCode));
    }

    @DeleteMapping("/{agencyCode}")
    public ResponseEntity<Void> deleteAgent(@PathVariable String agencyCode) {
        adminService.deleteAgent(agencyCode);
        return ResponseEntity.noContent().build();
    }
}
