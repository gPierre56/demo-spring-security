package dev;

import java.util.Arrays;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.security.crypto.password.PasswordEncoder;

import dev.model.Utilisateur;
import dev.repository.IUtilisateurRepository;

@SpringBootApplication
public class DemoSpringSecurityApplication {

	@Autowired
	private IUtilisateurRepository repository;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@EventListener(ContextRefreshedEvent.class)
	public void init() {
		repository
				.save(new Utilisateur("u1", passwordEncoder.encode("pass1"), Arrays.asList("ROLE_ADMIN", "ROLE_USER")));
		repository.save(new Utilisateur("u2", passwordEncoder.encode("pass2"), Arrays.asList("ROLE_USER")));
	}

	public static void main(String[] args) {
		SpringApplication.run(DemoSpringSecurityApplication.class, args);

	}

}
