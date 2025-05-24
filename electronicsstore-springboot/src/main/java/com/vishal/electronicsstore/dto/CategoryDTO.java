package com.vishal.electronicsstore.dto;

import com.fasterxml.jackson.annotation.JsonProperty;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class CategoryDto {

    @JsonProperty(access = JsonProperty.Access.READ_ONLY)
    private String categoryId;

    @NotBlank(message = "Title required!")
    @Size(min = 4, message = "Title must be of minimum 4 characters!")
    private String title;

    @NotBlank(message = "Description required!")
    private String description;

    private String categoryImage;

}
