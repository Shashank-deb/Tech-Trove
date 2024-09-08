package com.shashank.electronic.store.services;

import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.dtos.ProductDTO;
import com.shashank.electronic.store.models.Product;

public interface ProductService {
    ProductDTO createProduct(ProductDTO product);

    ProductDTO getProductById(String productId);

    PageableResponse<ProductDTO> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir);

    PageableResponse<ProductDTO> searchByTitle(int pageNumber, int pageSize, String sortBy, String sortDir,String subTitle);

    ProductDTO updateProduct(ProductDTO product, String productId);

    void deleteProduct(String productId);

    PageableResponse<ProductDTO> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir);

//    create product with category

    ProductDTO createWithCategory(ProductDTO productDTO,String categoryId);


//    update category of product
    ProductDTO updateCategory(String productId,String categoryId);


   PageableResponse<ProductDTO> getAllOfCategory(String categoryId,int pageNumber,int pageSize,String sortBy,String sortDir);

}
