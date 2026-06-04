package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.service;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.LoginRequest;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.dto.LoginResponse;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.model.Operator;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.repository.OperatorRepository;
import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.util.JwtUtil;
import jakarta.annotation.PostConstruct;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class AuthService {

    private final OperatorRepository operatorRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtil jwtUtil;

    public AuthService(OperatorRepository operatorRepository, PasswordEncoder passwordEncoder, JwtUtil jwtUtil) {
        this.operatorRepository = operatorRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtil = jwtUtil;
    }

    @PostConstruct
    public void seedAdminOperator() {
        // Automatically seed default operator for pilot dashboard access if empty
        if (operatorRepository.count() == 0) {
            Operator admin = new Operator(
                    "operator_admin",
                    passwordEncoder.encode("securepassword123"),
                    "admin"
            );
            operatorRepository.save(admin);
        }
    }

    public LoginResponse login(LoginRequest request) {
        Optional<Operator> opOpt = operatorRepository.findByUsername(request.getUsername());
        if (opOpt.isPresent() && passwordEncoder.matches(request.getPassword(), opOpt.get().getPassword())) {
            Operator op = opOpt.get();
            String accessToken = jwtUtil.generateAccessToken(op.getUsername(), op.getRole());
            String refreshToken = jwtUtil.generateRefreshToken(op.getUsername());
            
            LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                    op.getId(),
                    op.getUsername(),
                    op.getRole()
            );
            return new LoginResponse(true, accessToken, refreshToken, userInfo);
        }
        return new LoginResponse(false, null, null, null);
    }

    public LoginResponse refresh(String refreshToken) {
        try {
            String username = jwtUtil.extractUsername(refreshToken);
            Optional<Operator> opOpt = operatorRepository.findByUsername(username);
            if (opOpt.isPresent()) {
                Operator op = opOpt.get();
                if (jwtUtil.validateToken(refreshToken, op.getUsername())) {
                    String accessToken = jwtUtil.generateAccessToken(op.getUsername(), op.getRole());
                    String newRefreshToken = jwtUtil.generateRefreshToken(op.getUsername());
                    LoginResponse.UserInfo userInfo = new LoginResponse.UserInfo(
                            op.getId(),
                            op.getUsername(),
                            op.getRole()
                    );
                    return new LoginResponse(true, accessToken, newRefreshToken, userInfo);
                }
            }
        } catch (Exception e) {
            // Invalid refresh token
        }
        return new LoginResponse(false, null, null, null);
    }
}
