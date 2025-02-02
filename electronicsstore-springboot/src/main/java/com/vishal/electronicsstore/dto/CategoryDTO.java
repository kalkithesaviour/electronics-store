package com.vishal.electronicsstore.dto;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

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
public class CategoryDTO {

    private String categoryId;

    @NotBlank(message = "Title required!")
    @Size(min = 4, message = "Title must be of minimum 4 characters!")
    private String title;

    @NotBlank(message = "Description required!")
    private String description;

    private String categoryImage;

}
