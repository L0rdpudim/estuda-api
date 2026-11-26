package br.com.estuda.apoioestudos.usuario.service;

import br.com.estuda.apoioestudos.usuario.entity.StatusValidacaoProfessor;
import br.com.estuda.apoioestudos.usuario.entity.TipoPerfil;

public record UsuarioAutenticado(
    Long id,
    String email,
    TipoPerfil tipoPerfil,
    StatusValidacaoProfessor statusValidacaoProfessor
) {
}
