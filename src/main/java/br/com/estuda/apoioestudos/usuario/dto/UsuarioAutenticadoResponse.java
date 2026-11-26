package br.com.estuda.apoioestudos.usuario.dto;

import java.util.List;

import br.com.estuda.apoioestudos.usuario.entity.StatusValidacaoProfessor;
import br.com.estuda.apoioestudos.usuario.entity.TipoPerfil;

public record UsuarioAutenticadoResponse(
    Long usuarioId,
    String email,
    TipoPerfil tipoPerfil,
    StatusValidacaoProfessor statusValidacaoProfessor,
    List<String> permissoes
) {
}
