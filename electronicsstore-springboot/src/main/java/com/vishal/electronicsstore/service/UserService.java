package com.vishal.electronicsstore.service;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDTO;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO, String userId);

    void deleteUser(String userId, String path);

    PageableResponse<UserDTO> getAllUsers(int pageNumber, int pageSize, String sortBy, String sortDirec);

    UserDTO getUserById(String userId);

    UserDTO getUserByEmail(String email);

    PageableResponse<UserDTO> searchUsers(String keyword, int pageNumber, int pageSize, String sortBy,
            String sortDirec);

}