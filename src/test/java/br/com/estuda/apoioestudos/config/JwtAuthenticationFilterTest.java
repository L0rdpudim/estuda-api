package br.com.estuda.apoioestudos.config;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.mock.web.MockFilterChain;
import org.springframework.mock.web.MockHttpServletRequest;
import org.springframework.mock.web.MockHttpServletResponse;
import org.springframework.security.core.context.SecurityContextHolder;

import br.com.estuda.apoioestudos.usuario.service.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;

class JwtAuthenticationFilterTest {

    private final JwtService jwtService = org.mockito.Mockito.mock(JwtService.class);
    private final JwtAuthenticationFilter filter = new JwtAuthenticationFilter(jwtService);

    @AfterEach
    void limparContexto() {
        SecurityContextHolder.clearContext();
    }

    @Test
    @DisplayName("Professor pendente autentica como estudante, sem permissão docente")
    void professorPendenteRecebeSomentePermissaoDeEstudante() throws Exception {
        Claims claims = claims("PROFESSOR", "PENDENTE");
        when(jwtService.validarToken("token-pendente")).thenReturn(claims);
        var request = requestComToken("token-pendente");
        var response = new MockHttpServletResponse();

        filter.doFilter(request, response, new MockFilterChain());

        assertThat(response.getStatus()).isEqualTo(200);
        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_ESTUDANTE");
        assertThat(SecurityContextHolder.getContext().getAuthentication().getPrincipal())
            .hasFieldOrPropertyWithValue("id", 10L);
    }

    @Test
    @DisplayName("Professor validado recebe permissões de estudante e professor")
    void professorValidadoRecebePermissaoDocente() throws Exception {
        Claims claims = claims("PROFESSOR", "VALIDADO");
        when(jwtService.validarToken("token-validado")).thenReturn(claims);
        var response = new MockHttpServletResponse();

        filter.doFilter(
            requestComToken("token-validado"),
            response,
            new MockFilterChain()
        );

        assertThat(SecurityContextHolder.getContext().getAuthentication().getAuthorities())
            .extracting("authority")
            .containsExactly("ROLE_ESTUDANTE", "ROLE_PROFESSOR");
    }

    @Test
    @DisplayName("Token inválido interrompe a requisição com 401")
    void tokenInvalidoRetornaUnauthorized() throws Exception {
        when(jwtService.validarToken("token-invalido"))
            .thenThrow(new io.jsonwebtoken.JwtException("inválido"));
        var response = new MockHttpServletResponse();
        var chain = new MockFilterChain();

        filter.doFilter(requestComToken("token-invalido"), response, chain);

        assertThat(response.getStatus()).isEqualTo(401);
        assertThat(chain.getRequest()).isNull();
        verify(jwtService).validarToken("token-invalido");
    }

    private MockHttpServletRequest requestComToken(String token) {
        var request = new MockHttpServletRequest();
        request.addHeader("Authorization", "Bearer " + token);
        return request;
    }

    private Claims claims(String perfil, String status) {
        return Jwts.claims()
            .subject("10")
            .add("email", "professor@example.com")
            .add("perfil", perfil)
            .add("statusProfessor", status)
            .build();
    }
}
