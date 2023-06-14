package ru.itis.zheleznov;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;

@SpringBootApplication
@EnableJpaRepositories
public class ZheleznovDaDiplomApplication {

	public static void main(String[] args) {
		SpringApplication.run(ZheleznovDaDiplomApplication.class, args);
	}

}
