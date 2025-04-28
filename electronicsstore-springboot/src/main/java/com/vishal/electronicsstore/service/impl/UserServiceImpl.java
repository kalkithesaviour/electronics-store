package com.vishal.electronicsstore.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDTO;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.service.UserService;
import com.vishal.electronicsstore.util.PageableUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
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
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        user.setName(userDTO.getName());
        user.setPassword(userDTO.getPassword());
        user.setGender(userDTO.getGender());
        user.setAbout(userDTO.getAbout());
        user.setUserImageName(userDTO.getUserImageName());

        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    @Override
    public void deleteUser(String userId, String imagePath) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        Path path = Paths.get(imagePath, user.getUserImageName());
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("User image not found in folder!");
            e.printStackTrace();
        }

        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDTO> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<User> usersPage = userRepository.findAll(pageable);
        return PageableUtil.getPageableResponse(usersPage, UserDTO.class, modelMapper);
    }

    @Override
    public UserDTO getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        return entityToDto(user);
    }

    @Override
    public UserDTO getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + email));

        return entityToDto(user);
    }

    @Override
    public PageableResponse<UserDTO> searchUsers(
            String keyword,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<User> usersPage = userRepository.findByNameContaining(keyword, pageable);
        return PageableUtil.getPageableResponse(usersPage, UserDTO.class, modelMapper);
    }

    private User dtoToEntity(UserDTO userDTO) {
        return modelMapper.map(userDTO, User.class);
    }

    private UserDTO entityToDto(User savedUser) {
        return modelMapper.map(savedUser, UserDTO.class);
    }

    private Pageable createPageable(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Sort sort = sortDirec.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

}
