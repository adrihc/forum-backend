package com.liceu.forum.forum.services;

import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.liceu.forum.forum.model.Login;
import com.liceu.forum.forum.model.Token;
import com.liceu.forum.forum.model.User;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

@Service
public class TokenService {
    @Autowired
    UserService userService;
    @Value("${token.secret}")
    String tokenSecret;
    public Token newToken(Login login){
        Instant now = Instant.now();
        Instant expiration = now.plusSeconds(10_000_000);
        String tokenValue = JWT.create()
                .withSubject(login.getEmail())
                .withExpiresAt(expiration)
                .sign(Algorithm.HMAC512(tokenSecret.getBytes(StandardCharsets.UTF_8)));
        Token token = new Token();
        token.setToken(tokenValue);
        token.setExpiration(expiration.toString());
        return token;
    }
    public String getUser(String token){
        String user= JWT.require(Algorithm.HMAC512(tokenSecret.getBytes()))
                .build()
                .verify(token)
                .getSubject();
        return user;
    }
    public User getUserObject(String token){
        String user= JWT.require(Algorithm.HMAC512(tokenSecret.getBytes()))
                .build()
                .verify(token)
                .getSubject();
        return userService.catchUserEmail(user).get(0);
    }
    public boolean isAdmin(String token){
        String userMail = getUser(token);
        User user = userService.catchUserEmail(userMail).get(0);
        if (user.getRole().equals("admin")){
            return true;
        } else {
            return false;
        }
    }
}
