package com.liceu.forum.forum.controller;


import com.liceu.forum.forum.model.*;
import com.liceu.forum.forum.services.Encrypter;
import com.liceu.forum.forum.services.TokenService;
import com.liceu.forum.forum.services.UserService;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

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
    @CrossOrigin
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
        List<String> permissions = userService.getPermisions(profileUser.getRole());

        resp.put("token", token);
        resp.put("email", profileUser.getEmail());
        resp.put("name", profileUser.getName());
        resp.put("permissions", userService.createPermissions(profileUser));

        return resp;
    }

    @PostMapping("/register")
    @CrossOrigin
    public ResponseEntity<String> createUser(@RequestBody User user) {
        List<User> userlistEmail = userService.catchUserEmail(user.getEmail());

        if (userlistEmail.isEmpty()){
            user.setPassword(encrypter.SHA256(user.getPassword()));
            userService.save(user);
            return ResponseEntity.ok("Tu cuenta ha sido creada");
        } else if(!userlistEmail.isEmpty()){
            return ResponseEntity.status(HttpStatus.CONFLICT).body("El email ya existe");
        } else{
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
    }


    @PostMapping("/login")
    @CrossOrigin
    public ResponseEntity<Token> createUser(@RequestBody Login login) {
        List<User> userList = userService.catchUserEmail(login.getEmail());
        if(userList.isEmpty()){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        Token token = tokenService.newToken(login);
        User user = userList.get(0);
        if (!user.getPassword().equals(encrypter.SHA256(login.getPassword()))){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        return ResponseEntity.ok().body(token);
    }

    @PutMapping("/profile/password")
    @CrossOrigin
    public ResponseEntity<String> changePassword(@RequestBody Password password) {
        System.out.println(password.getNewPassword()+password.getCurrentPassword());
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        User user = tokenService.getUserObject(token);
        String encodedPassword = encrypter.SHA256(password.getCurrentPassword());
        if (!encodedPassword.equals(user.getPassword())){
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).build();
        }
        userService.updatePassword(user,password);
        return ResponseEntity.ok().body("Tu contrase??a se ha actualizado");
    }

    @PutMapping("/profile")
    @CrossOrigin
    public ResponseEntity<Token> changeProfile(@RequestBody ProfileBody body) {
        HttpServletRequest request = ((ServletRequestAttributes) RequestContextHolder.getRequestAttributes()).getRequest();
        String token = request.getHeader("Authorization").replace("Bearer ","");
        User user = tokenService.getUserObject(token);

        if (body.getEmail().equals(user.getEmail())){
            userService.updateProfile(user, body);
            Login login = new Login();
            login.setEmail(body.getEmail());
            login.setPassword(user.getPassword());
            Token newToken = tokenService.newToken(login);
            return ResponseEntity.ok().body(newToken);
        } else if(!userService.tryEmailExistance(body.getEmail())) {
            return ResponseEntity.status(HttpStatus.CONFLICT).build();
        } else {
            userService.updateProfile(user, body);
            Login login = new Login();
            login.setEmail(body.getEmail());
            login.setPassword(user.getPassword());
            Token newToken = tokenService.newToken(login);
            return ResponseEntity.ok().body(newToken);
        }
    }
}
