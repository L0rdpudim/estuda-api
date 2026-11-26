package br.com.estuda.apoioestudos.usuario.controller;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import br.com.estuda.apoioestudos.usuario.dto.AutenticacaoResponse;
import br.com.estuda.apoioestudos.usuario.dto.CadastroUsuarioRequest;
import br.com.estuda.apoioestudos.usuario.dto.LoginRequest;
import br.com.estuda.apoioestudos.usuario.dto.UsuarioAutenticadoResponse;
import br.com.estuda.apoioestudos.usuario.service.UsuarioAutenticado;
import br.com.estuda.apoioestudos.usuario.service.UsuarioService;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class UsuarioController {

    private final UsuarioService usuarioService;

    public UsuarioController(UsuarioService usuarioService) {
        this.usuarioService = usuarioService;
    }

    @PostMapping("/cadastro")
    public ResponseEntity<AutenticacaoResponse> cadastrar(
        @Valid @RequestBody CadastroUsuarioRequest request
    ) {
        return ResponseEntity.status(HttpStatus.CREATED)
            .body(usuarioService.cadastrar(request));
    }

    @PostMapping("/login")
    public AutenticacaoResponse autenticar(@Valid @RequestBody LoginRequest request) {
        return usuarioService.autenticar(request);
    }

    @GetMapping("/me")
    public UsuarioAutenticadoResponse usuarioAtual(
        @AuthenticationPrincipal UsuarioAutenticado usuario,
        Authentication authentication
    ) {
        List<String> permissoes = authentication
            .getAuthorities()
            .stream()
            .map(authority -> authority.getAuthority())
            .toList();

        return new UsuarioAutenticadoResponse(
            usuario.id(),
            usuario.email(),
            usuario.tipoPerfil(),
            usuario.statusValidacaoProfessor(),
            permissoes
        );
    }
}
