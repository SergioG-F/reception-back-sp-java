package com.pe.recepcion;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

import java.util.TimeZone;

@SpringBootApplication
public class RecepcionApplication {
	@PostConstruct
	public void init() {
		TimeZone.setDefault(TimeZone.getTimeZone("America/Caracas"));
	}
	public static void main(String[] args) {
		SpringApplication.run(RecepcionApplication.class, args);
	}

}
