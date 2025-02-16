package com.vishal.electronicsstore.controller;

import java.io.IOException;
import java.io.InputStream;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;

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

import com.vishal.electronicsstore.dto.APIResponseMessage;
import com.vishal.electronicsstore.dto.CategoryDTO;
import com.vishal.electronicsstore.dto.ImageResponse;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.ProductDTO;
import com.vishal.electronicsstore.service.CategoryService;
import com.vishal.electronicsstore.service.FileService;
import com.vishal.electronicsstore.service.ProductService;

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
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO savedCategoryDTO = categoryService.create(categoryDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedCategoryDTO);
    }

    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(
            @RequestBody CategoryDTO categoryDTO,
            @PathVariable String categoryId) {
        CategoryDTO updatedCategoryDTO = categoryService.update(categoryDTO, categoryId);
        return ResponseEntity.ok(updatedCategoryDTO);
    }

    @DeleteMapping("/{categoryId}")
    public ResponseEntity<APIResponseMessage> deleteCategory(@PathVariable String categoryId) {
        categoryService.delete(categoryId, imagePath);
        APIResponseMessage responseMessage = APIResponseMessage.builder()
                .message("Category deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDTO>> getAllCategories(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(categoryService.getAll(pageNumber, pageSize, sortBy, sortDirec));
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getCategory(@PathVariable String categoryId) {
        CategoryDTO categoryDTO = categoryService.get(categoryId);
        return ResponseEntity.ok(categoryDTO);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<CategoryDTO>> searchCategories(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(categoryService.searchCategories(keyword, pageNumber, pageSize, sortBy, sortDirec));
    }

    @PostMapping("/image/{categoryId}")
    public ResponseEntity<ImageResponse> uploadCategoryImage(
            @PathVariable String categoryId,
            @RequestParam MultipartFile categoryImage) throws IOException {
        String imageName = fileService.uploadFile(categoryImage, imagePath);

        CategoryDTO categoryDTO = categoryService.get(categoryId);
        categoryDTO.setCategoryImage(imageName);
        categoryService.update(categoryDTO, categoryId);

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
        CategoryDTO categoryDTO = categoryService.get(categoryId);
        log.info("Category image name : {}", categoryDTO.getCategoryImage());

        InputStream resource = fileService.getResource(imagePath, categoryDTO.getCategoryImage());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

    @PostMapping("/{categoryId}/product")
    public ResponseEntity<ProductDTO> createProductWithCategory(
            @PathVariable String categoryId,
            @RequestBody ProductDTO productDTO) {

        ProductDTO productWithCategory = productService.createProductWithCategory(productDTO, categoryId);

        return ResponseEntity.status(HttpStatus.CREATED).body(productWithCategory);
    }

    @PutMapping("/{categoryId}/product/{productId}")
    public ResponseEntity<ProductDTO> updateCategoryOfProduct(
            @PathVariable String categoryId,
            @PathVariable String productId) {

        ProductDTO updatedProduct = productService.updateCategoryOfProduct(productId, categoryId);

        return ResponseEntity.ok(updatedProduct);
    }

    @GetMapping("/{categoryId}/product")
    public ResponseEntity<PageableResponse<ProductDTO>> getAllProductsOfACategory(
            @PathVariable String categoryId,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {

        return ResponseEntity.ok(productService.getAllProductsOfACategory(
                categoryId, pageNumber, pageSize, sortBy, sortDirec));
    }

}
