package com.liceu.forum.forum.services;

import com.liceu.forum.forum.model.Password;
import com.liceu.forum.forum.model.User;
import com.liceu.forum.forum.repos.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class UserService {
    @Autowired
    UserRepo userRepo;
    public List<User> catchUserEmail(String email) {
        List<User> userList = userRepo.findByEmail(email);
        return userList;
    }
    public void save(User user){
        userRepo.save(user);
    }
    public void updatePassword(User user, Password password){
        userRepo.updatePassword(user.getEmail(),password.getNewPassword());
    }
    public List<String> getPermisions(String role){
        List<String> root = new ArrayList<>();

        switch (role){
            case "admin":
                root.add("own_topics:write");
                root.add("own_topics:delete");
                root.add("own_replies:write");
                root.add("own_replies:delete");
                root.add("categories:write");
                root.add("categories:delete");
                break;
            case "user":
                root.add("own_topics:write");
                root.add("own_topics:delete");
                root.add("own_replies:write");
                root.add("own_replies:delete");
                break;
        }
        return root;
    }
    public Map<String, Object> createPermissions(User user){
        Map<String, Object> permissions = new HashMap<>();
        permissions.put("root", getPermisions(user.getRole()));
        permissions.put("role", user.getRole());

        return permissions;
    }
}
