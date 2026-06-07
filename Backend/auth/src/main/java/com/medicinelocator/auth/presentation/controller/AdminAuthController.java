package com.medicinelocator.auth.presentation.controller;

import com.medicinelocator.auth.application.command.LoginCommand;
import com.medicinelocator.auth.application.command.handler.LoginHandler;
import com.medicinelocator.auth.application.dto.request.LoginRequest;
import com.medicinelocator.auth.application.dto.response.AuthResponse;
import com.medicinelocator.auth.application.mapper.AuthMapper;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth/admin")
public class AdminAuthController {

    private final LoginHandler loginHandler;
    private final AuthMapper authMapper;

    public AdminAuthController(LoginHandler loginHandler, AuthMapper authMapper) {
        this.loginHandler = loginHandler;
        this.authMapper = authMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> adminLogin(@Valid @RequestBody LoginRequest request) {
        LoginCommand command = authMapper.toLoginCommand(request);
        AuthResponse response = loginHandler.handle(command);
        return ResponseEntity.ok(response);
    }
}