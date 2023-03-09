package com.liceu.forum.forum.services;

import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

@Service
public class Encrypter {
    private MessageDigest digest = null;

    public Encrypter() {
        try {
            digest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            System.err.println("Algorithm SHA-256 not found: "+e);
        }
    }

    public String SHA256(String word) {
        byte[] hash = digest.digest(word.getBytes(StandardCharsets.UTF_8));
        return new String(hash);
    }
}
