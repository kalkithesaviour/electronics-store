package com.vishal.electronicsstore.dto;

import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import com.vishal.electronicsstore.validator.ImageName;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class UserDTO {

    private String userId;

    @Size(min = 3, max = 25, message = "Invalid name!")
    private String name;

    @Email(message = "Invalid email!")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Password is required!")
    private String password;

    @Size(min = 4, max = 6, message = "Invalid gender!")
    private String gender;

    @NotBlank(message = "Write something about yourself!")
    private String about;

    @ImageName
    private String userImageName;

}
