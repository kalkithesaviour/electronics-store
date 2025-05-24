package com.vishal.electronicsstore.service;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDto;

public interface UserService {

    UserDto createUser(UserDto userDto);

    UserDto updateUser(UserDto userDto, String userId);

    void updateImageOfUser(String imageName, String userId);

    void deleteUser(String userId, String path);

    PageableResponse<UserDto> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDirec);

    UserDto getUserById(String userId);

    UserDto getUserByEmail(String email);

    PageableResponse<UserDto> searchUsers(String keyword, int pageNumber, int pageSize, String sortBy,
            String sortDirec);

}