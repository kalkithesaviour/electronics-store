package com.vishal.electronicsstore.controller;

import java.util.List;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vishal.electronicsstore.dto.GoogleLoginRequest;
import com.vishal.electronicsstore.dto.JwtRequest;
import com.vishal.electronicsstore.dto.JwtResponse;
import com.vishal.electronicsstore.dto.UserDto;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.security.JwtHelper;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final JwtHelper jwtHelper;
    private final String clientId;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            ModelMapper modelMapper,
            JwtHelper jwtHelper,
            @Value("${client.id}") String clientId) {
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.jwtHelper = jwtHelper;
        this.clientId = clientId;
    }

    @PostMapping("/generate-token")
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        log.info("Username: {}, Password: {}", username, password);

        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            User user = (User) authentication.getPrincipal();
            String token = jwtHelper.generateToken(user);
            JwtResponse jwtResponse = JwtResponse.builder()
                    .token(token)
                    .user(modelMapper.map(user, UserDto.class))
                    .build();
            return ResponseEntity.ok(jwtResponse);
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Credentials!");
        }
    }

    @PostMapping("/login-with-google")
    public ResponseEntity<JwtResponse> handleGoogleLogin(
            @RequestBody GoogleLoginRequest request) {
        log.info("ID token: " + request.getIdToken());

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new ApacheHttpTransport(),
                new GsonFactory())
                .setAudience(List.of(clientId))
                .build();

        return null;
    }

}
