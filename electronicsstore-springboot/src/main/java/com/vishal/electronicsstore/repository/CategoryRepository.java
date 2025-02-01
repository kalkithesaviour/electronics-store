package com.vishal.electronicsstore.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.vishal.electronicsstore.entity.Category;

@Repository
public interface CategoryRepository extends JpaRepository<Category, String> {

    List<Category> findByTitleContaining(String keyword);

}
