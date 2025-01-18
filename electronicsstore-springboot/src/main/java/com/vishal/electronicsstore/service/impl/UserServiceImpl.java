package com.vishal.electronicsstore.service.impl;

import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.UserDTO;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.service.UserService;

@Service
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found: ";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public UserServiceImpl(UserRepository userRepository, ModelMapper modelMapper) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public UserDTO createUser(UserDTO userDTO) {
        String userId = UUID.randomUUID().toString();
        userDTO.setUserId(userId);

        User user = dtoToEntity(userDTO);
        User savedUser = userRepository.save(user);

        return entityToDto(savedUser);
    }

    @Override
    public UserDTO updateUser(UserDTO userDTO, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE + userId));

        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setGender(userDTO.getGender());
        user.setAbout(userDTO.getAbout());
        user.setImageName(userDTO.getImageName());

        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    @Override
    public void deleteUser(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE + userId));

        userRepository.delete(user);
    }

    @Override
    public List<UserDTO> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users.stream().map(this::entityToDto).toList();
    }

    @Override
    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE + userId));

        return entityToDto(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException(USER_NOT_FOUND_MESSAGE + email));

        return entityToDto(user);
    }

    @Override
    public List<UserDTO> searchUsers(String keyword) {
        List<User> users = userRepository.findByNameContaining(keyword);
        return users.stream().map(this::entityToDto).toList();
    }

    private User dtoToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO entityToDto(User savedUser) {
        return modelMapper.map(savedUser, UserDTO.class);
    }

}
