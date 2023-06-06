package com.liceu.forum.forum.repos;

import com.liceu.forum.forum.model.Categories;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface CategoriesRepo extends JpaRepository<Categories, Long> {
    List<Categories> findByTitle(String title);
    List<Categories> findBySlug(String slug);
}
