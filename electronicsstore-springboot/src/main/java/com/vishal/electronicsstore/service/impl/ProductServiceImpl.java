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
import com.vishal.electronicsstore.dto.ProductDTO;
import com.vishal.electronicsstore.entity.Product;
import com.vishal.electronicsstore.exception.ResourceNotFoundException;
import com.vishal.electronicsstore.repository.ProductRepository;
import com.vishal.electronicsstore.service.ProductService;
import com.vishal.electronicsstore.util.PageableUtil;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class ProductServiceImpl implements ProductService {

    private static final String PRODUCT_NOT_FOUND_MESSAGE = "Product not found: ";

    private final ProductRepository productRepository;
    private final ModelMapper modelMapper;

    @Autowired
    public ProductServiceImpl(ProductRepository productRepository, ModelMapper modelMapper) {
        this.productRepository = productRepository;
        this.modelMapper = modelMapper;
    }

    @Override
    public ProductDTO create(ProductDTO productDTO) {
        String productId = UUID.randomUUID().toString();
        productDTO.setProductId(productId);
        productDTO.setAddedDate(new Date());
        Product product = dtoToEntity(productDTO);
        Product savedProduct = productRepository.save(product);
        return entityToDto(savedProduct);
    }

    @Override
    public ProductDTO update(ProductDTO productDTO, String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));

        product.setTitle(productDTO.getTitle());
        product.setDescription(productDTO.getDescription());
        product.setPrice(productDTO.getPrice());
        product.setDiscountedPrice(productDTO.getDiscountedPrice());
        product.setQuantity(productDTO.getQuantity());
        product.setLive(productDTO.isLive());
        product.setStock(productDTO.isStock());
        product.setProductImage(productDTO.getProductImage());

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
    public ProductDTO get(String productId) {
        Product product = productRepository.findById(productId)
                .orElseThrow(() -> new ResourceNotFoundException(PRODUCT_NOT_FOUND_MESSAGE + productId));

        return entityToDto(product);
    }

    @Override
    public PageableResponse<ProductDTO> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Product> productsPage = productRepository.findAll(pageable);
        return PageableUtil.getPageableResponse(productsPage, ProductDTO.class, modelMapper);
    }

    @Override
    public PageableResponse<ProductDTO> getAllLive(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Product> productsPage = productRepository.findByLiveTrue(pageable);
        return PageableUtil.getPageableResponse(productsPage, ProductDTO.class, modelMapper);
    }

    @Override
    public PageableResponse<ProductDTO> searchProducts(
            String keyword,
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDirec) {
        Pageable pageable = createPageable(pageNumber, pageSize, sortBy, sortDirec);
        Page<Product> productsPage = productRepository.findByTitleContaining(keyword, pageable);
        return PageableUtil.getPageableResponse(productsPage, ProductDTO.class, modelMapper);
    }

    private Product dtoToEntity(ProductDTO productDTO) {
        return modelMapper.map(productDTO, Product.class);
    }

    private ProductDTO entityToDto(Product savedProduct) {
        return modelMapper.map(savedProduct, ProductDTO.class);
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
