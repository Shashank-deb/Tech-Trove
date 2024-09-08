package com.shashank.electronic.store.services.impl;

import com.shashank.electronic.store.models.Category;
import com.shashank.electronic.store.dtos.CategoryDTO;
import com.shashank.electronic.store.dtos.PageableResponse;
import com.shashank.electronic.store.exceptions.ResourceNotFoundException;
import com.shashank.electronic.store.helper.Helper;
import com.shashank.electronic.store.repositories.CategoryRepository;
import com.shashank.electronic.store.services.CategoryService;
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
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;


@Service
public class CategoryServiceImpl implements CategoryService {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private ModelMapper mapper;

    @Value("${user.category.image.path}")
    private String imagePath;


    private Logger logger = LoggerFactory.getLogger(CategoryServiceImpl.class);

    @Override
    public CategoryDTO create(CategoryDTO categoryDTO) {
//        creating category id randomly 
        String categoryId = UUID.randomUUID().toString();
        categoryDTO.setCategoryId(categoryId);
        Category category = mapper.map(categoryDTO, Category.class);
        Category savedCategory = categoryRepository.save(category);
        CategoryDTO savedCategoryDTO = mapper.map(savedCategory, CategoryDTO.class);
        return savedCategoryDTO;
    }

    @Override
    public CategoryDTO update(CategoryDTO categoryDTO, String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found exception"));
        category.setTitle(categoryDTO.getTitle());
        category.setDescription(categoryDTO.getDescription());
        category.setCoverImage(categoryDTO.getCoverImage());
        Category updatedCategory = categoryRepository.save(category);
        return mapper.map(updatedCategory, CategoryDTO.class);
    }

    @Override
    public void delete(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found exception"));
        //delete user profile image
        //images/user/abc.png
        String fullPath = imagePath + category.getCoverImage();

        try {
            Path path = Paths.get(fullPath);
            Files.delete(path);
        } catch (NoSuchFileException ex) {
            logger.info("User image not found in folder");
            ex.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }


        categoryRepository.delete(category);

    }

    @Override
    public PageableResponse<CategoryDTO> getAll(
            int pageNumber,
            int pageSize,
            String sortBy,
            String sortDir) {
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findAll(pageable);
        PageableResponse<CategoryDTO> response = Helper.getPageableResponse(page, CategoryDTO.class);
        return response;
    }

    @Override
    public CategoryDTO get(String categoryId) {
        Category category = categoryRepository.findById(categoryId).orElseThrow(() -> new ResourceNotFoundException("Category not found exception"));
        return mapper.map(category, CategoryDTO.class);
    }

    @Override
    public PageableResponse<CategoryDTO> searchCategories(int pageNumber, int pageSize, String sortBy, String sortDir, String keyword) {
        // TODO Auto-generated method stub
        Sort sort = (sortDir.equalsIgnoreCase("desc")) ? (Sort.by(sortBy).descending()) : (Sort.by(sortBy).ascending());
        Pageable pageable = PageRequest.of(pageNumber, pageSize, sort);
        Page<Category> page = categoryRepository.findByTitleContaining(keyword, pageable);
        PageableResponse<CategoryDTO> pageableResponse = Helper.getPageableResponse(page, CategoryDTO.class);
        return pageableResponse;
    }



}
