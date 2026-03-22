package com.newgen.login.controller;

import com.newgen.login.dto.LoginResponseDTO;
import com.newgen.login.dto.MpinLoginRequestDTO;
import com.newgen.login.dto.PasswordLoginRequestDTO;
import com.newgen.login.service.LoginService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/newgen/login")
@RequiredArgsConstructor
public class LoginController {

    private final LoginService loginService;

    @PostMapping("/password")
    public ResponseEntity<LoginResponseDTO> loginWithPassword(
            @Valid @RequestBody PasswordLoginRequestDTO request) {
        return ResponseEntity.ok(loginService.loginWithPassword(request));
    }

    @PostMapping("/mpin")
    public ResponseEntity<LoginResponseDTO> loginWithMpin(
            @Valid @RequestBody MpinLoginRequestDTO request) {
        return ResponseEntity.ok(loginService.loginWithMpin(request));
    }
}
