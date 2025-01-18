package com.vishal.electronicsstore.dto;

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
public class UserDTO {

    private String userId;

    private String name;

    private String email;

    private String password;

    private String gender;

    private String about;

    private String imageName;

}
