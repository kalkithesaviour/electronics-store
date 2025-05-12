package com.vishal.electronicsstore.service;

import com.vishal.electronicsstore.dto.CategoryDto;
import com.vishal.electronicsstore.dto.PageableResponse;

public interface CategoryService {

    CategoryDto create(CategoryDto categoryDto);

    CategoryDto update(CategoryDto categoryDto, String categoryId);

    void delete(String categoryId, String imagePath);

    PageableResponse<CategoryDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDirec);

    CategoryDto get(String categoryId);

    PageableResponse<CategoryDto> searchCategories(String keyword, int pageNumber, int pageSize, String sortBy,
            String sortDirec);

}
