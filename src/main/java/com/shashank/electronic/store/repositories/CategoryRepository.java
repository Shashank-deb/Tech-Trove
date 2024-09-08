package com.shashank.electronic.store.repositories;

import com.shashank.electronic.store.dtos.CategoryDTO;
import com.shashank.electronic.store.models.Category;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, String> {

    Page<Category> findByTitleContaining(String keyword, Pageable pageable);

}
