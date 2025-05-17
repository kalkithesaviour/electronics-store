package com.vishal.electronicsstore.controller;

import java.io.IOException;
import java.io.InputStream;

import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.vishal.electronicsstore.dto.ApiResponseMessage;
import com.vishal.electronicsstore.dto.CategoryDto;
import com.vishal.electronicsstore.dto.ImageResponse;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.ProductDto;
import com.vishal.electronicsstore.service.CategoryService;
import com.vishal.electronicsstore.service.FileService;
import com.vishal.electronicsstore.service.ProductService;

import io.swagger.v3.oas.annotations.Operation;
import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/categories")
@Slf4j
public class CategoryController {

    @Value("${category.image.path}")
    private String imagePath;

    private final CategoryService categoryService;
    private final FileService fileService;
    private final ProductService productService;

    @Autowired
    public CategoryController(CategoryService categoryService, FileService fileService,
            ProductService productService) {
        this.categoryService = categoryService;
        this.fileService = fileService;
        this.productService = productService;
    }

    @PostMapping
    public ResponseEntity<CategoryDto> createCategory(@Valid @RequestBody CategoryDto categoryDto) {
        CategoryDto savedCategoryDto = categoryService.create(categoryDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDto);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> updateCategory(
            @RequestBody CategoryDto categoryDto,
            @PathVariable String categoryId) {
        CategoryDto updatedCategoryDto = categoryService.update(categoryDto, categoryId);
        return ResponseEntity.ok(updatedCategoryDto);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<ApiResponseMessage> deleteCategory(@PathVariable String categoryId) {
        categoryService.delete(categoryId, imagePath);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("Category deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDto>> getAllCategories(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(categoryService.getAll(pageNumber, pageSize, sortBy, sortDirec));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDto> getCategory(@PathVariable String categoryId) {
        CategoryDto categoryDto = categoryService.get(categoryId);
        return ResponseEntity.ok(categoryDto);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<CategoryDto>> searchCategories(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(categoryService.searchCategories(keyword, pageNumber, pageSize, sortBy, sortDirec));
    }

    @PostMapping(value = "/image/{categoryId}", consumes = "multipart/form-data")
    @Operation(summary = "Upload category image")
    public ResponseEntity<ImageResponse> uploadCategoryImage(
            @PathVariable String categoryId,
            @RequestParam MultipartFile categoryImage) throws IOException {
        String imageName = fileService.uploadFile(categoryImage, imagePath);

        CategoryDto categoryDto = categoryService.get(categoryId);
        categoryDto.setCategoryImage(imageName);
        categoryService.update(categoryDto, categoryId);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .message("Image uploaded successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(imageResponse);
    }

    @GetMapping("/image/{categoryId}")
    public void serveCategoryImage(
            @PathVariable String categoryId,
            HttpServletResponse response) throws IOException {
        CategoryDto categoryDto = categoryService.get(categoryId);
        log.info("Category image name : {}", categoryDto.getCategoryImage());

        InputStream resource = fileService.getResource(imagePath, categoryDto.getCategoryImage());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    @PostMapping("/{categoryId}/product")
    public ResponseEntity<ProductDto> createProductWithCategory(
            @PathVariable String categoryId,
            @RequestBody ProductDto productDto) {

        ProductDto productWithCategory = productService.createProductWithCategory(productDto, categoryId);

        return ResponseEntity.status(HttpStatus.CREATED).body(productWithCategory);
    }

    @PutMapping("/{categoryId}/product/{productId}")
    public ResponseEntity<ProductDto> updateCategoryOfProduct(
            @PathVariable String categoryId,
            @PathVariable String productId) {

        ProductDto updatedProduct = productService.updateCategoryOfProduct(productId, categoryId);

        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{categoryId}/product")
    public ResponseEntity<PageableResponse<ProductDto>> getAllProductsOfACategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {

        return ResponseEntity.ok(productService.getAllProductsOfACategory(
                categoryId, pageNumber, pageSize, sortBy, sortDirec));
    }

}
