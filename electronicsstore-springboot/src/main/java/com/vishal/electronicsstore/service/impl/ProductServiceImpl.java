package com.vishal.electronicsstore.service.impl;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.UUID;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.ProductDto;
import com.vishal.electronicsstore.entity.Category;
import com.vishal.electronicsstore.entity.Product;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.repository.CategoryRepository;
import com.vishal.electronicsstore.repository.ProductRepository;
import com.vishal.electronicsstore.service.ProductService;
import com.vishal.electronicsstore.util.PageableUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found: ";
    private static final String CATEGORY_NOT_FOUND_MESSAGE = "Category not found: ";

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;
    private final CategoryRepository categoryRepository;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper,
            CategoryRepository categoryRepository) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
        this.categoryRepository = categoryRepository;
    }

    @Override
    public ProductDto create(ProductDto productDto) {
        String productId = UUID.randomUUID().toString();
        productDto.setProductId(productId);
        productDto.setAddedDate(new Date());
        Product product = dtoToEntity(productDto);
        Product savedProduct = productRepository.save(product);
        return entityToDto(savedProduct);
    }

    @Override
    public ProductDto update(ProductDto productDto, String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));

        product.setTitle(productDto.getTitle());
        product.setDescription(productDto.getDescription());
        product.setPrice(productDto.getPrice());
        product.setDiscountedPrice(productDto.getDiscountedPrice());
        product.setQuantity(productDto.getQuantity());
        product.setLive(productDto.isLive());
        product.setStock(productDto.isStock());
        product.setProductImage(productDto.getProductImage());

        Product updatedProduct = productRepository.save(product);
        return entityToDto(updatedProduct);
    }

    @Override
    public void delete(String productId, String imagePath) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));

        Path path = Paths.get(imagePath, product.getProductImage());
        try {
            Files.delete(path);
        } catch (IOException e) {
            log.error("Product image not found in folder!");
            e.printStackTrace();
        }

        productRepository.delete(product);
    }

    @Override
    public ProductDto get(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));

        return entityToDto(product);
    }

    @Override
    public PageableResponse<ProductDto> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Product> productsPage = productRepository.findAll(pageable);
        return PageableUtil.getPageableResponse(productsPage, ProductDto.class, modelMapper);
    }

    @Override
    public PageableResponse<ProductDto> getAllLive(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Product> productsPage = productRepository.findByLiveTrue(pageable);
        return PageableUtil.getPageableResponse(productsPage, ProductDto.class, modelMapper);
    }

    @Override
    public PageableResponse<ProductDto> searchProducts(
            String keyword,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Product> productsPage = productRepository.findByTitleContaining(keyword, pageable);
        return PageableUtil.getPageableResponse(productsPage, ProductDto.class, modelMapper);
    }

    private Product dtoToEntity(ProductDto productDto) {
        return modelMapper.map(productDto, Product.class);
    }

    private ProductDto entityToDto(Product savedProduct) {
        return modelMapper.map(savedProduct, ProductDto.class);
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

    @Override
    public ProductDto createProductWithCategory(ProductDto productDto, String categoryId) {
        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        Product product = dtoToEntity(productDto);

        String productId = UUID.randomUUID().toString();
        product.setProductId(productId);
        product.setAddedDate(new Date());
        product.setCategory(category);

        Product savedProduct = productRepository.save(product);
        return entityToDto(savedProduct);
    }

    @Override
    public ProductDto updateCategoryOfProduct(String productId, String categoryId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        product.setCategory(category);
        Product savedProduct = productRepository.save(product);
        return entityToDto(savedProduct);
    }

    @Override
    public PageableResponse<ProductDto> getAllProductsOfACategory(
            String categoryId,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {

        Category category = categoryRepository.findById(categoryId)
                .orElseThrow(() -> new ResourceNotFoundException(CATEGORY_NOT_FOUND_MESSAGE + categoryId));

        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Product> productsPage = productRepository.findByCategory(category, pageable);
        return PageableUtil.getPageableResponse(productsPage, ProductDto.class, modelMapper);
    }

}
