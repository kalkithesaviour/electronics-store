package com.vishal.electronicsstore.service;

import java.util.List;

import com.vishal.electronicsstore.dto.UserDTO;

public interface UserService {

    UserDTO createUser(UserDTO userDTO);

    UserDTO updateUser(UserDTO userDTO, String userId);

    void deleteUser(String userId);

    List<UserDTO> getAllUsers();

    UserDTO getUserById(String userId);

    UserDTO getUserByEmail(String email);

    List<UserDTO> searchUsers(String keyword);

}