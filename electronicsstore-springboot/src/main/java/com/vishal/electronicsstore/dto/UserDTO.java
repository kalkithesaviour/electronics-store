package com.vishal.electronicsstore.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import java.util.List;

import com.fasterxml.jackson.annotation.JsonProperty;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Schema
public class UserDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userId;

    @Size(min = 3, max = 25, message = "Invalid full name!")
    private String fullName;

    @Email(message = "Invalid email!")
    @NotBlank(message = "Email is required!")
    private String email;

    @NotBlank(message = "Password is required!")
    private String password;

    @Size(min = 4, max = 6, message = "Invalid gender!")
    private String gender;

    @NotBlank(message = "Write something about yourself!")
    private String about;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private List<RoleDto> roles;

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String userImageName;

}
