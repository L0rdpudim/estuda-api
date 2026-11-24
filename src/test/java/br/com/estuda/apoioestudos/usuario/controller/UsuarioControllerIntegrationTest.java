package br.com.estuda.apoioestudos.usuario.controller;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import static org.assertj.core.api.Assertions.assertThat;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.estuda.apoioestudos.usuario.entity.StatusValidacaoProfessor;
import br.com.estuda.apoioestudos.usuario.repository.UsuarioRepository;

@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
@Transactional
@Import(UsuarioControllerIntegrationTest.TestRoutes.class)
class UsuarioControllerIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @Autowired
    private UsuarioRepository usuarioRepository;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Test
    @DisplayName("Cadastro e login emitem token e impõem permissões de professor validado")
    void cadastroLoginEPermissoesPorStatusProfessor() throws Exception {
        String cadastro = mockMvc.perform(post("/api/v1/auth/cadastro")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "nome": "Professor de teste",
                      "email": "professor-auth@example.com",
                      "senha": "senha-segura-123",
                      "tipoPerfil": "PROFESSOR"
                    }
                    """))
            .andExpect(status().isCreated())
            .andExpect(jsonPath("$.statusValidacaoProfessor").value("PENDENTE"))
            .andExpect(jsonPath("$.senha").doesNotExist())
            .andReturn()
            .getResponse()
            .getContentAsString();
        String tokenPendente = campoJson(cadastro, "token");
        var usuarioCadastrado = usuarioRepository
            .findByEmailIgnoreCase("professor-auth@example.com")
            .orElseThrow();
        assertThat(usuarioCadastrado.getSenhaHash()).isNotEqualTo("senha-segura-123");
        assertThat(passwordEncoder.matches(
            "senha-segura-123",
            usuarioCadastrado.getSenhaHash()
        )).isTrue();

        mockMvc.perform(get("/api/v1/auth/me")
                .header("Authorization", "Bearer " + tokenPendente))
            .andExpect(status().isOk())
            .andExpect(jsonPath("$.permissoes").value(org.hamcrest.Matchers.contains(
                "ROLE_ESTUDANTE"
            )));

        mockMvc.perform(get("/api/v1/professores/test-access")
                .header("Authorization", "Bearer " + tokenPendente))
            .andExpect(status().isForbidden());

        mockMvc.perform(get("/api/v1/estudantes/test-access")
                .header("Authorization", "Bearer " + tokenPendente))
            .andExpect(status().isOk())
            .andExpect(content().string("permitido"));

        var usuario = usuarioRepository.findByEmailIgnoreCase("professor-auth@example.com")
            .orElseThrow();
        usuario.getPerfil().atualizarStatusValidacaoProfessor(
            StatusValidacaoProfessor.VALIDADO
        );
        usuarioRepository.saveAndFlush(usuario);

        String login = mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {
                      "email": "PROFESSOR-auth@example.com",
                      "senha": "senha-segura-123"
                    }
                    """))
            .andExpect(status().isOk())
            .andReturn()
            .getResponse()
            .getContentAsString();
        String tokenValidado = campoJson(login, "token");

        mockMvc.perform(get("/api/v1/professores/test-access")
                .header("Authorization", "Bearer " + tokenValidado))
            .andExpect(status().isOk())
            .andExpect(content().string("permitido"));
    }

    @Test
    @DisplayName("Rejeita credenciais inválidas e impede acesso sem token")
    void rejeitaCredenciaisInvalidasEAcessoAnonimo() throws Exception {
        mockMvc.perform(post("/api/v1/auth/login")
                .contentType(MediaType.APPLICATION_JSON)
                .content("""
                    {"email": "inexistente@example.com", "senha": "senha-segura"}
                    """))
            .andExpect(status().isUnauthorized());

        mockMvc.perform(get("/api/v1/auth/me"))
            .andExpect(status().isUnauthorized());
    }

    private String campoJson(String json, String campo) throws Exception {
        return new com.fasterxml.jackson.databind.ObjectMapper()
            .readTree(json)
            .get(campo)
            .asText();
    }

    @TestConfiguration
    static class TestRoutes {

        @Bean
        TestAccessController testAccessController() {
            return new TestAccessController();
        }
    }

    @RestController
    static class TestAccessController {

        @GetMapping("/api/v1/professores/test-access")
        @PreAuthorize("hasRole('PROFESSOR')")
        String recursoDeProfessor() {
            return "permitido";
        }

        @GetMapping("/api/v1/estudantes/test-access")
        @PreAuthorize("hasRole('ESTUDANTE')")
        String recursoDeEstudante() {
            return "permitido";
        }
    }
}
