package com.vishal.electronicsstore.controller;

import java.util.List;

import javax.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.vishal.electronicsstore.dto.APIResponseMessage;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.UserDTO;
import com.vishal.electronicsstore.service.UserService;

@RestController
@RequestMapping("/user")
public class UserController {

    private final UserService userService;

    @Autowired
    public UserController(UserService userService) {
        this.userService = userService;
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
        userService.deleteUser(userId);
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
    public ResponseEntity<List<UserDTO>> searchUsers(@PathVariable String keyword) {
        return ResponseEntity.ok(userService.searchUsers(keyword));
    }

}
