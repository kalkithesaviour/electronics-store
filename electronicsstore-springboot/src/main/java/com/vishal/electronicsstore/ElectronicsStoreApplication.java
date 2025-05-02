package com.vishal.electronicsstore;

import java.util.List;
import java.util.UUID;

import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.vishal.electronicsstore.entity.Role;
import com.vishal.electronicsstore.entity.User;
import com.vishal.electronicsstore.repository.RoleRepository;
import com.vishal.electronicsstore.repository.UserRepository;

@SpringBootApplication
public class ElectronicsStoreApplication {

	public static void main(String[] args) {
		SpringApplication.run(ElectronicsStoreApplication.class, args);
	}

	@Bean
	public CommandLineRunner startupTasks(
			RoleRepository roleRepository,
			UserRepository userRepository,
			PasswordEncoder passwordEncoder) {
		return args -> {
			Role roleAdmin = roleRepository.findByRoleName("ROLE_ADMIN").orElse(null);
			if (roleAdmin == null) {
				roleAdmin = Role.builder()
						.roleId(UUID.randomUUID().toString())
						.roleName("ROLE_ADMIN")
						.build();
				roleRepository.save(roleAdmin);
			}

			Role roleUser = roleRepository.findByRoleName("ROLE_USER").orElse(null);
			if (roleUser == null) {
				roleUser = Role.builder()
						.roleId(UUID.randomUUID().toString())
						.roleName("ROLE_USER")
						.build();
				roleRepository.save(roleUser);
			}

			User user = userRepository.findByEmail("vishal@gmail.com").orElse(null);
			if (user == null) {
				user = User.builder()
						.fullName("vishal")
						.email("vishal@gmail.com")
						.password(passwordEncoder.encode("vishal"))
						.roles(List.of(roleAdmin))
						.userId(UUID.randomUUID().toString())
						.build();
				userRepository.save(user);
			}
		};
	}

}
