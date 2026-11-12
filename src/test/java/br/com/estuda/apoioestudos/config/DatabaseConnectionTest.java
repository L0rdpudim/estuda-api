package br.com.estuda.apoioestudos.config;

import javax.sql.DataSource;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.TestPropertySource;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@SpringBootTest
@TestPropertySource(locations = "classpath:application-test.properties")
class DatabaseConnectionTest {

    @Autowired
    private DataSource dataSource;

    @Test
    @DisplayName("Deve verificar se a conexão com o banco de dados está ativa")
    void deveConectarAoBancoDeDados() {
        assertDoesNotThrow(() -> {
            try (var connection = dataSource.getConnection()) {
                assertThat(connection.isValid(2)).isTrue();
            }
        });
    }
}