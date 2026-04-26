package com.gabrielsoac.overview_security.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.gabrielsoac.overview_security.config.TokenConfig;
import com.gabrielsoac.overview_security.dto.request.LoginRequest;
import com.gabrielsoac.overview_security.dto.request.RegisterUserRequest;
import com.gabrielsoac.overview_security.dto.response.LoginResponse;
import com.gabrielsoac.overview_security.dto.response.RegisterUserResponse;
import com.gabrielsoac.overview_security.entity.User;
import com.gabrielsoac.overview_security.repository.UserRepository;

import jakarta.validation.Valid;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;

@RestController
@RequestMapping("/auth")
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final TokenConfig tokenConfig;

    public AuthController(
        UserRepository userRepository,
        PasswordEncoder passwordEncoder,
        AuthenticationManager authenticationManager,
        TokenConfig tokenConfig){
        
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.tokenConfig = tokenConfig;
    }
    
    @PostMapping("/login")
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginRequest loginRequest){
        UsernamePasswordAuthenticationToken userAndPass = 
            new UsernamePasswordAuthenticationToken(loginRequest.email(), loginRequest.password());
        
        Authentication authentication = authenticationManager.authenticate(userAndPass);
        User user = (User) authentication.getPrincipal();
        String token = tokenConfig.generateToken(user);
        return ResponseEntity.ok(new LoginResponse(token));
    }

    @PostMapping("/register")
    public ResponseEntity<RegisterUserResponse> register(@Valid @RequestBody RegisterUserRequest registerUser){
        User newUser = new User();
        newUser.setEmail(registerUser.email());
        newUser.setName(registerUser.name());
        newUser.setPassword(passwordEncoder.encode(registerUser.password()));
        userRepository.save(newUser);
        return ResponseEntity.ok(new RegisterUserResponse(newUser.getName(), newUser.getEmail()));
    }
}
