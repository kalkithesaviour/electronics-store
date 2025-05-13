package com.vishal.electronicsstore.service.impl;

import java.time.Instant;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.RefreshTokenDto;
import com.vishal.electronicsstore.dto.UserDto;
import com.vishal.electronicsstore.entity.RefreshToken;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.repository.RefreshTokenRepository;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.service.RefreshTokenService;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class RefreshTokenServiceImpl implements RefreshTokenService {

    private final RefreshTokenRepository refreshTokenRepository;
    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public RefreshTokenServiceImpl(
            RefreshTokenRepository refreshTokenRepository,
            UserRepository userRepository,
            ModelMapper modelMapper) {
        this.refreshTokenRepository = refreshTokenRepository;
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public RefreshTokenDto createRefreshToken(String username) {
        User user = userRepository.findByEmail(username).orElseThrow(
                () -> new ResourceNotFoundException("User not found in database!"));

        RefreshToken refreshToken = refreshTokenRepository.findByUser(user).orElse(null);
        if (refreshToken != null) {
            refreshToken.setToken(UUID.randomUUID().toString());
            refreshToken.setExpiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60));
        } else {
            refreshToken = RefreshToken.builder()
                    .user(user)
                    .token(UUID.randomUUID().toString())
                    .expiryDate(Instant.now().plusSeconds(5 * 24 * 60 * 60))
                    .build();
        }

        RefreshToken savedRefreshToken = refreshTokenRepository.save(refreshToken);
        return modelMapper.map(savedRefreshToken, RefreshTokenDto.class);
    }

    @Override
    public RefreshTokenDto findByToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token).orElseThrow(
                () -> new ResourceNotFoundException("Refresh token not found in database!"));

        return modelMapper.map(refreshToken, RefreshTokenDto.class);
    }

    @Override
    public void verifyRefreshToken(RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = modelMapper.map(refreshTokenDto, RefreshToken.class);
        if (refreshTokenDto.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(refreshToken);
            throw new RuntimeException("Refresh token expired!");
        }
    }

    @Override
    public UserDto getUser(RefreshTokenDto refreshTokenDto) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(refreshTokenDto.getToken()).orElseThrow(
                () -> new ResourceNotFoundException("Refresh token not found in database!"));

        User user = refreshToken.getUser();
        return modelMapper.map(user, UserDto.class);
    }

}
