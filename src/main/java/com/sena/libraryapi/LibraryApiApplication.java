package com.sena.libraryapi;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.env.Environment;

@SpringBootApplication
@ComponentScan(basePackages = "com.sena.libraryapi.*")
public class LibraryApiApplication implements CommandLineRunner {

	private static final Logger LOGGER = LoggerFactory.getLogger(LibraryApiApplication.class);

	@Autowired
	private Environment enviroment;

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
