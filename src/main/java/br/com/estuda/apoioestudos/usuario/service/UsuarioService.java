package br.com.estuda.apoioestudos.usuario.service;

import java.nio.charset.StandardCharsets;
import java.util.Locale;

import org.hibernate.exception.ConstraintViolationException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import br.com.estuda.apoioestudos.usuario.dto.AutenticacaoResponse;
import br.com.estuda.apoioestudos.usuario.dto.CadastroUsuarioRequest;
import br.com.estuda.apoioestudos.usuario.dto.LoginRequest;
import br.com.estuda.apoioestudos.usuario.entity.Usuario;
import br.com.estuda.apoioestudos.usuario.repository.UsuarioRepository;

@Service
public class UsuarioService {

    private final UsuarioRepository usuarioRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UsuarioService(
        UsuarioRepository usuarioRepository,
        PasswordEncoder passwordEncoder,
        JwtService jwtService
    ) {
        this.usuarioRepository = usuarioRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public AutenticacaoResponse cadastrar(CadastroUsuarioRequest request) {
        validarTamanhoSenha(request.senha());
        String email = normalizarEmail(request.email());

        if (usuarioRepository.existsByEmailIgnoreCase(email)) {
            throw new EmailJaCadastradoException();
        }

        Usuario usuario = new Usuario(
            request.nome().trim(),
            email,
            passwordEncoder.encode(request.senha()),
            request.tipoPerfil()
        );

        try {
            usuario = usuarioRepository.saveAndFlush(usuario);
        } catch (DataIntegrityViolationException exception) {
            if (violacaoDeEmailDuplicado(exception)) {
                throw new EmailJaCadastradoException();
            }
            throw exception;
        }

        return criarResposta(usuario);
    }

    @Transactional(readOnly = true)
    public AutenticacaoResponse autenticar(LoginRequest request) {
        validarTamanhoSenha(request.senha());
        Usuario usuario = usuarioRepository
            .findByEmailIgnoreCase(normalizarEmail(request.email()))
            .filter(Usuario::isAtivo)
            .filter(conta -> passwordEncoder.matches(request.senha(), conta.getSenhaHash()))
            .orElseThrow(CredenciaisInvalidasException::new);

        return criarResposta(usuario);
    }

    private AutenticacaoResponse criarResposta(Usuario usuario) {
        var perfil = usuario.getPerfil();
        return new AutenticacaoResponse(
            usuario.getId(),
            usuario.getNome(),
            usuario.getEmail(),
            perfil.getTipo(),
            perfil.getStatusValidacaoProfessor(),
            jwtService.gerarToken(usuario),
            "Bearer",
            jwtService.getValidadeEmSegundos()
        );
    }

    private String normalizarEmail(String email) {
        return email.trim().toLowerCase(Locale.ROOT);
    }

    private void validarTamanhoSenha(String senha) {
        if (senha.getBytes(StandardCharsets.UTF_8).length > 72) {
            throw new IllegalArgumentException(
                "A senha deve possuir no máximo 72 bytes em UTF-8."
            );
        }
    }

    private boolean violacaoDeEmailDuplicado(Throwable exception) {
        for (Throwable causa = exception; causa != null; causa = causa.getCause()) {
            if (causa instanceof ConstraintViolationException violacao
                && "uk_tb_usuario_email_ci".equals(violacao.getConstraintName())) {
                return true;
            }
        }
        return false;
    }
}
