package com.linku.backend;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@EnableScheduling
public class LinKuBackendApplication {

	public static void main(String[] args) {
		SpringApplication.run(LinKuBackendApplication.class, args);
	}

}
