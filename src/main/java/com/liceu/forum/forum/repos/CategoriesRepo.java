package com.liceu.forum.forum.repos;

import com.liceu.forum.forum.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CategoriesRepo extends JpaRepository<Categories, Long> {
}
