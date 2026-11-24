package br.com.estuda.apoioestudos.usuario.service;

import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.time.Instant;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import br.com.estuda.apoioestudos.usuario.entity.Usuario;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

@Service
public class JwtService {

    private final SecretKey chave;
    private final Duration validade;

    public JwtService(
        @Value("${app.jwt.secret}") String segredo,
        @Value("${app.jwt.expiration:PT1H}") Duration validade
    ) {
        byte[] bytes = segredo.getBytes(StandardCharsets.UTF_8);
        if (bytes.length < 32) {
            throw new IllegalArgumentException(
                "A configuração app.jwt.secret deve possuir ao menos 32 bytes."
            );
        }

        if (validade.isZero() || validade.isNegative()) {
            throw new IllegalArgumentException(
                "A configuração app.jwt.expiration deve ser positiva."
            );
        }

        this.chave = Keys.hmacShaKeyFor(bytes);
        this.validade = validade;
    }

    public String gerarToken(Usuario usuario) {
        Instant agora = Instant.now();
        var perfil = usuario.getPerfil();

        var builder = Jwts.builder()
            .subject(usuario.getId().toString())
            .claim("email", usuario.getEmail())
            .claim("perfil", perfil.getTipo().name())
            .issuedAt(Date.from(agora))
            .expiration(Date.from(agora.plus(validade)));

        if (perfil.getStatusValidacaoProfessor() != null) {
            builder.claim(
                "statusProfessor",
                perfil.getStatusValidacaoProfessor().name()
            );
        }

        return builder.signWith(chave).compact();
    }

    public Claims validarToken(String token) {
        return Jwts.parser()
            .verifyWith(chave)
            .build()
            .parseSignedClaims(token)
            .getPayload();
    }

    public long getValidadeEmSegundos() {
        return validade.toSeconds();
    }
}
