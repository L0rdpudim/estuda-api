package br.com.estuda.apoioestudos.usuario.entity;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class UsuarioPerfilTest {

    @Test
    @DisplayName("Perfil de estudante não possui status de validação de professor")
    void perfilDeEstudanteNaoPossuiStatusDeValidacao() {
        Usuario usuario = criarUsuario(TipoPerfil.ESTUDANTE);

        assertEquals(TipoPerfil.ESTUDANTE, usuario.getPerfil().getTipo());
        assertNull(usuario.getPerfil().getStatusValidacaoProfessor());
    }

    @Test
    @DisplayName("Perfil de professor começa com validação pendente")
    void perfilDeProfessorIniciaPendente() {
        Usuario usuario = criarUsuario(TipoPerfil.PROFESSOR);

        assertEquals(TipoPerfil.PROFESSOR, usuario.getPerfil().getTipo());
        assertEquals(
            StatusValidacaoProfessor.PENDENTE,
            usuario.getPerfil().getStatusValidacaoProfessor()
        );
    }

    @Test
    @DisplayName("Status de validação do professor pode ser atualizado")
    void statusDeValidacaoPodeSerAtualizado() {
        Usuario usuario = criarUsuario(TipoPerfil.PROFESSOR);

        usuario.getPerfil().atualizarStatusValidacaoProfessor(
            StatusValidacaoProfessor.VALIDADO
        );

        assertEquals(
            StatusValidacaoProfessor.VALIDADO,
            usuario.getPerfil().getStatusValidacaoProfessor()
        );
    }

    @Test
    @DisplayName("Perfil de estudante não aceita alteração de status de professor")
    void perfilDeEstudanteNaoAceitaStatusDeProfessor() {
        Usuario usuario = criarUsuario(TipoPerfil.ESTUDANTE);

        assertThrows(
            IllegalStateException.class,
            () -> usuario.getPerfil().atualizarStatusValidacaoProfessor(
                StatusValidacaoProfessor.VALIDADO
            )
        );
    }

    private Usuario criarUsuario(TipoPerfil tipoPerfil) {
        return new Usuario(
            "Usuário de teste",
            "usuario@estuda.com",
            "hash-da-senha",
            tipoPerfil
        );
    }
}
