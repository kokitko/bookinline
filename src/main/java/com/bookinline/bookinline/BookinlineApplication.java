package com.bookinline.bookinline;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

@SpringBootApplication
@EnableCaching
public class BookinlineApplication {

	public static void main(String[] args) {
		SpringApplication.run(BookinlineApplication.class, args);
	}

}
