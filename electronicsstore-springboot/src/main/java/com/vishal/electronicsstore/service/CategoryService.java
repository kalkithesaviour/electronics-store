package com.vishal.electronicsstore.service;

import java.util.List;

import com.vishal.electronicsstore.dto.CategoryDTO;
import com.vishal.electronicsstore.dto.PageableResponse;

public interface CategoryService {

    CategoryDTO create(CategoryDTO categoryDTO);

    CategoryDTO update(CategoryDTO categoryDTO, String categoryId);

    void delete(String categoryId, String imagePath);

    PageableResponse<CategoryDTO> getAll(int pageNumber, int pageSize, String sortBy, String sortDirec);

    CategoryDTO get(String categoryId);

    List<CategoryDTO> searchCategories(String keyword);

}
