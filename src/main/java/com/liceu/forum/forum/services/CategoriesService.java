package com.liceu.forum.forum.services;

import com.liceu.forum.forum.model.Categories;
import com.liceu.forum.forum.repos.CategoriesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CategoriesService {
    @Autowired
    CategoriesRepo categoriesRepo;

    public List<Categories> getAllCategories(){
        return categoriesRepo.findAll();
    }

    public void saveCategorie(Categories categories){
        categoriesRepo.save(categories);
    }
}
