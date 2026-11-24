package br.com.estuda.apoioestudos.usuario.dto;

import br.com.estuda.apoioestudos.usuario.entity.StatusValidacaoProfessor;
import br.com.estuda.apoioestudos.usuario.entity.TipoPerfil;

public record AutenticacaoResponse(
    Long usuarioId,
    String nome,
    String email,
    TipoPerfil tipoPerfil,
    StatusValidacaoProfessor statusValidacaoProfessor,
    String token,
    String tipoToken,
    long expiraEmSegundos
) {
}
