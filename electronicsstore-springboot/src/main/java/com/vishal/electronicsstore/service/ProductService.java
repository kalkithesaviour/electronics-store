package com.vishal.electronicsstore.service;

import com.vishal.electronicsstore.dto.PageableResponse;
import com.vishal.electronicsstore.dto.ProductDTO;

public interface ProductService {

    ProductDTO create(ProductDTO productDTO);

    ProductDTO update(ProductDTO productDTO, String productId);

    void delete(String productId, String imagePath);

    PageableResponse<ProductDTO> getAll(int pageNumber, int pageSize, String sortBy, String sortDirec);

    PageableResponse<ProductDTO> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDirec);

    ProductDTO get(String productId);

    PageableResponse<ProductDTO> searchProducts(String keyword, int pageNumber, int pageSize, String sortBy,
            String sortDirec);

    ProductDTO createProductWithCategory(ProductDTO productDTO, String categoryId);

    ProductDTO updateCategoryOfProduct(String productId, String categoryId);

    PageableResponse<ProductDTO> getAllProductsOfACategory(String categoryId, int pageNumber, int pageSize,
            String sortBy, String sortDirec);

}
