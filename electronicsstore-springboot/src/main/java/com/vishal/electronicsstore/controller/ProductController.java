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
import com.vishal.electronicsstore.dto.ProductDTO;
import com.vishal.electronicsstore.dto.ImageResponse;
import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.service.ProductService;
import com.vishal.electronicsstore.service.FileService;

import lombok.extern.slf4j.Slf4j;

@RestController
@RequestMapping("/product")
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
    public ResponseEntity<ProductDTO> createProduct(@Valid @RequestBody ProductDTO productDTO) {
        ProductDTO savedProductDTO = productService.create(productDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(savedProductDTO);
    }

    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(
            @RequestBody ProductDTO productDTO,
            @PathVariable String productId) {
        ProductDTO updatedProductDTO = productService.update(productDTO, productId);
        return ResponseEntity.ok(updatedProductDTO);
    }

    @DeleteMapping("/{productId}")
    public ResponseEntity<APIResponseMessage> deleteProduct(@PathVariable String productId) {
        productService.delete(productId, imagePath);
        APIResponseMessage responseMessage = APIResponseMessage.builder()
                .message("Product deleted successfully")
                .status(HttpStatus.OK)
                .success(true)
                .build();
        return ResponseEntity.ok(responseMessage);
    }

    @GetMapping
    public ResponseEntity<PageableResponse<ProductDTO>> getAllProducts(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(productService.getAll(pageNumber, pageSize, sortBy, sortDirec));
    }

    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDTO>> getAllLiveProducts(
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(productService.getAllLive(pageNumber, pageSize, sortBy, sortDirec));
    }

    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable String productId) {
        ProductDTO productDTO = productService.get(productId);
        return ResponseEntity.ok(productDTO);
    }

    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<ProductDTO>> searchProducts(
            @PathVariable String keyword,
            @RequestParam(defaultValue = "0", required = false) int pageNumber,
            @RequestParam(defaultValue = "5", required = false) int pageSize,
            @RequestParam(defaultValue = "title", required = false) String sortBy,
            @RequestParam(defaultValue = "asc", required = false) String sortDirec) {
        return ResponseEntity.ok(productService.searchProducts(keyword, pageNumber, pageSize, sortBy, sortDirec));
    }

    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponse> uploadProductImage(
            @PathVariable String productId,
            @RequestParam MultipartFile productImage) throws IOException {
        String imageName = fileService.uploadFile(productImage, imagePath);

        ProductDTO productDTO = productService.get(productId);
        productDTO.setProductImage(imageName);
        productService.update(productDTO, productId);

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
        ProductDTO productDTO = productService.get(productId);
        log.info("Product image name : {}", productDTO.getProductImage());

        InputStream resource = fileService.getResource(imagePath, productDTO.getProductImage());

        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());
    }

}
