package com.shashank.electronic.store.controllers;


import com.shashank.electronic.store.dtos.*;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.models.Product;
import com.shashank.electronic.store.repositories.ProductRepository;
import com.shashank.electronic.store.services.FileService;
import com.shashank.electronic.store.services.ProductService;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.servlet.http.HttpServletResponse;
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
import java.nio.file.Files;
import java.nio.file.NoSuchFileException;
import java.nio.file.Path;
import java.nio.file.Paths;


@RestController
@RequestMapping("/products")
@Tag(name = "ProductController", description = "REST APIs related to perform product operations")

//@CrossOrigin(origins="http://localhost:4200")
public class ProductController {
    @Autowired
    private ProductService productService;


    @Autowired
    private FileService fileService;


    @Value("${user.product.image.path}")
    private String imageUploadPath;


    private Logger logger = LoggerFactory.getLogger(ProductController.class);

    @PostMapping
    public ResponseEntity<ProductDTO> createProduct(@RequestBody ProductDTO productDTO) {
        ProductDTO createdProduct = productService.createProduct(productDTO);
        return new ResponseEntity<>(createdProduct, HttpStatus.CREATED);
    }


    @PutMapping("/{productId}")
    public ResponseEntity<ProductDTO> updateProduct(@PathVariable("productId") String productId,
                                                    @RequestBody ProductDTO productDTO
    ) {
        ProductDTO updatedProduct = productService.updateProduct(productDTO, productId);
        return new ResponseEntity<>(updatedProduct, HttpStatus.OK);
    }


    @DeleteMapping("/{productId}")
    public ResponseEntity<APIResponseMessage> deleteProduct(@PathVariable("productId") String productId) {
        productService.deleteProduct(productId);
        APIResponseMessage responseMessage = APIResponseMessage.builder().message("Product deleted successfully").success(true).status(HttpStatus.OK).build();
        return new ResponseEntity<>(responseMessage, HttpStatus.OK);
    }


    @GetMapping("/{productId}")
    public ResponseEntity<ProductDTO> getProduct(@PathVariable("productId") String productId) {
        ProductDTO productDTO = productService.getProductById(productId);
        return new ResponseEntity<>(productDTO, HttpStatus.OK);

    }


    @GetMapping
    public ResponseEntity<PageableResponse<ProductDTO>> getAllProducts(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<ProductDTO> pageableResponse = productService.getAllProducts(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);

    }


    @GetMapping("/live")
    public ResponseEntity<PageableResponse<ProductDTO>> getAllLive(
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<ProductDTO> pageableResponse = productService.getAllLive(pageNumber, pageSize, sortBy, sortDir);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);

    }


    @GetMapping("/search/{query}")
    public ResponseEntity<PageableResponse<ProductDTO>> searchProduct(
            @PathVariable("query") String query,
            @RequestParam(value = "pageNumber", defaultValue = "0", required = false) int pageNumber,
            @RequestParam(value = "pageSize", defaultValue = "10", required = false) int pageSize,
            @RequestParam(value = "sortBy", defaultValue = "title", required = false) String sortBy,
            @RequestParam(value = "sortDir", defaultValue = "asc", required = false) String sortDir
    ) {
        PageableResponse<ProductDTO> pageableResponse = productService.searchByTitle(pageNumber, pageSize, sortBy, sortDir, query);
        return new ResponseEntity<>(pageableResponse, HttpStatus.OK);

    }


    //    upload user image
    @PostMapping("/image/{productId}")
    public ResponseEntity<ImageResponseMessage> uploadProductImage(
            @RequestParam("productImage") MultipartFile image,
            @PathVariable("productId") String productId
    ) throws IOException {
        String imageName = fileService.uploadImage(image, imageUploadPath);
        ProductDTO product = productService.getProductById(productId);
        product.setProductImageName(imageName);
        ProductDTO update = productService.updateProduct(product, productId);

        ImageResponseMessage imageResponseMessage = ImageResponseMessage
                .builder()
                .imageName(imageName)
                .message("Image uploaded successfully")
                .success(true)
                .status(HttpStatus.CREATED)
                .build();
        return new ResponseEntity<>(imageResponseMessage, HttpStatus.CREATED);
    }


    //    server user image
    @GetMapping("/image/{productId}")
    public void serveProductImage(
            @PathVariable("productId") String productId,
            HttpServletResponse response
    ) throws IOException {
        ProductDTO product = productService.getProductById(productId);
        logger.info("Product Image Name: {} " + product.getProductImageName());
        InputStream resource = fileService.getResource(imageUploadPath, product.getProductImageName());
        response.setContentType(MediaType.IMAGE_JPEG_VALUE);
        StreamUtils.copy(resource, response.getOutputStream());

    }


}
