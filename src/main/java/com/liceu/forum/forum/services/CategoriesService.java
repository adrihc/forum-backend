package com.liceu.forum.forum.services;

import com.liceu.forum.forum.model.Categories;
import com.liceu.forum.forum.model.CategoryBody;
import com.liceu.forum.forum.repos.CategoriesRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
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

    public Categories createCategory(CategoryBody body) throws UnsupportedEncodingException {
        Categories category = new Categories();
        category.setTitle(body.getTitle());
        category.setDescription(body.getDescription());
        category.setSlug(createSlug(body.getTitle()));
        return category;
    }

    private String createSlug(String title) throws UnsupportedEncodingException {
        String slug = title.toLowerCase();
        String codificada = URLEncoder.encode(slug, "UTF-8").replaceAll("\\+", "_");
        return codificada;
    }
    public boolean tryExistanceCategory(String title){
        List<Categories> list = categoriesRepo.findByTitle(title);
        if (list.isEmpty()){
            return true;
        } else {
            return false;
        }
    }
    public Categories findBySlug(String slug){
        return categoriesRepo.findBySlug(slug).get(0);
    }
    public Categories findByTitle(String title){
        return categoriesRepo.findByTitle(title).get(0);
    }
    public boolean trySlug(String slug){
        List<Categories> list = categoriesRepo.findBySlug(slug);
        if (list.isEmpty()){
            return false;
        } else {
            return true;
        }
    }

    public void updateCategory(Categories category,CategoryBody body) throws UnsupportedEncodingException {
        category.setTitle(body.getTitle());
        category.setDescription(body.getDescription());
        String slug = createSlug(body.getTitle());
        category.setSlug(slug);
        categoriesRepo.save(category);
    }

    public void deleteCategory(String slug){
        Categories category = findBySlug(slug);
        categoriesRepo.delete(category);
    }
}
