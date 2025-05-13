package com.vishal.electronicsstore.service;

import com.vishal.electronicsstore.dto.RefreshTokenDto;
import com.vishal.electronicsstore.dto.UserDto;

public interface RefreshTokenService {

    RefreshTokenDto createRefreshToken(String username);

    RefreshTokenDto findByToken(String token);

    void verifyRefreshToken(RefreshTokenDto refreshTokenDto);

    UserDto getUser(RefreshTokenDto refreshTokenDto);

}
