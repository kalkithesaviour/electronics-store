package com.vishal.electronicsstore.service.impl;

import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDto;
import com.vishal.electronicsstore.entity.Role;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.exception.UserAlreadyExistsException;
import com.vishal.electronicsstore.repository.RefreshTokenRepository;
import com.vishal.electronicsstore.repository.RoleRepository;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.service.UserService;
import com.vishal.electronicsstore.util.PageableUtil;

import jakarta.transaction.Transactional;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private static final String USER_NOT_FOUND_MESSAGE = "User not found: ";

    private final UserRepository userRepository;
    private final ModelMapper modelMapper;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final RefreshTokenRepository refreshTokenRepository;

    @Autowired
    public UserServiceImpl(
            UserRepository userRepository,
            ModelMapper modelMapper,
            PasswordEncoder passwordEncoder,
            RoleRepository roleRepository,
            RefreshTokenRepository refreshTokenRepository) {
        this.userRepository = userRepository;
        this.modelMapper = modelMapper;
        this.passwordEncoder = passwordEncoder;
        this.roleRepository = roleRepository;
        this.refreshTokenRepository = refreshTokenRepository;
    }

    @Override
    public UserDto createUser(UserDto userDto) {
        userRepository.findByEmail(userDto.getEmail())
                .ifPresent(user -> {
                    throw new UserAlreadyExistsException();
                });

        String userId = UUID.randomUUID().toString();
        userDto.setUserId(userId);
        User user = dtoToEntity(userDto);
        user.setPassword(passwordEncoder.encode(user.getPassword()));
        Role roleNormal = roleRepository.findByRoleName("ROLE_USER")
                .orElseThrow(() -> new ResourceNotFoundException("Role not found in Database!"));
        user.setRoles(List.of(roleNormal));
        User savedUser = userRepository.save(user);
        return entityToDto(savedUser);
    }

    @Override
    public UserDto updateUser(UserDto userDto, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        user.setFullName(userDto.getFullName());
        user.setPassword(passwordEncoder.encode(userDto.getPassword()));
        user.setGender(userDto.getGender());
        user.setAbout(userDto.getAbout());
        user.setUserImageName(userDto.getUserImageName());

        User updatedUser = userRepository.save(user);
        return entityToDto(updatedUser);
    }

    @Override
    public void updateImageOfUser(String imageName, String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        user.setUserImageName(imageName);
        userRepository.save(user);
    }

    @Transactional
    @Override
    public void deleteUser(String userId, String imagePath) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        // Resolve the absolute project root (with Docker compatibility)
        Path projectRoot;
        try {
            // Check if we're running in Docker (via environment variable)
            String dockerBasePath = System.getenv("DOCKER_BASE_PATH");

            if (dockerBasePath != null && !dockerBasePath.isEmpty()) {
                // Docker mode - use explicit base path
                projectRoot = Paths.get(dockerBasePath);
                log.info("Using DOCKER_BASE_PATH: {}", projectRoot);
            } else {
                // Local development mode - original logic
                Path codeSourcePath = Paths.get(
                        UserServiceImpl.class.getProtectionDomain().getCodeSource().getLocation().toURI());
                projectRoot = codeSourcePath.getParent().getParent();
            }
        } catch (URISyntaxException e) {
            throw new RuntimeException("Failed to resolve project root path", e);
        }

        // Build image path
        Path imageFilePath = projectRoot.resolve(Paths.get(imagePath, user.getUserImageName()));
        log.info("Resolved image path: {}", imageFilePath.toAbsolutePath());

        // Attempt to delete
        try {
            boolean deleted = Files.deleteIfExists(imageFilePath);
            log.info("Image deleted? {}", deleted);
        } catch (IOException e) {
            log.error("Error deleting image file", e);
        }

        // Proceed with user deletion
        refreshTokenRepository.deleteRefreshTokenByUserId(user.getUserId());
        user.setRoles(new ArrayList<>(user.getRoles()));
        user.getRoles().clear();
        userRepository.save(user);
        userRepository.delete(user);
    }

    @Override
    public PageableResponse<UserDto> getAllUsers(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<User> usersPage = userRepository.findAll(pageable);
        return PageableUtil.getPageableResponse(usersPage, UserDto.class, modelMapper);
    }

    @Override
    public UserDto getUserById(String userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + userId));

        return entityToDto(user);
    }

    @Override
    public UserDto getUserByEmail(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException(USER_NOT_FOUND_MESSAGE + email));

        return entityToDto(user);
    }

    @Override
    public PageableResponse<UserDto> searchUsers(
            String keyword,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<User> usersPage = userRepository.findByFullNameContaining(keyword, pageable);
        return PageableUtil.getPageableResponse(usersPage, UserDto.class, modelMapper);
    }

    private User dtoToEntity(UserDto userDto) {
        return modelMapper.map(userDto, User.class);
    }

    private UserDto entityToDto(User savedUser) {
        return modelMapper.map(savedUser, UserDto.class);
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
