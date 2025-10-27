package dev.m0b1.vrsvp;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
@SpringBootApplication
public class Main {

	static void main(String[] args) {
		SpringApplication.run(Main.class, args);
	}

}
