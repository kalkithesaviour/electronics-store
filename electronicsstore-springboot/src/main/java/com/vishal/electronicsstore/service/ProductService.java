package com.vishal.electronicsstore.service;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.ProductDto;

public interface ProductService {

    ProductDto create(ProductDto productDto);

    ProductDto update(ProductDto productDto, String productId);

    void delete(String productId, String imagePath);

    PageableResponse<ProductDto> getAll(int pageNumber, int pageSize, String sortBy, String sortDirec);

    PageableResponse<ProductDto> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDirec);

    ProductDto get(String productId);

    PageableResponse<ProductDto> searchProducts(String keyword, int pageNumber, int pageSize, String sortBy,
            String sortDirec);

    ProductDto createProductWithCategory(ProductDto productDto, String categoryId);

    ProductDto updateCategoryOfProduct(String productId, String categoryId);

    PageableResponse<ProductDto> getAllProductsOfACategory(String categoryId, int pageNumber, int pageSize,
            String sortBy, String sortDirec);

}
