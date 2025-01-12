package com.challengeAlura;

import com.challengeAlura.main.Principal;
import com.challengeAlura.repository.AutorRepo;
import com.challengeAlura.repository.LibrosRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

@SpringBootApplication
public class ChallengeAluraApplication implements CommandLineRunner {

	@Autowired
	private LibrosRepo librosRepo;

	@Autowired
	private AutorRepo autorRepo;

	public static void main(String[] args) {
		SpringApplication.run(ChallengeAluraApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		Principal app = new Principal(librosRepo, autorRepo);
		app.iniciarPrograma();
	}
}
