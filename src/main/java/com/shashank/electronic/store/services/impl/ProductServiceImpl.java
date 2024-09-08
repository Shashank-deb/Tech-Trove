package com.shashank.electronic.store.services.impl;

import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.dtos.ProductDTO;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.helper.Helper;
import com.shashank.electronic.store.models.Category;
import com.shashank.electronic.store.models.Product;
import com.shashank.electronic.store.repositories.CategoryRepository;
import com.shashank.electronic.store.repositories.ProductRepository;
import com.shashank.electronic.store.services.ProductService;
import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Date;
import java.util.List;
import java.util.UUID;


@Service
public class ProductServiceImpl implements ProductService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${user.product.image.path}")
    private String imagePath;


    private Logger logger = LoggerFactory.getLogger(ProductServiceImpl.class);

    @Override
    public ProductDTO createProduct(ProductDTO product) {

        Product product1 = mapper.map(product, Product.class);
        //generating the product id and date
        String productId = UUID.randomUUID().toString();
        product1.setProductId(productId);

        product1.setAddedDate(new Date());
        Product savedProduct = productRepository.save(product1);
        return mapper.map(savedProduct, ProductDTO.class);
    }

    @Override
    public ProductDTO getProductById(String productId) {
        Product product1 = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with given id"));
        return mapper.map(product1, ProductDTO.class);
    }

    @Override
    public PageableResponse<ProductDTO> getAllProducts(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findAll(pageable);
        PageableResponse<ProductDTO> response = Helper.getPageableResponse(page, ProductDTO.class);
        return response;
    }

    @Override
    public PageableResponse<ProductDTO> searchByTitle(int pageNumber, int pageSize, String sortBy, String sortDir, String subTitle) {

        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> byTitleContaining = productRepository.findByTitleContaining(subTitle, pageable);
        PageableResponse<ProductDTO> response = Helper.getPageableResponse(byTitleContaining, ProductDTO.class);
        return response;
    }

    @Override
    public ProductDTO updateProduct(ProductDTO product, String productId) {
        //fetch the product of giving productId
        Product product1 = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with given id"));

        //update the product
        product1.setTitle(product.getTitle());
        product1.setDescription(product.getDescription());
        product1.setPrice(product.getPrice());
        product1.setDiscountedPrice(product.getDiscountedPrice());
        product1.setQuantity(product.getQuantity());
        product1.setLive(product.isLive());
        product1.setStock(product.isStock());
        product1.setProductImageName(product.getProductImageName());
        Product updatedProduct = productRepository.save(product1);

        return mapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public void deleteProduct(String productId) {
        Product product1 = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with given id"));
        //delete user profile image
        //images/user/abc.png
        String fullPath = imagePath + product1.getProductImageName();
        logger.info("Image path : {} " + fullPath);

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            logger.info("No such file/directory exists");
            ex.printStackTrace();
        } catch (IOException ex) {
            ex.printStackTrace();
        }
        productRepository.delete(product1);

    }

    @Override
    public PageableResponse<ProductDTO> getAllLive(int pageNumber, int pageSize, String sortBy, String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> byLiveTrue = productRepository.findByLiveTrue(pageable);
        PageableResponse<ProductDTO> response = Helper.getPageableResponse(byLiveTrue, ProductDTO.class);
        return response;
    }

    @Override
    public ProductDTO createWithCategory(ProductDTO productDTO, String categoryId) {

        //fetch the category from database
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id"));
        Product product1 = mapper.map(productDTO, Product.class);
        //generating the product id and date
        String productId = UUID.randomUUID().toString();
        product1.setProductId(productId);
        product1.setAddedDate(new Date());
        product1.setCategory(category);
        Product savedProduct = productRepository.save(product1);
        return mapper.map(savedProduct, ProductDTO.class);


    }

    @Override
    public ProductDTO updateCategory(String productId, String categoryId) {

        Product product = productRepository.findById(productId).orElseThrow(() -> new ResourceNotFoundException("Product not found with given id"));
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id"));
        product.setCategory(category);
        Product updatedProduct = productRepository.save(product);
        return mapper.map(updatedProduct, ProductDTO.class);
    }

    @Override
    public PageableResponse<ProductDTO> getAllOfCategory(String categoryId, int pageNumber, int pageSize, String sortBy, String sortDir) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found with given id"));
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Product> page = productRepository.findByCategory(category, pageable);
        return Helper.getPageableResponse(page, ProductDTO.class);
    }
}
