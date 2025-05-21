package com.vishal.electronicsstore.service;

import java.util.Arrays;
import java.util.List;
import java.util.Optional;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDto;
import com.vishal.electronicsstore.entity.Role;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.repository.RoleRepository;
import com.vishal.electronicsstore.repository.UserRepository;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@SpringBootTest
public class UserServiceTests {

    private final UserService userService;
    private final ModelMapper modelMapper;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private RoleRepository roleRepository;

    @Autowired
    public UserServiceTests(UserService userService, ModelMapper modelMapper) {
        this.userService = userService;
        this.modelMapper = modelMapper;
    }

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    private User user;
    private Role role;

    @BeforeEach
    public void init() {
        role = Role.builder()
                .roleName("roleTest")
                .build();

        user = User.builder()
                .fullName("Vedanti Gori")
                .email("vedanti@gmail.com")
                .about("This is testing create user")
                .gender("Female")
                .userImageName("vedanti.png")
                .password("vedanti")
                .roles(List.of(role))
                .build();
    }

    @Test
    public void createUserTest() {
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);
        Mockito.when(roleRepository.findByRoleName(Mockito.anyString())).thenReturn(Optional.of(role));

        UserDto user1 = userService.createUser(modelMapper.map(user, UserDto.class));
        Assertions.assertNotNull(user1);
        Assertions.assertEquals("Vedanti Gori", user1.getFullName());
        log.info(user1.getFullName());
    }

    @Test
    public void updateUserTest() {
        UserDto userDto = UserDto.builder()
                .fullName("Vedanti Balachandra Gori")
                .about("This is testing update user")
                .gender("Female")
                .password("new password")
                .userImageName("vedanti.png")
                .build();

        Mockito.when(userRepository.findById(Mockito.anyString())).thenReturn(Optional.of(user));
        Mockito.when(userRepository.save(Mockito.any())).thenReturn(user);

        UserDto updatedUser = userService.updateUser(userDto, "userIdTest");
        Assertions.assertNotNull(updatedUser);
        Assertions.assertEquals(userDto.getFullName(), updatedUser.getFullName(), "Full name not matched - Updation of user failed!");
        log.info(updatedUser.getFullName());
    }

    @Value("${user.image.path}")
    private String imagePath;

    @Test
    public void deleteUserTest() {
        String userId = "userIdTest";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));
        userService.deleteUser(userId, imagePath);
        Mockito.verify(userRepository, Mockito.times(1)).delete(user);
    }

    @Test
    public void getAllUsersTest() {
        User user1 = User.builder()
                .fullName("Vishal Singh Adhikari")
                .email("vishal@gmail.com")
                .about("This is testing get all users")
                .gender("Male")
                .userImageName("vishal.png")
                .password("vishal")
                .roles(List.of(role))
                .build();

        User user2 = User.builder()
                .fullName("Aditi Khanduja")
                .email("aditi@gmail.com")
                .about("This is testing get all users")
                .gender("Female")
                .userImageName("aditi.png")
                .password("aditi")
                .roles(List.of(role))
                .build();

        List<User> userList = Arrays.asList(user, user1, user2);
        Page<User> page = new PageImpl<>(userList);
        Mockito.when(userRepository.findAll(Mockito.<Pageable>any())).thenReturn(page);

        PageableResponse<UserDto> allUsers = userService.getAllUsers(1, 5, "fullName", "asc");

        Assertions.assertEquals(3, allUsers.getContent().size());
    }

    @Test
    public void getUserByIdTest() {
        String userId = "userIdTest";
        Mockito.when(userRepository.findById(userId)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserById(userId);
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getFullName(), userDto.getFullName(), "Full name not matched - retrieval of user by id failed!");
    }

    @Test
    public void getUserByEmailTest() {
        String email = "emailTest@xxx.com";
        Mockito.when(userRepository.findByEmail(email)).thenReturn(Optional.of(user));

        UserDto userDto = userService.getUserByEmail(email);
        Assertions.assertNotNull(userDto);
        Assertions.assertEquals(user.getFullName(), userDto.getFullName(), "Full name not matched - retrieval of user by email failed!");
    }

    @Test
    public void searchUsersTest() {
        User user1 = User.builder()
                .fullName("Aditi Khanduja")
                .email("aditi@gmail.com")
                .about("This is testing get all users")
                .gender("Female")
                .userImageName("aditi.png")
                .password("aditi")
                .roles(List.of(role))
                .build();

        String keyword = "t";
        List<User> userList = Arrays.asList(user, user1);
        Page<User> page = new PageImpl<>(userList);

        Mockito.when(userRepository.findByFullNameContaining(Mockito.eq(keyword), Mockito.<Pageable>any())).thenReturn(page);

        PageableResponse<UserDto> searchedUsers = userService.searchUsers(keyword, 1, 5, "fullName", "asc");

        Assertions.assertEquals(2, searchedUsers.getContent().size(), "No. of searched users not matched!");
    }

}
