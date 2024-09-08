package com.shashank.electronic.store.services;

import com.shashank.electronic.store.dtos.CategoryDTO;
import com.shashank.electronic.store.dtos.PageableResponse;

import java.util.List;

public interface CategoryService {

    CategoryDTO create(CategoryDTO categoryDTO);

    CategoryDTO update(CategoryDTO categoryDTO, String categoryId);

    void delete(String categoryId);


    PageableResponse<CategoryDTO> getAll(int pageNumber, int pageSize, String sortBy, String sortDir);


    CategoryDTO get(String categoryId);


    // search categories
    PageableResponse<CategoryDTO> searchCategories(int pageNumber, int pageSize, String sortBy, String sortDir, String keyword);

}
