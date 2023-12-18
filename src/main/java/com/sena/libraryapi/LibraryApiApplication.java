package com.sena.libraryapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;
import org.springframework.scheduling.annotation.EnableScheduling;

@SpringBootApplication
@ComponentScan(basePackages = "com.sena.libraryapi.*")
@EnableScheduling
public class LibraryApiApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryApiApplication.class);
	private final Environment enviroment;


	public LibraryApiApplication(Environment enviroment) {
		this.enviroment = enviroment;
	}

	public static void main(String[] args) {
		LOGGER.trace("Entrando na aplicação");
		SpringApplication.run(LibraryApiApplication.class, args);
		LOGGER.trace("Saindo da aplicação");
	}

	@Override
	public void run(String... args) throws Exception {

		LOGGER.info("---------------------------------------");
		LOGGER.info("Enviroment......: [ {} ]", enviroment);
		LOGGER.info("---------------------------------------");
	}

}
