package com.newgen.agent.controller;

import com.newgen.agent.dto.*;
import com.newgen.agent.service.AgentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/newgen/agents")
@RequiredArgsConstructor
public class AgentController {

    private final AgentService agentService;

    @PostMapping("/signup")
    public ResponseEntity<SignUpResponseDTO> signUp(@Valid @RequestBody SignUpRequestDTO request) {
        return new ResponseEntity<>(agentService.signUp(request), HttpStatus.CREATED);
    }

    @PostMapping("/validate-agency-code")
    public ResponseEntity<AgencyCodeResponseDTO> validateAgencyCode(
            @Valid @RequestBody AgencyCodeRequestDTO request) {
        return ResponseEntity.ok(agentService.validateAgencyCode(request));
    }

    @PostMapping("/consent")
    public ResponseEntity<ConsentResponseDTO> recordConsent(
            @Valid @RequestBody ConsentRequestDTO request) {
        return ResponseEntity.ok(agentService.recordConsent(request));
    }

    @PostMapping("/set-password")
    public ResponseEntity<CredentialResponseDTO> setPassword(
            @Valid @RequestBody SetPasswordRequestDTO request) {
        return ResponseEntity.ok(agentService.setPassword(request));
    }

    @PostMapping("/set-mpin")
    public ResponseEntity<CredentialResponseDTO> setMpin(
            @Valid @RequestBody SetMpinRequestDTO request) {
        return ResponseEntity.ok(agentService.setMpin(request));
    }

    @GetMapping("/{agencyCode}")
    public ResponseEntity<AgentResponseDTO> getAgent(@PathVariable String agencyCode) {
        return ResponseEntity.ok(agentService.getAgent(agencyCode));
    }

    @PostMapping("/verify-password")
    public ResponseEntity<VerifyCredentialResponseDTO> verifyPassword(
            @Valid @RequestBody VerifyPasswordRequestDTO request) {
        return ResponseEntity.ok(agentService.verifyPassword(request));
    }

    @PostMapping("/verify-mpin")
    public ResponseEntity<VerifyCredentialResponseDTO> verifyMpin(
            @Valid @RequestBody VerifyMpinRequestDTO request) {
        return ResponseEntity.ok(agentService.verifyMpin(request));
    }
}
