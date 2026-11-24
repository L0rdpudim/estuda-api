package br.com.estuda.apoioestudos;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.security.autoconfigure.UserDetailsServiceAutoConfiguration;

@SpringBootApplication(exclude = UserDetailsServiceAutoConfiguration.class)
public class ApoioEstudosApiApplication {

	public static void main(String[] args) {
		SpringApplication.run(ApoioEstudosApiApplication.class, args);
	}

}
