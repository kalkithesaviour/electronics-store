package com.vishal.electronicsstore.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.CategoryDTO;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.entity.Category;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.repository.CategoryRepository;
import com.vishal.electronicsstore.service.CategoryService;
import com.vishal.electronicsstore.util.PageableUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class CategoryServiceImpl implements CategoryService {

    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found: ";

    private final CategoryRepository categoryRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public CategoryServiceImpl(CategoryRepository categoryRepository, ModelMapper modelMapper) {
        this.categoryRepository = categoryRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
        String categoryId = UUID.randomUUID().toString();
        categoryDTO.setCategoryId(categoryId);
        Category category = dtoToEntity(categoryDTO);
        Category savedCategory = categoryRepository.save(category);
        return entityToDto(savedCategory);
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO, String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        category.setTitle(categoryDTO.getTitle());
        category.setDescription(categoryDTO.getDescription());
        category.setCoverImage(categoryDTO.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);
        return entityToDto(updatedCategory);
    }

    @Override
    public void delete(String categoryId, String imagePath) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        Path path = Paths.get(imagePath, category.getCoverImage());
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Category image not found in folder!");
            e.printStackTrace();
        }

        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDTO> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        return PageableUtil.getPageableResponse(categoriesPage, CategoryDTO.class, modelMapper);
    }

    @Override
    public CategoryDTO get(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        return entityToDto(category);
    }

    @Override
    public PageableResponse<CategoryDTO> searchCategories(
            String keyword,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Category> categoriesPage = categoryRepository.findByTitleContaining(keyword, pageable);
        return PageableUtil.getPageableResponse(categoriesPage, CategoryDTO.class, modelMapper);
    }

    private Category dtoToEntity(CategoryDTO categoryDTO) {
        return modelMapper.map(categoryDTO, Category.class);
    }

    private CategoryDTO entityToDto(Category savedCategory) {
        return modelMapper.map(savedCategory, CategoryDTO.class);
    }

    private Pageable createPageable(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Sort sort = sortDirec.equalsIgnoreCase("desc")
                ? Sort.by(sortBy).descending()
                : Sort.by(sortBy).ascending();
        return PageRequest.of(pageNumber, pageSize, sort);
    }

}
