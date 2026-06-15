package com.feedbackhub.service;

import com.feedbackhub.dto.AuthDto;
import com.feedbackhub.entity.User;
import com.feedbackhub.enums.UserRole;
import com.feedbackhub.exception.ResourceNotFoundException;
import com.feedbackhub.repository.UserRepository;
import com.feedbackhub.security.JwtService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

@Service
public class AuthService {

    @Autowired
    private UserRepository userRepo;

    @Autowired
    private PasswordEncoder encoder;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private ApplicationContext applicationContext;

    private AuthenticationManager getAuthManager() {
        return applicationContext.getBean(AuthenticationManager.class);
    }

    public AuthDto.AuthResponse login(AuthDto.LoginRequest req) {
        getAuthManager().authenticate(
                new UsernamePasswordAuthenticationToken(req.getEmail(), req.getPassword()));

        User user = userRepo.findByEmail(req.getEmail())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        String token = jwtService.generateToken(user);

        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setFullName(user.getFullName());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setPodName(user.getPodName());
        response.setCohortName(user.getCohortName());
        return response;
    }

    public AuthDto.AuthResponse register(AuthDto.RegisterRequest req) {
        if (userRepo.existsByEmail(req.getEmail())) {
            throw new IllegalArgumentException("Email already registered: " + req.getEmail());
        }
        User user = new User();
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setPassword(encoder.encode(req.getPassword()));
        user.setRole(UserRole.valueOf(req.getRole().toUpperCase()));
        user.setDepartment(req.getDepartment());
        user.setPodName(req.getPodName());
        user.setCohortName(req.getCohortName());
        user.setActive(true);
        user = userRepo.save(user);

        String token = jwtService.generateToken(user);

        AuthDto.AuthResponse response = new AuthDto.AuthResponse();
        response.setToken(token);
        response.setRole(user.getRole().name());
        response.setFullName(user.getFullName());
        response.setUserId(user.getId());
        response.setEmail(user.getEmail());
        response.setPodName(user.getPodName());
        response.setCohortName(user.getCohortName());
        return response;
    }
}
