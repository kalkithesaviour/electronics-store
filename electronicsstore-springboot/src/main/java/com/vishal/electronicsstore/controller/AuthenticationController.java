package com.vishal.electronicsstore.controller;

import java.io.IOException;
import java.security.GeneralSecurityException;
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

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken.Payload;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.apache.v2.ApacheHttpTransport;
import com.google.api.client.json.gson.GsonFactory;
import com.vishal.electronicsstore.dto.GoogleLoginRequest;
import com.vishal.electronicsstore.dto.JwtRequest;
import com.vishal.electronicsstore.dto.JwtResponse;
import com.vishal.electronicsstore.dto.RefreshTokenDto;
import com.vishal.electronicsstore.dto.RefreshTokenRequest;
import com.vishal.electronicsstore.dto.UserDto;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.exception.BadAPIRequestException;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.security.JwtHelper;
import com.vishal.electronicsstore.service.RefreshTokenService;
import com.vishal.electronicsstore.service.UserService;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/auth")
@Slf4j
@Tag(name = "Auth APIs", description = "Login, Login with Google, and Regenerate JWT")
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final ModelMapper modelMapper;
    private final JwtHelper jwtHelper;
    private final String clientId;
    private final String googleProviderDefaultPassword;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    @Autowired
    public AuthenticationController(
            AuthenticationManager authenticationManager,
            ModelMapper modelMapper,
            JwtHelper jwtHelper,
            @Value("${google.client.id}") String clientId,
            @Value("${google.default-password}") String googleProviderDefaultPassword,
            UserService userService,
            RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager;
        this.modelMapper = modelMapper;
        this.jwtHelper = jwtHelper;
        this.clientId = clientId;
        this.googleProviderDefaultPassword = googleProviderDefaultPassword;
        this.userService = userService;
        this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/regenerate-jwt-token")
    @Operation(summary = "Regenerate JWT using Refresh token")
    public ResponseEntity<JwtResponse> regenerateJwtToken(@RequestBody RefreshTokenRequest request) {
        RefreshTokenDto refreshTokenDto = refreshTokenService.findByToken(request.getRefreshToken());
        refreshTokenService.verifyRefreshToken(refreshTokenDto);
        UserDto userDto = refreshTokenService.getUser(refreshTokenDto);
        User user = modelMapper.map(userDto, User.class);
        String jwtToken = jwtHelper.generateToken(user);
        JwtResponse jwtResponse = JwtResponse.builder()
                .jwtToken(jwtToken)
                .user(userDto)
                .refreshToken(refreshTokenDto)
                .build();
        return ResponseEntity.ok(jwtResponse);
    }

    @PostMapping("/login")
    @Operation(summary = "Login with username & password")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Successful login", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "401", description = "Unauthorized - Invalid credentials", content = @Content(mediaType = "application/json")),
            @ApiResponse(responseCode = "404", description = "User not found or endpoint does not exist", content = @Content(mediaType = "application/json"))
    })
    public ResponseEntity<JwtResponse> login(@RequestBody JwtRequest request) {
        String username = request.getUsername();
        String password = request.getPassword();
        log.info("Username: {}, Password: {}", username, password);
        JwtResponse jwtResponse = this.doAuthenticate(username, password);
        return ResponseEntity.ok(jwtResponse);
    }

    private JwtResponse doAuthenticate(String username, String password) {
        try {
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, password));
            User user = (User) authentication.getPrincipal();
            String jwtToken = jwtHelper.generateToken(user);

            RefreshTokenDto refreshTokenDto = refreshTokenService.createRefreshToken(username);

            JwtResponse jwtResponse = JwtResponse.builder()
                    .jwtToken(jwtToken)
                    .user(modelMapper.map(user, UserDto.class))
                    .refreshToken(refreshTokenDto)
                    .build();
            return jwtResponse;
        } catch (BadCredentialsException e) {
            throw new BadCredentialsException("Invalid Credentials!");
        }
    }

    @PostMapping("/login-with-google")
    @Operation(summary = "Login with Google")
    public ResponseEntity<JwtResponse> handleGoogleLogin(
            @RequestBody GoogleLoginRequest request) throws GeneralSecurityException, IOException {
        String idToken = request.getIdToken();
        log.info("ID token: " + idToken);

        GoogleIdTokenVerifier verifier = new GoogleIdTokenVerifier.Builder(
                new ApacheHttpTransport(), new GsonFactory())
                .setAudience(List.of(clientId))
                .build();

        GoogleIdToken googleIdToken = verifier.verify(idToken);
        if (googleIdToken != null) {
            Payload payload = googleIdToken.getPayload();
            String name = (String) payload.get("name");
            String email = payload.getEmail();
            String pictureUrl = (String) payload.get("picture");

            log.info("Full name: " + name);
            log.info("Username: " + email);
            log.info("Picture: " + pictureUrl);

            UserDto userDto = null;
            try {
                userDto = userService.getUserByEmail(email);
                log.info("Google user exists in DB");
            } catch (ResourceNotFoundException e) {
                log.info("Creating & saving new Google user in DB");
                userDto = UserDto.builder()
                        .fullName(name)
                        .email(email)
                        .userImageName(pictureUrl)
                        .password(googleProviderDefaultPassword)
                        .build();
                userDto = userService.createUser(userDto);
            }

            log.info("Now authenticating Google user...");
            JwtResponse jwtResponse = this.doAuthenticate(
                    userDto.getEmail(), googleProviderDefaultPassword);
            return ResponseEntity.ok(jwtResponse);
        } else {
            log.error("Token is invalid!");
            throw new BadAPIRequestException("Invalid Google User!");
        }
    }

}
