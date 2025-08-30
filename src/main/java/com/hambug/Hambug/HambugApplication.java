package com.hambug.Hambug;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;

@SpringBootApplication
@EnableJpaAuditing
public class HambugApplication {

	public static void main(String[] args) {
		SpringApplication.run(HambugApplication.class, args);
	}

}
