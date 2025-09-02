package com.dreamzlancer.cucumbersample;

import lombok.extern.log4j.Log4j2;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@Log4j2
@SpringBootApplication
public class CucumbersampleApplication {

	public static void main(String[] args) {

		log.info("Starting DreamzLancer Cucumber Sample Application...");
		SpringApplication.run(CucumbersampleApplication.class, args);
		log.info("Application started successfully!");
	}

}
