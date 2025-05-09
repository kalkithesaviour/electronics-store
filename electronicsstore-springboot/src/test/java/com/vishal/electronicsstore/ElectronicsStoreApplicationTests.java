package com.vishal.electronicsstore;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.repository.UserRepository;
import com.vishal.electronicsstore.security.JwtHelper;

import io.github.cdimascio.dotenv.Dotenv;
import lombok.extern.slf4j.Slf4j;

@SpringBootTest
@Slf4j
class ElectronicsStoreApplicationTests {

	private final UserRepository userRepository;
	private final JwtHelper jwtHelper;

	@Autowired
	public ElectronicsStoreApplicationTests(
			UserRepository userRepository,
			JwtHelper jwtHelper) {
		this.userRepository = userRepository;
		this.jwtHelper = jwtHelper;
	}

	@Test
	void contextLoads() {
	}

	@BeforeAll
	static void loadEnv() {
		Dotenv dotenv = Dotenv.configure()
				.directory("../")
				.load();

		dotenv.entries().forEach(entry -> System.setProperty(entry.getKey(), entry.getValue()));
	}

	@Test
	void testToken() {
		User user = userRepository.findByEmail("vishal@gmail.com").get();
		String token = jwtHelper.generateToken(user);
		log.info("Generated JWT Token: " + token);
		log.info("Username from JWT: " + jwtHelper.getUsernameFromToken(token));
		log.info("Is token expired?: " + jwtHelper.isTokenExpired(token));
	}

}
