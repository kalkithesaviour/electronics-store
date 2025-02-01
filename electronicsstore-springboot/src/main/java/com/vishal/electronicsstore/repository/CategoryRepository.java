package com.vishal.electronicsstore.repository;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vishal.electronicsstore.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    Page<Category> findByTitleContaining(String keyword, Pageable pageable);

}
