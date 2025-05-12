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

import com.vishal.electronicsstore.dto.CategoryDto;
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
    public CategoryDto create(CategoryDto categoryDto) {
        String categoryId = UUID.randomUUID().toString();
        categoryDto.setCategoryId(categoryId);
        Category category = dtoToEntity(categoryDto);
        Category savedCategory = categoryRepository.save(category);
        return entityToDto(savedCategory);
    }

    @Override
    public CategoryDto update(CategoryDto categoryDto, String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        category.setTitle(categoryDto.getTitle());
        category.setDescription(categoryDto.getDescription());
        category.setCategoryImage(categoryDto.getCategoryImage());
        Category updatedCategory = categoryRepository.save(category);
        return entityToDto(updatedCategory);
    }

    @Override
    public void delete(String categoryId, String imagePath) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        Path path = Paths.get(imagePath, category.getCategoryImage());
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Category image not found in folder!");
            e.printStackTrace();
        }

        categoryRepository.delete(category);
    }

    @Override
    public PageableResponse<CategoryDto> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Category> categoriesPage = categoryRepository.findAll(pageable);
        return PageableUtil.getPageableResponse(categoriesPage, CategoryDto.class, modelMapper);
    }

    @Override
    public CategoryDto get(String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        return entityToDto(category);
    }

    @Override
    public PageableResponse<CategoryDto> searchCategories(
            String keyword,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Category> categoriesPage = categoryRepository.findByTitleContaining(keyword, pageable);
        return PageableUtil.getPageableResponse(categoriesPage, CategoryDto.class, modelMapper);
    }

    private Category dtoToEntity(CategoryDto categoryDto) {
        return modelMapper.map(categoryDto, Category.class);
    }

    private CategoryDto entityToDto(Category savedCategory) {
        return modelMapper.map(savedCategory, CategoryDto.class);
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
