package com.shashank.electronic.store.controllers;


import com.shashank.electronic.store.dtos.*;
import com.shashank.electronic.store.services.CategoryService;
import com.shashank.electronic.store.services.FileService;
import com.shashank.electronic.store.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;


@RestController
@RequestMapping("/categories")
@Tag(name = "CategoryController", description = "REST APIs related to perform Category operations")

public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private ProductService productService;

    @Autowired
    private FileService fileService;


    @Value("${user.category.image.path}")
    private String imageUploadPath;

    private Logger logger = LoggerFactory.getLogger(CategoryController.class);

    @PostMapping
    public ResponseEntity<CategoryDTO> createCategory(@Valid @RequestBody CategoryDTO categoryDTO) {
        CategoryDTO categoryDTO1 = categoryService.create(categoryDTO);
        return new ResponseEntity<>(categoryDTO1, HttpStatus.CREATED);
    }


    @PutMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> updateCategory(@RequestBody CategoryDTO categoryDTO, @PathVariable("categoryId") String categoryId) {
        CategoryDTO updatedCategory = categoryService.update(categoryDTO, categoryId);
        return new ResponseEntity<>(updatedCategory, HttpStatus.OK);
    }


    @DeleteMapping("/{categoryId}")
    public ResponseEntity<APIResponseMessage> deleteCategory(@PathVariable("categoryId") String categoryId) {
        categoryService.delete(categoryId);
        APIResponseMessage deleteResponse = APIResponseMessage.builder().message("Category deleted successfully").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(deleteResponse, HttpStatus.OK);
    }


    @GetMapping
    public ResponseEntity<PageableResponse<CategoryDTO>> getAllCategories(@RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize, @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy, @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageableResponse<CategoryDTO> pageableResponse = categoryService.getAll(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }

    @GetMapping("/{categoryId}")
    public ResponseEntity<CategoryDTO> getSingleCategory(@PathVariable("categoryId") String categoryId) {
        CategoryDTO categoryDTO = categoryService.get(categoryId);
        return ResponseEntity.ok(categoryDTO);
    }


    @GetMapping("/search/{keyword}")
    public ResponseEntity<PageableResponse<CategoryDTO>> searchCategory(@RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize, @RequestParam(name = "sortBy", defaultValue = "title", required = false) String sortBy, @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir, @PathVariable("keyword") String keyword) {
        PageableResponse<CategoryDTO> list = categoryService.searchCategories(pageNumber, pageSize, sortBy, sortDir, keyword);
        logger.info("----* SEARCHED RESULTS *----");
        return new ResponseEntity<PageableResponse<CategoryDTO>>(list, HttpStatus.FOUND);
    }


    //    upload user image
    @PostMapping("/image/{categoryId}")
    public ResponseEntity<ImageResponseMessage> uploadCategoryImage(@RequestParam("categoryImage") MultipartFile image, @PathVariable("categoryId") String categoryId) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        CategoryDTO category = categoryService.get(categoryId);
        category.setCoverImage(imageName);
        CategoryDTO update = categoryService.update(category, categoryId);

        ImageResponseMessage imageResponseMessage = ImageResponseMessage.builder().imageName(imageName).message("Image uploaded successfully").success(true).status(HttpStatus.CREATED).build();
        return new ResponseEntity<>(imageResponseMessage, HttpStatus.CREATED);
    }


    //    server user image
    @GetMapping("/image/{categoryId}")
    public void serveCategoryImage(@PathVariable("categoryId") String categoryId, HttpServletResponse response) throws IOException {
        CategoryDTO category = categoryService.get(categoryId);
        logger.info("Category Image Name: {} " + category.getCoverImage());
        InputStream resource = fileService.getResource(imageUploadPath, category.getCoverImage());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

    }


    //create product with category
    @PostMapping("/{categoryId}/products")
    public ResponseEntity<ProductDTO> createProductWithCategory(@PathVariable("categoryId") String categoryId, @RequestBody ProductDTO productDTO) {
        ProductDTO productWithCategory = productService.createWithCategory(productDTO, categoryId);
        return new ResponseEntity<>(productWithCategory, HttpStatus.CREATED);
    }


    //update category of product
    @PutMapping("/{categoryId}/products/{productId}")

    public ResponseEntity<ProductDTO> updateCategoryOfProduct(@PathVariable("categoryId") String categoryId, @PathVariable("productId") String productId) {
        ProductDTO productDTO = productService.updateCategory(productId, categoryId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);
    }


    //get products of categories

    @GetMapping("/{categoryId}/products")
    public ResponseEntity<PageableResponse<ProductDTO>> getProductsOfCategory(@PathVariable("categoryId") String categoryId, @RequestParam(name = "pageNumber", defaultValue = "0", required = false) int pageNumber, @RequestParam(name = "pageSize", defaultValue = "10", required = false) int pageSize, @RequestParam(name = "sortBy", defaultValue = "title", required = false) String sortBy, @RequestParam(name = "sortDir", defaultValue = "asc", required = false) String sortDir) {
        PageableResponse<ProductDTO> pageableResponse = productService.getAllOfCategory(categoryId, pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);
    }


}
