package com.liceu.forum.forum.controller;

import com.liceu.forum.forum.model.Categories;
import com.liceu.forum.forum.model.Token;
import com.liceu.forum.forum.services.CategoriesService;
import com.liceu.forum.forum.services.TokenService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.util.List;
import java.util.Map;

@RestController
public class CategoriesController {
    @Autowired
    CategoriesService categoriesService;

    @Autowired
    TokenService tokenService;
    @GetMapping("/categories")
    @CrossOrigin
    public List<Categories> getCategories(){
        return categoriesService.getAllCategories();
    }

    @PostMapping("/categories")
    @CrossOrigin
    public Map<String, Object> postCategories(){
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");

        if (tokenService.isAdmin(token)){
            Categories category = new Categories();
        }
        return null;
    }
}
