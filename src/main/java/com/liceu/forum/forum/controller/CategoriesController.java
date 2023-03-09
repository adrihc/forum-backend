package com.liceu.forum.forum.controller;

import com.liceu.forum.forum.model.*;
import com.liceu.forum.forum.services.CategoriesService;
import com.liceu.forum.forum.services.TokenService;
import com.liceu.forum.forum.services.UserService;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.io.UnsupportedEncodingException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class CategoriesController {
    @Autowired
    CategoriesService categoriesService;
    @Autowired
    TokenService tokenService;
    @Autowired
    UserService userService;

    @GetMapping("/categories")
    @CrossOrigin
    public List<Categories> getCategories(){
        return categoriesService.getAllCategories();
    }

    @PostMapping("/categories")
    @CrossOrigin
    public ResponseEntity<Categories> postCategories(@RequestBody CategoryBody body) throws UnsupportedEncodingException, URISyntaxException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        if (tokenService.isAdmin(token)){
            if (categoriesService.tryExistanceCategory(body.getTitle())){
                Categories category = categoriesService.createCategory(body);
                categoriesService.saveCategorie(category);
                return ResponseEntity.created(new URI("/categories/"+category.getSlug())).body(category);
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
            }
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }

    @GetMapping("/categories/{slug}")
    @CrossOrigin
    public ResponseEntity<Categories> getCategory(@PathVariable String slug) throws UnsupportedEncodingException {
        String encodedSlug = URLEncoder.encode(slug, "UTF-8").replaceAll("\\+", "_");
        if (categoriesService.trySlug(encodedSlug)){
            Categories category = categoriesService.findBySlug(encodedSlug);
            return ResponseEntity.ok(category);
        } else {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }
    @PutMapping("/categories/{slug}")
    @CrossOrigin
    public ResponseEntity<Categories> updateCategory(@PathVariable String slug,@RequestBody CategoryBody body) throws UnsupportedEncodingException {
        String encodedSlug = URLEncoder.encode(slug, "UTF-8").replaceAll("\\+", "_");
        Categories category = categoriesService.findBySlug(encodedSlug);
        categoriesService.updateCategory(category,body);
        return ResponseEntity.ok(category);
    }

    @DeleteMapping("/categories/{slug}")
    @CrossOrigin
    public ResponseEntity<Void> deleteCategory(@PathVariable String slug) throws UnsupportedEncodingException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        String userString = tokenService.getUser(token);
        User user = userService.catchUserEmail(userString).get(0);
        if (!user.getRole().equals("admin")){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        String encodedSlug = URLEncoder.encode(slug, "UTF-8").replaceAll("\\+", "_");
        categoriesService.deleteCategory(encodedSlug);
        return ResponseEntity.ok().build();
    }


}