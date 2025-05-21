package com.vishal.electronicsstore.controller;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.request.MockMvcRequestBuilders;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Arrays;

import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.vishal.electronicsstore.config.SecurityConfig;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDto;
import com.vishal.electronicsstore.repository.RoleRepository;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.security.JwtAuthenticationEntryPoint;
import com.vishal.electronicsstore.security.JwtHelper;
import com.vishal.electronicsstore.service.FileService;
import com.vishal.electronicsstore.service.UserService;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@WebMvcTest(UserController.class)
@Import(SecurityConfig.class)
@ActiveProfiles("test")
@Slf4j
public class UserControllerTests {

    @MockBean
    private UserService userService;

    @MockBean
    private FileService fileService;

    @MockBean
    private JwtHelper jwtHelper;

    @MockBean
    private RoleRepository roleRepository;

    @MockBean
    private UserRepository userRepository;

    @MockBean
    private PasswordEncoder passwordEncoder;

    @MockBean
    private JwtAuthenticationEntryPoint jwtAuthenticationEntryPoint;

    private final MockMvc mockMvc;

    @Autowired
    public UserControllerTests(MockMvc mockMvc) {
        this.mockMvc = mockMvc;
    }

    @BeforeAll
    static void loadEnv() {
        Dotenv dotenv = Dotenv.configure()
                .directory("../")
                .load();

        dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
    }

    private UserDto userDto;

    @BeforeEach
    public void init() {
        userDto = UserDto.builder()
                .fullName("Vedanti Gori")
                .email("vedanti@gmail.com")
                .about("This is Vedanti")
                .gender("Female")
                .userImageName("vedanti.png")
                .password("vedanti")
                .build();
    }

    private String convertObjectToJsonString(Object userDto) {
        try {
            return new ObjectMapper().writeValueAsString(userDto);
        } catch (Exception e) {
            log.error("Error serializing userDto", e);
            return null;
        }
    }

    @Test
    public void createUserTest() throws Exception {
        Mockito.when(userService.createUser(Mockito.any())).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.post("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(userDto))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.fullName").exists());
    }

    @WithMockUser(username = "admin", roles = { "USER" })
    @Test
    public void updateUserTest() throws Exception {
        String userId = "userIdTest";
        Mockito.when(userService.updateUser(Mockito.any(), Mockito.eq(userId))).thenReturn(userDto);

        mockMvc.perform(MockMvcRequestBuilders.put("/users/" + userId)
                .contentType(MediaType.APPLICATION_JSON)
                .content(convertObjectToJsonString(userDto))
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.fullName").exists());
    }

    @Test
    public void getAllUsersTest() throws Exception {
        UserDto userDto1 = UserDto.builder()
                .fullName("Vishal Singh Adhikari")
                .email("vishal@gmail.com")
                .about("This is Vishal")
                .gender("Male")
                .userImageName("vishal.png")
                .password("vishal")
                .build();

        UserDto userDto2 = UserDto.builder()
                .fullName("Aditi Khanduja")
                .email("aditi@gmail.com")
                .about("This is Aditi")
                .gender("Female")
                .userImageName("aditi.png")
                .password("aditi")
                .build();

        PageableResponse<UserDto> pageableResponse = new PageableResponse<>();
        pageableResponse.setContent(Arrays.asList(userDto, userDto1, userDto2));
        pageableResponse.setLastPage(false);
        pageableResponse.setPageSize(10);
        pageableResponse.setPageNumber(100);
        pageableResponse.setTotalElements(1000);

        Mockito.when(
                userService.getAllUsers(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyString(), Mockito.anyString()))
                .thenReturn(pageableResponse);

        mockMvc.perform(MockMvcRequestBuilders.get("/users")
                .contentType(MediaType.APPLICATION_JSON)
                .accept(MediaType.APPLICATION_JSON))
                .andDo(print())
                .andExpect(status().isOk());
    }

}
