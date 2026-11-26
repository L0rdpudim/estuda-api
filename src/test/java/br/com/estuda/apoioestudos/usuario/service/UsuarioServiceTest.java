package br.com.estuda.apoioestudos.usuario.service;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.sql.SQLException;
import java.util.Optional;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.test.util.ReflectionTestUtils;
import org.springframework.dao.DataIntegrityViolationException;
import org.hibernate.exception.ConstraintViolationException;

import br.com.estuda.apoioestudos.usuario.dto.CadastroUsuarioRequest;
import br.com.estuda.apoioestudos.usuario.dto.LoginRequest;
import br.com.estuda.apoioestudos.usuario.entity.StatusValidacaoProfessor;
import br.com.estuda.apoioestudos.usuario.entity.TipoPerfil;
import br.com.estuda.apoioestudos.usuario.entity.Usuario;
import br.com.estuda.apoioestudos.usuario.repository.UsuarioRepository;
import org.springframework.security.crypto.password.PasswordEncoder;

@ExtendWith(MockitoExtension.class)
class UsuarioServiceTest {

    @Mock
    private UsuarioRepository usuarioRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtService jwtService;

    @InjectMocks
    private UsuarioService usuarioService;

    @Test
    @DisplayName("Cadastra professor com senha em hash e perfil pendente")
    void cadastrarProfessorArmazenaHashEIniciaPendente() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha-segura"))
            .thenReturn("$2a$12$hash-ficticio");
        when(jwtService.gerarToken(any())).thenReturn("token-assinado");
        when(jwtService.getValidadeEmSegundos()).thenReturn(3600L);
        when(usuarioRepository.saveAndFlush(any(Usuario.class))).thenAnswer(invocacao -> {
            Usuario usuario = invocacao.getArgument(0);
            ReflectionTestUtils.setField(usuario, "id", 42L);
            return usuario;
        });

        var resposta = usuarioService.cadastrar(new CadastroUsuarioRequest(
            "Ana Professora",
            " ANA@EXAMPLE.COM ",
            "senha-segura",
            TipoPerfil.PROFESSOR
        ));

        ArgumentCaptor<Usuario> usuarioCaptor = ArgumentCaptor.forClass(Usuario.class);
        verify(usuarioRepository).saveAndFlush(usuarioCaptor.capture());
        assertThat(usuarioCaptor.getValue().getEmail()).isEqualTo("ana@example.com");
        assertThat(usuarioCaptor.getValue().getSenhaHash()).isEqualTo("$2a$12$hash-ficticio");
        assertThat(usuarioCaptor.getValue().getSenhaHash()).isNotEqualTo("senha-segura");
        assertThat(resposta.usuarioId()).isEqualTo(42L);
        assertThat(resposta.statusValidacaoProfessor())
            .isEqualTo(StatusValidacaoProfessor.PENDENTE);
        assertThat(resposta.tipoToken()).isEqualTo("Bearer");
    }

    @Test
    @DisplayName("Não cadastra novamente um e-mail existente")
    void cadastroRejeitaEmailDuplicado() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(true);

        assertThatThrownBy(() -> usuarioService.cadastrar(new CadastroUsuarioRequest(
            "Ana",
            "ana@example.com",
            "senha-segura",
            TipoPerfil.ESTUDANTE
        ))).isInstanceOf(EmailJaCadastradoException.class);

        verify(usuarioRepository, never()).saveAndFlush(any());
    }

    @Test
    @DisplayName("Converte conflito concorrente no índice único do e-mail em 409")
    void cadastroTrataConflitoConcorrenteDeEmail() {
        when(usuarioRepository.existsByEmailIgnoreCase("ana@example.com")).thenReturn(false);
        when(passwordEncoder.encode("senha-segura")).thenReturn("$2a$12$hash-ficticio");
        var violacao = new ConstraintViolationException(
            "email duplicado",
            new SQLException("duplicate key"),
            "uk_tb_usuario_email_ci"
        );
        when(usuarioRepository.saveAndFlush(any(Usuario.class)))
            .thenThrow(new DataIntegrityViolationException("constraint", violacao));

        assertThatThrownBy(() -> usuarioService.cadastrar(new CadastroUsuarioRequest(
            "Ana",
            "ana@example.com",
            "senha-segura",
            TipoPerfil.ESTUDANTE
        ))).isInstanceOf(EmailJaCadastradoException.class);
    }

    @Test
    @DisplayName("Autentica e emite token para credenciais válidas")
    void autenticarComCredenciaisValidas() {
        Usuario usuario = new Usuario(
            "Ana",
            "ana@example.com",
            "$2a$12$hash-ficticio",
            TipoPerfil.ESTUDANTE
        );
        ReflectionTestUtils.setField(usuario, "id", 7L);
        when(usuarioRepository.findByEmailIgnoreCase("ana@example.com"))
            .thenReturn(Optional.of(usuario));
        when(passwordEncoder.matches("senha-segura", usuario.getSenhaHash())).thenReturn(true);
        when(jwtService.gerarToken(any())).thenReturn("token-assinado");
        when(jwtService.getValidadeEmSegundos()).thenReturn(3600L);

        var resposta = usuarioService.autenticar(
            new LoginRequest("ANA@example.com", "senha-segura")
        );

        assertThat(resposta.usuarioId()).isEqualTo(7L);
        assertThat(resposta.token()).isEqualTo("token-assinado");
        verify(passwordEncoder).matches("senha-segura", usuario.getSenhaHash());
    }

    @Test
    @DisplayName("Retorna o mesmo erro para usuário inexistente e senha incorreta")
    void autenticarComCredenciaisInvalidas() {
        when(usuarioRepository.findByEmailIgnoreCase("ana@example.com"))
            .thenReturn(Optional.empty());

        assertThatThrownBy(() -> usuarioService.autenticar(
            new LoginRequest("ana@example.com", "senha-incorreta")
        )).isInstanceOf(CredenciaisInvalidasException.class);
    }

    @Test
    @DisplayName("Rejeita senha que excede o limite seguro do BCrypt")
    void rejeitaSenhaMaiorQueLimiteBcrypt() {
        String senhaLonga = "a".repeat(73);

        assertThatThrownBy(() -> usuarioService.cadastrar(new CadastroUsuarioRequest(
            "Ana",
            "ana@example.com",
            senhaLonga,
            TipoPerfil.ESTUDANTE
        ))).isInstanceOf(IllegalArgumentException.class);

        verify(usuarioRepository, never()).existsByEmailIgnoreCase(any());
    }
}
