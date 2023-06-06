package com.liceu.forum.forum.controller;


import com.liceu.forum.forum.model.*;
import com.liceu.forum.forum.services.Encrypter;
import com.liceu.forum.forum.services.TokenService;
import com.liceu.forum.forum.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@RestController
public class UserController {
    @Autowired
    UserService userService;
    @Autowired
    TokenService tokenService;
    @Autowired
    Encrypter encrypter;
    public UserController(TokenService tokenService){
        this.tokenService = tokenService;
    }

    @GetMapping("/getprofile")
    @CrossOrigin("http://localhost:3000/")
    public Map<String, Object> selectProfile() {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        Map<String, Object> resp = new HashMap<>();
        String user = tokenService.getUser(token);
        List<User> userList = userService.catchUserEmail(user);
        if (userList.isEmpty()){
            resp.put("error", "No se ha encontrado un usuario");
            return resp;
        }
        User profileUser = userList.get(0);
        //List<String> permissions = userService.getPermisions(profileUser.getRole());

        resp.put("token", token);
        resp.put("email", profileUser.getEmail());
        resp.put("name", profileUser.getName());
        resp.put("permissions", userService.createPermissions(profileUser));

        return resp;
    }

    @PostMapping("/register")
    @CrossOrigin("http://localhost:3000/")
    public Map<String, Object> createUser(@RequestBody User user, HttpServletResponse response) throws NoSuchAlgorithmException {
        List<User> userlistEmail = userService.catchUserEmail(user.getEmail());
        Map<String, Object> resp = new HashMap<>();
        if (userlistEmail.isEmpty()){
            user.setPassword(encrypter.SHA256(user.getPassword()));
            userService.save(user);
            return resp;
        } else if(!userlistEmail.isEmpty()){
            resp.put("message", "Ese email ya está en uso");
            response.setStatus(400);
            return resp;
        } else{
            response.setStatus(400);
            return resp;
        }
    }


    @PostMapping("/login")
    @CrossOrigin("http://localhost:3000/")
    public Map<String, Object>  createUser(@RequestBody Login login, HttpServletResponse response) throws NoSuchAlgorithmException {
        List<User> userList = userService.catchUserEmail(login.getEmail());
        Map<String, Object> resp = new HashMap<>();

        if(userList.isEmpty()){
            String message = "User does not exists";
            resp.put("message", message);
            response.setStatus(400);
            return resp;
        }
        Token token = tokenService.newToken(login);
        User user = userList.get(0);
        if (!user.getPassword().equals(encrypter.SHA256(login.getPassword()))){
            String message = "Wrong password";
            resp.put("message", message);
            response.setStatus(400);
            return resp;
        }

        Map<String, Object> permissions = userService.createPermissions(user);
        UserResp userResp = new UserResp(user.getRole(),user.getId().toString(),user.getEmail(),user.getName());
        userResp.setPermissions(permissions);
        resp.put("token", token.getToken());
        resp.put("user",userResp);
        return resp;
    }

    @PutMapping("/profile/password")
    @CrossOrigin("http://localhost:3000/")
    public Map<String, Object> changePassword(@RequestBody Password password, HttpServletResponse response) throws NoSuchAlgorithmException {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        User user = tokenService.getUserObject(token);
        String encodedPassword = encrypter.SHA256(password.getCurrentPassword());
        Map <String, Object> resp = new HashMap<>();
        if (!encodedPassword.equals(user.getPassword())){
            response.setStatus(400);
            resp.put("message", "No se ha podido actualizar");
            return resp;
        }
        userService.updatePassword(user,password);
        String message = "Tu contraseña se ha actualizado";
        resp.put("message", message);
        return resp;
    }

    @PutMapping("/profile")
    @CrossOrigin("http://localhost:3000/")
    public Map<String, Object> changeProfile(@RequestBody ProfileBody body, HttpServletResponse response) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        User user = tokenService.getUserObject(token);
        UserResp userResp = new UserResp(user.getRole(), user.getId().toString(), user.getEmail(), user.getName());
        userResp.setPermissions(userService.createPermissions(user));
        Map<String, Object> resp = new HashMap<>();

        resp.put("user",userResp);
        if (body.getEmail().equals(user.getEmail())){
            userService.updateProfile(user, body);
            Login login = new Login();
            login.setEmail(body.getEmail());
            login.setPassword(user.getPassword());
            Token newToken = tokenService.newToken(login);
            resp.put("token", newToken.getToken());
            resp.put("message", "Utiliza un email diferente");
            return resp;
        } else if(!userService.tryEmailExistance(body.getEmail())) {
            resp.put("message", "Ese email ya existe");
            response.setStatus(400);
            return resp;
        } else {
            userService.updateProfile(user, body);
            Login login = new Login();
            login.setEmail(body.getEmail());
            login.setPassword(user.getPassword());
            Token newToken = tokenService.newToken(login);
            resp.put("token", newToken.getToken());
            return resp;
        }
    }
}