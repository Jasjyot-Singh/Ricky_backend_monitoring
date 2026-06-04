package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.controller;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.LoginRequest;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.LoginResponse;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.RefreshTokenRequest;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/api/v1/auth")
@CrossOrigin(origins = "*")
public class AuthController {

    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@RequestBody LoginRequest request) {
        LoginResponse response = authService.login(request);
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/refresh")
    public ResponseEntity<LoginResponse> refresh(@RequestBody RefreshTokenRequest request) {
        LoginResponse response = authService.refresh(request.getRefreshToken());
        if (response.isSuccess()) {
            return ResponseEntity.ok(response);
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @PostMapping("/logout")
    public ResponseEntity<Map<String, Object>> logout(@RequestBody(required = false) Map<String, String> requestBody) {
        // Stateless JWT logout - client discards tokens.
        return ResponseEntity.ok(Map.of("success", true, "message", "Logged out successfully"));
    }
}
