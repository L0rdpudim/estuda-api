package br.com.estuda.apoioestudos.config;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import br.com.estuda.apoioestudos.usuario.entity.StatusValidacaoProfessor;
import br.com.estuda.apoioestudos.usuario.entity.TipoPerfil;
import br.com.estuda.apoioestudos.usuario.service.JwtService;
import br.com.estuda.apoioestudos.usuario.service.UsuarioAutenticado;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtService jwtService;

    public JwtAuthenticationFilter(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    @Override
    protected void doFilterInternal(
        HttpServletRequest request,
        HttpServletResponse response,
        FilterChain filterChain
    ) throws ServletException, IOException {
        String header = request.getHeader("Authorization");
        if (header == null || !header.startsWith("Bearer ")) {
            filterChain.doFilter(request, response);
            return;
        }

        try {
            Claims claims = jwtService.validarToken(header.substring(7));
            TipoPerfil perfil = TipoPerfil.valueOf(claims.get("perfil", String.class));
            String statusClaim = claims.get("statusProfessor", String.class);
            StatusValidacaoProfessor status = statusClaim == null
                ? null
                : StatusValidacaoProfessor.valueOf(statusClaim);
            UsuarioAutenticado principal = new UsuarioAutenticado(
                Long.valueOf(claims.getSubject()),
                claims.get("email", String.class),
                perfil,
                status
            );

            List<SimpleGrantedAuthority> permissoes = new ArrayList<>();
            permissoes.add(new SimpleGrantedAuthority("ROLE_ESTUDANTE"));
            if (perfil == TipoPerfil.PROFESSOR
                && status == StatusValidacaoProfessor.VALIDADO) {
                permissoes.add(new SimpleGrantedAuthority("ROLE_PROFESSOR"));
            }

            var authentication = new UsernamePasswordAuthenticationToken(
                principal,
                null,
                permissoes
            );
            SecurityContextHolder.getContext().setAuthentication(authentication);
        } catch (JwtException | IllegalArgumentException exception) {
            SecurityContextHolder.clearContext();
            response.sendError(HttpServletResponse.SC_UNAUTHORIZED, "Token inválido ou expirado.");
            return;
        }

        filterChain.doFilter(request, response);
    }
}
