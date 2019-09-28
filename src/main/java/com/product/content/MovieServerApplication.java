package com.product.content;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableConfigurationProperties
public class MovieServerApplication {

	public static void main(String[] args) {
		SpringApplication.run(MovieServerApplication.class, args);
	}

}
