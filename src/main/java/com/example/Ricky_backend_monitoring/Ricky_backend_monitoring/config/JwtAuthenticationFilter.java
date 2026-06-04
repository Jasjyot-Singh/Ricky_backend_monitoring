package com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.config;

import com.example.Ricky_backend_monitoring.Ricky_backend_monitoring.util.JwtUtil;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Collections;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtUtil jwtUtil;

    @Value("${ricky.device.token:RICKY_PILOT_DEVICE_TOKEN_SECRET_2026}")
    private String configuredDeviceToken;

    public JwtAuthenticationFilter(JwtUtil jwtUtil) {
        this.jwtUtil = jwtUtil;
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {

        final String authHeader = request.getHeader("Authorization");
        final String path = request.getRequestURI();

        String token = null;
        String username = null;

        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            token = authHeader.substring(7);

            // Check if this is a telemetry or device command endpoint requesting device authentication
            if (path.startsWith("/api/v1/telemetry") || path.contains("/commands")) {
                if (token.equals(configuredDeviceToken)) {
                    // Authenticate as DEVICE
                    UserDetails deviceDetails = new User("DEVICE", "", 
                            Collections.singletonList(new SimpleGrantedAuthority("ROLE_DEVICE")));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                            deviceDetails, null, deviceDetails.getAuthorities());
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                    filterChain.doFilter(request, response);
                    return;
                }
            }

            try {
                username = jwtUtil.extractUsername(token);
            } catch (Exception e) {
                // Invalid or malformed token
            }
        }

        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // Validate dashboard operator JWT
            if (jwtUtil.validateToken(token, username)) {
                String role = jwtUtil.extractClaim(token, claims -> claims.get("role", String.class));
                UserDetails userDetails = new User(username, "", 
                        Collections.singletonList(new SimpleGrantedAuthority("ROLE_" + (role != null ? role.toUpperCase() : "ADMIN"))));
                
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        userDetails, null, userDetails.getAuthorities());
                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                SecurityContextHolder.getContext().setAuthentication(authentication);
            }
        }

        filterChain.doFilter(request, response);
    }
}
