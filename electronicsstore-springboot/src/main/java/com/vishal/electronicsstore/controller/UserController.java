package com.vishal.electronicsstore.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vishal.electronicsstore.dto.APIResponseMessage;
import com.vishal.electronicsstore.dto.ImageResponse;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDTO;
import com.vishal.electronicsstore.service.FileService;
import com.vishal.electronicsstore.service.UserService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/users")
@Slf4j
public class UserController {

    @Value("${user.image.path}")
    private String imagePath;

    private final UserService userService;
    private final FileService fileService;

    @Autowired
    public UserController(UserService userService, FileService fileService) {
        this.userService = userService;
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<UserDTO> createUser(@Valid @RequestBody UserDTO userDTO) {
        UserDTO savedUserDTO = userService.createUser(userDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedUserDTO);
    }

    @PutMapping("/{userId}")
    public ResponseEntity<UserDTO> updateUser(
            @Valid @RequestBody UserDTO userDTO,
            @PathVariable String userId) {
        UserDTO updatedUserDTO = userService.updateUser(userDTO, userId);
        return ResponseEntity.ok(updatedUserDTO);
    }

    @DeleteMapping("/{userId}")
    public ResponseEntity<APIResponseMessage> deleteUser(@PathVariable String userId) {
        userService.deleteUser(userId, imagePath);
        APIResponseMessage message = APIResponseMessage.builder()
                .message("User deleted successfully")
                .success(true)
                .status(HttpStatus.OK)
                .build();
        return ResponseEntity.ok(message);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<UserDTO>> getAllUsers(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "name", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(userService.getAllUsers(pageNumber, pageSize, sortBy, sortDirec));
    }

    @GetMapping("/{userId}")
    public ResponseEntity<UserDTO> getUserById(@PathVariable String userId) {
        return ResponseEntity.ok(userService.getUserById(userId));
    }

    @GetMapping("/email/{email}")
    public ResponseEntity<UserDTO> getUserByEmail(@PathVariable String email) {
        return ResponseEntity.ok(userService.getUserByEmail(email));
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<UserDTO>> searchUsers(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "name", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(userService.searchUsers(keyword, pageNumber, pageSize, sortBy, sortDirec));
    }

    @PostMapping("/image/{userId}")
    public ResponseEntity<ImageResponse> uploadUserImage(
            @PathVariable String userId,
            @RequestParam MultipartFile userImage) throws IOException {
        String imageName = fileService.uploadFile(userImage, imagePath);

        UserDTO userDTO = userService.getUserById(userId);
        userDTO.setUserImageName(imageName);
        userService.updateUser(userDTO, userId);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .message("Image uploaded successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(imageResponse);
    }

    @GetMapping("/image/{userId}")
    public void serveUserImage(
            @PathVariable String userId,
            HttpServletResponse response) throws IOException {
        UserDTO userDTO = userService.getUserById(userId);
        log.info("User image name : {}", userDTO.getUserImageName());

        InputStream resource = fileService.getResource(imagePath, userDTO.getUserImageName());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

}
