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
import com.vishal.electronicsstore.dto.ProductDto;
import com.vishal.electronicsstore.dto.ImageResponse;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.service.ProductService;
import com.vishal.electronicsstore.service.FileService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/products")
@Slf4j
public class ProductController {

    @Value("${product.image.path}")
    private String imagePath;

    private final ProductService productService;
    private final FileService fileService;

    @Autowired
    public ProductController(ProductService productService, FileService fileService) {
        this.productService = productService;
        this.fileService = fileService;
    }

    @PostMapping
    public ResponseEntity<ProductDto> createProduct(@Valid @RequestBody ProductDto productDto) {
        ProductDto savedProductDto = productService.create(productDto);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductDto);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDto> updateProduct(
            @RequestBody ProductDto productDto,
            @PathVariable String productId) {
        ProductDto updatedProductDto = productService.update(productDto, productId);
        return ResponseEntity.ok(updatedProductDto);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<ApiResponseMessage> deleteProduct(@PathVariable String productId) {
        productService.delete(productId, imagePath);
        ApiResponseMessage responseMessage = ApiResponseMessage.builder()
                .message("Product deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<ProductDto>> getAllProducts(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(productService.getAll(pageNumber, pageSize, sortBy, sortDirec));
    }

    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDto>> getAllLiveProducts(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(productService.getAllLive(pageNumber, pageSize, sortBy, sortDirec));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDto> getProduct(@PathVariable String productId) {
        ProductDto productDto = productService.get(productId);
        return ResponseEntity.ok(productDto);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<ProductDto>> searchProducts(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageNumber, pageSize, sortBy, sortDirec));
    }

    @PostMapping(value = "/image/{productId}", consumes = "multipart/form-data")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam MultipartFile productImage) throws IOException {
        String imageName = fileService.uploadFile(productImage, imagePath);

        ProductDto productDto = productService.get(productId);
        productDto.setProductImage(imageName);
        productService.update(productDto, productId);

        ImageResponse imageResponse = ImageResponse.builder()
                .imageName(imageName)
                .message("Image uploaded successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .build();
        return ResponseEntity.status(HttpStatus.CREATED).body(imageResponse);
    }

    @GetMapping("/image/{productId}")
    public void serveProductImage(
            @PathVariable String productId,
            HttpServletResponse response) throws IOException {
        ProductDto productDto = productService.get(productId);
        log.info("Product image name : {}", productDto.getProductImage());

        InputStream resource = fileService.getResource(imagePath, productDto.getProductImage());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

}
