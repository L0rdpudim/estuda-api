package br.com.estuda.apoioestudos;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationContext;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

import br.com.estuda.apoioestudos.config.JwtAuthenticationFilter;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
@ActiveProfiles("test")
class ApoioEstudosApiApplicationTests {

	@Autowired
	private ApplicationContext applicationContext;

	@Test
	void contextLoads() {
	}

	@Test
	void jwtFilterIsOnlyRegisteredInsideSecurityChain() {
		assertThat(applicationContext.getBeansOfType(JwtAuthenticationFilter.class))
			.isEmpty();
	}

}
