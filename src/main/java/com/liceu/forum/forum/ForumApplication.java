package com.liceu.forum.forum;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@SpringBootApplication
public class ForumApplication implements WebMvcConfigurer {

	public static void main(String[] args) {
		SpringApplication.run(ForumApplication.class, args);
	}

}
